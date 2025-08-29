package cfks.bugjump.ghosteclipse.relay;

import cfks.bugjump.ghosteclipse.AndroidApplication;
import cfks.bugjump.ghosteclipse.util.ContextUtils;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import dev.sora.relay.session.listener.xbox.XboxDeviceInfo;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import kotlin.Pair;

public class AccountManager {

  private static final String KEY_CURRENT_MICROSOFT_REFRESH_TOKEN = "MICROSOFT_REFRESH_TOKEN";

  public static final List<Account> accounts = new ArrayList<>();
  private static String currentRefreshToken;

  public static String getCurrentRefreshToken() {
    return ContextUtils.readString(AndroidApplication.getInstance(), KEY_CURRENT_MICROSOFT_REFRESH_TOKEN).isEmpty() ? null : ContextUtils.readString(AndroidApplication.getInstance(), KEY_CURRENT_MICROSOFT_REFRESH_TOKEN);
  }

  public static void setCurrentRefreshToken(String value) {
    ContextUtils.writeString(AndroidApplication.getInstance(), KEY_CURRENT_MICROSOFT_REFRESH_TOKEN, value != null ? value : "");
  }

  public static Account getCurrentAccount() {
    String token = getCurrentRefreshToken();
    return token != null ? accounts.stream().filter(account -> account.refreshToken.equals(token)).findFirst().orElse(null) : null;
  }

  public static void setCurrentAccount(Account value) {
    if (value == null) {
      setCurrentRefreshToken(null);
    } else if (accounts.contains(value)) {
      setCurrentRefreshToken(value.refreshToken);
    }
  }

  private static final File storeFile = new File(AndroidApplication.getInstance().getFilesDir(), "credentials.json");
  private static final Gson gson = new GsonBuilder()
    .registerTypeAdapter(XboxDeviceInfo.class, new DeviceInfoAdapter())
    .create();

  static {
    load();
  }

  public static void load() {
    accounts.clear();
    if (!storeFile.exists()) {
      setCurrentRefreshToken(null);
      return;
    }
    try {
      Account[] loadedAccounts = gson.fromJson(new FileReader(storeFile), Account[].class);
      for (Account account: loadedAccounts) {
        accounts.add(account);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    cleanupCurrentRefreshToken();
  }

  public static void save() {
    try {
      FileWriter writer = new FileWriter(storeFile);
      gson.toJson(accounts.toArray(new Account[0]), writer);
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void cleanupCurrentRefreshToken() {
    String current = getCurrentRefreshToken();
    for (Account account: accounts) {
      if (account.refreshToken.equals(current)) {
        return;
      }
    }
    setCurrentRefreshToken(null);
  }

  private static class DeviceInfoAdapter implements JsonSerializer < XboxDeviceInfo > , JsonDeserializer < XboxDeviceInfo > {

    @Override
    public JsonElement serialize(XboxDeviceInfo src, Type typeOf, JsonSerializationContext ctx) {
      return new JsonPrimitive(src.getDeviceType());
    }

    @Override
    public XboxDeviceInfo deserialize(JsonElement json, Type typeOf, JsonDeserializationContext ctx) {
      return XboxDeviceInfo.devices.get(json.getAsString()) != null ? XboxDeviceInfo.devices.get(json.getAsString()) : XboxDeviceInfo.Companion.getDEVICE_ANDROID();
    }
  }

  public static class Account {
    @SerializedName("remark")
    private String remark;
    @SerializedName("device")
    private XboxDeviceInfo platform;
    @SerializedName("refresh_token")
    private String refreshToken;

    public Account(String remark, XboxDeviceInfo platform, String refreshToken) {
      this.remark = remark;
      this.platform = platform;
      this.refreshToken = refreshToken;
    }

    public String refresh() {
      boolean isCurrent = AccountManager.getCurrentAccount() == this;
      Pair<String, String> tokens = platform.refreshToken(refreshToken);
      String accessToken = tokens.getFirst();
      refreshToken = tokens.getSecond();
      if (isCurrent) {
        AccountManager.setCurrentAccount(this);
      }
      AccountManager.save();
      return accessToken;
    }
    
    public String getRemark(){
        return this.remark;
    }
    
    public void setRemark(String remark){
        this.remark = remark;
    }
    
    @Override
    public String toString() {
        // TODO: Implement this method
        return "Account={username=" + this.remark + ",platform=" + this.platform.getDeviceType() + ",refreshToken=" + this.refreshToken + "}";
    }
    
    public String toString(boolean hideRefreshToken) {
        // TODO: Implement this method
        return "Account={username=" + this.remark + ",platform=" + this.platform.getDeviceType() + ",refreshToken=***}";
    }
  }
}
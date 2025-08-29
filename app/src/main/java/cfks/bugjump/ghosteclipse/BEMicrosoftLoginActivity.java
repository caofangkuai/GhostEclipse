package cfks.bugjump.ghosteclipse;

import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import cfks.bugjump.ghosteclipse.databinding.ActivityBeMicrosoftLoginBinding;
import cfks.bugjump.ghosteclipse.relay.AccountManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.dialogx.dialogs.PopTip;
import dev.sora.relay.session.listener.xbox.RelayListenerXboxLogin;
import dev.sora.relay.session.listener.xbox.XboxDeviceInfo;
import dev.sora.relay.session.listener.xbox.XboxGamerTagException;
import dev.sora.relay.session.listener.xbox.cache.XboxIdentityToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import jiesheng.应用操作;
import jiesheng.文件操作;
import kotlin.Pair;
import okhttp3.HttpUrl;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;

public class BEMicrosoftLoginActivity extends AppCompatActivity {
    private ActivityBeMicrosoftLoginBinding binding;
    private XboxDeviceInfo device;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBeMicrosoftLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ImmersionBar.with(this)
        .transparentBar()
        .fullScreen(true)
        .fitsSystemWindows(false)
        .hideBar(BarHide.FLAG_HIDE_BAR)
        .init();
        
        CookieManager.getInstance().removeAllCookies(null);
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.setWebViewClient(new CustomWebViewClient());
        String deviceType = getIntent().getStringExtra("channel");
        PopTip.show("DeviceType: " + deviceType).iconSuccess();
        device = XboxDeviceInfo.devices.get(deviceType);
        binding.webview.loadUrl("https://login.live.com/oauth20_authorize.srf?client_id=" + device.component1() + "&redirect_uri=https://login.live.com/oauth20_desktop.srf&response_type=code&scope=service::user.auth.xboxlive.com::MBI_SSL");
    }
    
    private class CustomWebViewClient extends WebViewClient{
        private Pair<String,String> account = null;
        
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            // TODO: Implement this method
            if(account != null && Optional.ofNullable(request.getUrl().getScheme()).orElse("").startsWith("ms-xal")){
                new Thread(()->{
                    try{
                        showLoadingPage("Verifying your credentials...");
                        XboxIdentityToken identityToken = RelayListenerXboxLogin.Companion.fetchIdentityToken(account.getFirst(),device);
                        showLoadingPage("Almost done...");
                        String username = getUsernameFromChain(readText(RelayListenerXboxLogin.Companion.fetchRawChain(identityToken.component1(),EncryptionUtils.createKeyPair().getPublic())));
                        AccountManager.accounts.add(new AccountManager.Account(username,device,account.getSecond()));
                        AccountManager.save();
                        back(true);
                    }catch(Throwable t){
                        runOnUiThread(()->{
                            binding.webview.loadData(t.toString(),"text/html", "UTF-8");
                        });
                    }
                }).start();
                return true;
            }
            HttpUrl url = HttpUrl.parse(request.getUrl().toString());
            if (url == null) {
                return false;
            }
            if (!url.host().equals("login.live.com") || !url.encodedPath().equals("/oauth20_desktop.srf")) {
				if (url.queryParameter("res").equals("cancel")) {
					PopTip.show("action cancelled").iconError();
                    back(false);
					return false;
				}
                PopTip.show("invalid url " + request.getUrl().toString()).iconError();
                return false;
            }
            String authCode = url.queryParameter("code");
            if (authCode == null || authCode.isEmpty()) {
                return false;
            }
            showLoadingPage("Setting up your account...");
            new Thread(()->{
                try{
                    Pair<String,String> tokens = device.refreshToken(authCode);
                    showLoadingPage("Authenticating with Xbox...");
                    try{
                        XboxIdentityToken identityToken = RelayListenerXboxLogin.Companion.fetchIdentityToken(tokens.getFirst(),device);
                        showLoadingPage("Retrieving your profile...");
                        String username = getUsernameFromChain(readText(RelayListenerXboxLogin.Companion.fetchRawChain(identityToken.getToken(), EncryptionUtils.createKeyPair().getPublic())));
                        AccountManager.Account account = new AccountManager.Account(username,device,tokens.getSecond());
                        while (AccountManager.accounts.stream().map(AccountManager.Account::getRemark).collect(Collectors.toList()).contains(account.getRemark())) {
                           account.setRemark(account.getRemark() + new Random().nextInt(10));
                        }
                        AccountManager.accounts.add(account);
                        AccountManager.save();
                        back(true);
                    }catch(XboxGamerTagException e){
                        account = new Pair<>(tokens.getFirst(),tokens.getSecond());
                        runOnUiThread(()->{
                            binding.webview.loadUrl(e.getSisuStartUrl());
                        });
                    }
                 }catch(Throwable t){
                     runOnUiThread(()->{
                         binding.webview.loadData(t.toString(),"text/html", "UTF-8");
                     });
                 }
            }).start();
            return true;
        }
    
        private String getUsernameFromChain(String chains) {
            JsonObject root = JsonParser.parseString(chains).getAsJsonObject();
            JsonArray body = root.getAsJsonArray("chain");
            for (int i = 0; i < body.size(); i++) {
                String chain = body.get(i).getAsString();
                String[] parts = chain.split("\\.");
                if (parts.length < 2) continue;   
                String decoded = new String(java.util.Base64.getDecoder().decode(parts[1]), StandardCharsets.UTF_8);
                JsonObject chainBody = JsonParser.parseString(decoded).getAsJsonObject();
                if (chainBody.has("extraData")) {
                    JsonObject extraData = chainBody.getAsJsonObject("extraData");
                    return extraData.get("displayName").getAsString();
                }
            }
            throw new RuntimeException("no username found");
        }
    }
    
    private void showLoadingPage(String title){
        runOnUiThread(()->{
            String data = 文件操作.读入资源文件(this,"MicrosoftLoginLoading.html").replace("[title]",title);
            binding.webview.loadData(Base64.encodeToString(data.getBytes(), Base64.DEFAULT),"text/html; charset=UTF-8", "base64");
        });
    }
    
    public String readText(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(System.lineSeparator()); // 保留换行符
        }
        return stringBuilder.toString();
    }
    
    private void back(boolean ok){
        Utils.toActivity(this,MainActivity.class);
    }
    
    @Override
    public boolean onKeyDown(int arg0, KeyEvent arg1) {
        // TODO: Implement this method
        if(arg0 == KeyEvent.KEYCODE_BACK){
            Utils.toActivity(this,MainActivity.class);
            return true;
        }
        return super.onKeyDown(arg0, arg1);
    }
}
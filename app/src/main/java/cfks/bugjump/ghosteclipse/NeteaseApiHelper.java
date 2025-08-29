package cfks.bugjump.ghosteclipse;

import android.util.Pair;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import jiesheng.加解密操作;
import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import okhttp3.*;
import java.net.URLEncoder;
import java.util.*;

// 注:此类方法中所有的String userId, String userToken均为执行者的
//均为异步http请求,请放新线程执行
public class NeteaseApiHelper {
  private static final String UA = "com.netease.x19/840272725 NeteaseMobileGame/a5.9.0 (OPPO-A5;29)";
  public static final Map < Integer, String > ErrCodeDICT = new HashMap < Integer, String > () {
    {
      put(-123456, "Response Data is Null");
      put(-18, "网络异常（服务器提交了协议冲突）");
      put(-17, "网络异常（无法从传输连接中读取数据: 连接已关闭）");
      put(-16, "网络异常（基础连接已经关闭: 接收时发生错误）");
      put(-15, "网络异常（基础连接已经关闭: 发送时发生错误）");
      put(-14, "网络异常（无法连接到远程服务器）");
      put(-13, "网络异常（未能解析此远程名称）");
      put(-12, "网络异常（请求被中止: 未能创建 SSL/TLS 安全通道）");
      put(-11, "网络异常（操作超时）");
      put(-10, "网络异常（请求超时）");
      put(-4, "[JsonDeserialize]");
      put(0, "成功");
      put(1, "用户名或密码错误");
      put(2, "无法识别的请求");
      put(3, "URS验证不通过，具体内容参考data数据");
      put(4, "参数为空");
      put(5, "昵称格式不合法");
      put(6, "用户不存在");
      put(7, "昵称暂不支持修改");
      put(8, "昵称已存在");
      put(9, "包含敏感词");
      put(10, "网易登录已失效");
      put(11, "实名认证失败");
      put(12, "参数错误");
      put(13, "操作失败");
      put(14, "参数超出长度范围");
      put(17, "只允许使用中英文字符和数字");
      put(21, "您的账号尚未获得测试资格，请激活后再来登录。");
      put(22, "您的账号在另一处登录。");
      put(23, "目前服务器尚未开启，请于正式开服后再尝试登录。");
      put(41, "批量查询数量过多");
      put(29, "账号已被封禁！");
      put(100, "上传失败");
      put(200, "凭证过期");
      put(201, "凭证错误");
      put(300, "套餐不存在");
      put(301, "套餐获取失败");
      put(302, "服务器不存在");
      put(303, "服务器租赁时间过长");
      put(304, "服务器启动中");
      put(305, "未初始化的服务器数量达到上限");
      put(306, "Realms错误");
      put(311, "用户在黑名单");
      put(307, "已经初始化");
      put(310, "获取服务器信息失败");
      put(308, "切换世界失败");
      put(309, "不是服主");
      put(312, "请稍候重试");
      put(313, "您所能获取的租赁服数量达到上限");
      put(314, "游戏世界服务器已关闭");
      put(315, "服务器人数已满，请稍候重试！");
      put(316, "存档尚未生成，请至少进入一次游戏，生成存当后再备份世界");
      put(802, "角色删除后24小时后才能再删除其他角色");
      put(-300, "服务器状态异常！");
      put(-1, "未知错误");
      put(-6, "请求服务器返回超时");
      put(400, "未知绿宝石事件");
      put(401, "没有加上绿宝石");
      put(402, "达到次数上限");
      put(403, "等级不够");
      put(20000, "群组数量已达到上限");
      put(20001, "群人数已达上限");
      put(20002, "邀请已失效");
      put(20003, "操作失败,请稍后重试");
      put(20004, "您的权限不足");
      put(20005, "创建群组数量已达上限");
      put(20006, "名称长度超过可用上限");
      put(20007, "名称内包含敏感词");
      put(20008, "解散失败，距离上次解散还未满15天");
      put(20009, "该群已被解散");
    }
  };

  private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
  private static final MediaType FORM_MEDIA_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

  private static OkHttpClient client;
  private static JSONObject serverUrlList;

  static {
    // 初始化OkHttpClient，配置连接超时和缓存
    client = new OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .build();
  }
    
  public static String getErrStr(int code) {
  	return ErrCodeDICT.get(code);
  }

  // 初始化,第一次使用前执行
  // 参数说明:[isg79]是否为g79 api,否则为x19 api(g79是PE端,x19是PC端)
  public static void init(boolean isg79) {
    String g79 = "https://g79.update.netease.com/serverlist/adr_release.0.17.json";
    String x19 = "https://x19.update.netease.com/serverlist/release.json";
    Request request = new Request.Builder()
      .url(isg79 ? g79 : x19)
      .get()
      .build();
    try{
        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            String responseBody = response.body().string();
            serverUrlList = new JSONObject(responseBody);
        }
    }catch(Exception e){
        e.printStackTrace();
    }
  }

  // 创建基础请求构建器
  private static Request.Builder createBaseRequestBuilder(String userId, String userToken) {
    return new Request.Builder()
      .addHeader("User-Agent", UA)
      .addHeader("Content-Type", "application/json")
      .addHeader("user-token", userToken)
      .addHeader("user-id", userId);
  }

  // 获取API网关URL
  private static String getApiGatewayUrl() throws JSONException {
    if (serverUrlList == null) {
      throw new IllegalStateException("Server URL list not initialized. Call init() first.");
    }
    return serverUrlList.getString("ApiGatewayUrl");
  }

  // 获取Web服务器URL
  private static String getWebServerUrl() throws JSONException {
    if (serverUrlList == null) {
      throw new IllegalStateException("Server URL list not initialized. Call init() first.");
    }
    return serverUrlList.getString("WebServerUrl");
  }

  // 获取租赁服信息(ip,端口)
  public static JSONObject getRentalServerInfo(String userId, String userToken, String serverId) throws Exception {
    JSONObject bodyJSON = new JSONObject();
    try {
      bodyJSON.put("pwd", "");
      bodyJSON.put("server_id", serverId);
    } catch (JSONException e) {
      // 记录错误但不中断流程
      System.err.println("Error creating JSON body: " + e.getMessage());
    }

    RequestBody body = RequestBody.create(bodyJSON.toString(), JSON_MEDIA_TYPE);
    Request request = createBaseRequestBuilder(userId, userToken)
      .url(getApiGatewayUrl() + "/rental-server-world-enter/get")
      .post(body)
      .build();

    Response response = client.newCall(request).execute();
    return response.isSuccessful() ? new JSONObject(response.body().string()) : null;
  }

  // 通过游戏内显示的服务器号搜索租赁服(用于获取server_id)
  public static JSONObject searchRentalServerByCode(String userId, String userToken, String code) throws Exception {
    JSONObject bodyJSON = new JSONObject();
    try {
      bodyJSON.put("offset", 0);
      bodyJSON.put("server_name", code);
    } catch (JSONException e) {
      System.err.println("Error creating JSON body: " + e.getMessage());
    }

    RequestBody body = RequestBody.create(bodyJSON.toString(), JSON_MEDIA_TYPE);
    Request request = createBaseRequestBuilder(userId, userToken)
      .url(getApiGatewayUrl() + "/rental-server/query/search-by-name")
      .post(body)
      .build();

    Response response = client.newCall(request).execute();
    return response.isSuccessful() ? new JSONObject(response.body().string()) : null;
  }

  // 通过游戏内显示的房间号搜索联机大厅房间
  public static JSONObject searchOnlineLobbyRoomByCode(String userId, String userToken, String code) throws Exception {
    JSONObject bodyJSON = new JSONObject();
    try {
      bodyJSON.put("length", 10);
      bodyJSON.put("version", "1.21.0");
      bodyJSON.put("res_id", "");
      bodyJSON.put("keyword", code);
      bodyJSON.put("offset", 0);
    } catch (JSONException e) {
      System.err.println("Error creating JSON body: " + e.getMessage());
    }

    RequestBody body = RequestBody.create(bodyJSON.toString(), JSON_MEDIA_TYPE);
    Request request = createBaseRequestBuilder(userId, userToken)
      .url(getApiGatewayUrl() + "/online-lobby-room/query/search-by-name-v2")
      .post(body)
      .build();

    Response response = client.newCall(request).execute();
    return response.isSuccessful() ? new JSONObject(response.body().string()) : null;
  }

  // 获取加入的群组列表
  public static JSONObject getGroupsList(String userId, String userToken) throws Exception {
    RequestBody body = RequestBody.create("{}", JSON_MEDIA_TYPE);
    Request request = createBaseRequestBuilder(userId, userToken)
      .url(getApiGatewayUrl() + "/get-all-groups/")
      .post(body)
      .build();

    Response response = client.newCall(request).execute();
    return response.isSuccessful() ? new JSONObject(response.body().string()) : null;
  }

  // 获取好友列表
  public static JSONObject getFriendsList(String userId, String userToken) throws Exception {
    RequestBody body = RequestBody.create("{\"with_game_state\": false, \"with_dynamic_head_img\": 1}", JSON_MEDIA_TYPE);
    Request request = createBaseRequestBuilder(userId, userToken)
      .url(getWebServerUrl() + "/user-allfriends")
      .post(body)
      .build();

    Response response = client.newCall(request).execute();
    return response.isSuccessful() ? new JSONObject(response.body().string()) : null;
  }

  // 获取公告
  public static String getNotice() throws Exception {
    Request request = new Request.Builder()
      .url("https://g79.update.netease.com/game_notice/g79_notice_netease")
      .get()
      .build();

    Response response = client.newCall(request).execute();
    return response.isSuccessful() ? response.body().string() : null;
  }

  // 获取游戏内提示
  public static JSONObject getTips() throws Exception {
    Request request = new Request.Builder()
      .url("https://g79.update.netease.com/clienttips/release/setting.json")
      .get()
      .build();

    Response response = client.newCall(request).execute();
    return response.isSuccessful() ? new JSONObject(response.body().string()) : null;
  }

  // 通过uid获取多个玩家信息
  public static JSONObject getPlayerInfoByIds(String userId, String userToken, String[] ids) throws Exception {
    StringBuilder idsBuilder = new StringBuilder();
    for (int i = 0; i < ids.length; i++) {
      if (i > 0) {
        idsBuilder.append(";");
      }
      idsBuilder.append(ids[i]);
    }

    String requestBody = "{\"is_need_recharge_benefit\": 0, \"uids\": \"" + idsBuilder.toString() + "\"}";
    Request request = createBaseRequestBuilder(userId, userToken)
      .url(getApiGatewayUrl() + "/user-else-detail-many/")
      .post(RequestBody.create(requestBody, JSON_MEDIA_TYPE))
      .build();

    Response response = client.newCall(request).execute();
    return response.isSuccessful() ? new JSONObject(response.body().string()) : null;
  }

  // 获取联机大厅房间成员列表
  public static JSONObject getOnlineLobbyRoomMembers(String userId, String userToken, String roomId) throws Exception {
    String requestBody = "{\"room_id\": \"" + roomId + "\"}";
    Request request = createBaseRequestBuilder(userId, userToken)
      .url(getApiGatewayUrl() + "/online-lobby-member/query/list-by-room-id")
      .post(RequestBody.create(requestBody, JSON_MEDIA_TYPE))
      .build();

    Response response = client.newCall(request).execute();
    return response.isSuccessful() ? new JSONObject(response.body().string()) : null;
  }

  // 获取网易钻石数量信息
  public static JSONObject getNeteaseDiamonds(String userId, String userToken) throws Exception {
    RequestBody body = RequestBody.create("{}", JSON_MEDIA_TYPE);
    Request request = createBaseRequestBuilder(userId, userToken)
      .url(getWebServerUrl() + "/user-currency/query")
      .post(body)
      .build();

    Response response = client.newCall(request).execute();
    return response.isSuccessful() ? new JSONObject(response.body().string()) : null;
  }

  // 通过用户名查询玩家信息
  public static JSONObject getUidByName(String userId, String userToken, String targetUid) throws Exception {
    JSONObject bodyJSON = new JSONObject();
    try {
      bodyJSON.put("name_or_mail", Utils.stringToUnicode(targetUid));
    } catch (JSONException e) {
      System.err.println("Error creating JSON body: " + e.getMessage());
    }

    RequestBody body = RequestBody.create(bodyJSON.toString(), JSON_MEDIA_TYPE);
    Request request = createBaseRequestBuilder(userId, userToken)
      .url(getApiGatewayUrl() + "/user-search-friend/")
      .post(body)
      .build();

    Response response = client.newCall(request).execute();
    return response.isSuccessful() ? new JSONObject(response.body().string()) : null;
  }
    
  public static JSONObject authenticationOtp(String osName, String osVer, String macAddr, String saUdid, String appVer, String disk, String sdkUid, String sessionId, String sdkVersion, String udid, String deviceId, String aid, String otpToken) throws Exception {
  	String packet = "{\"otp_token\":\"$OTPToken$\",\"otp_pwd\":\"\",\"aid\":$A1D$,\"sauth_json\":\"{\\u0022gameid\\u0022:\\u0022x19\\u0022,\\u0022login_channel\\u0022:\\u0022netease\\u0022,\\u0022app_channel\\u0022:\\u0022netease\\u0022,\\u0022platform\\u0022:\\u0022pc\\u0022,\\u0022sdkuid\\u0022:\\u0022$SDKUID$\\u0022,\\u0022sessionid\\u0022:\\u0022$sessionid$\\u0022,\\u0022sdk_version\\u0022:\\u00223.4.0\\u0022,\\u0022udid\\u0022:\\u0022$UDID$\\u0022,\\u0022deviceid\\u0022:\\u0022$deviceid$\\u0022,\\u0022aim_info\\u0022:\\u0022{\\\\\\u0022aim\\\\\\u0022:\\\\\\u0022$IP$\\\\\\u0022,\\\\\\u0022country\\\\\\u0022:\\\\\\u0022CN\\\\\\u0022,\\\\\\u0022tz\\\\\\u0022:\\\\\\u0022\\\\\\u002B0800\\\\\\u0022,\\\\\\u0022tzid\\\\\\u0022:\\\\\\u0022\\\\\\u0022}\\u0022,\\u0022client_login_sn\\u0022:\\u0022846C15C9F72E4C399247CFB35532C07A\\u0022,\\u0022gas_token\\u0022:\\u0022\\u0022,\\u0022source_platform\\u0022:\\u0022pc\\u0022,\\u0022ip\\u0022:\\u0022$IP$\\u0022}\",\"sa_data\":\"{\\u0022os_name\\u0022:\\u0022$OS_NAME$\\u0022,\\u0022os_ver\\u0022:\\u0022$OS_VER$\\u0022,\\u0022mac_addr\\u0022:\\u0022$MACA$\\u0022,\\u0022udid\\u0022:\\u0022$udid_sa$\\u0022,\\u0022app_ver\\u0022:\\u0022$APPVER$\\u0022,\\u0022sdk_ver\\u0022:\\u0022\\u0022,\\u0022network\\u0022:\\u0022\\u0022,\\u0022disk\\u0022:\\u0022$Disk$\\u0022,\\u0022is64bit\\u0022:\\u00221\\u0022,\\u0022video_card1\\u0022:\\u0022NVIDIA GeForce GTX 1060\\u0022,\\u0022video_card2\\u0022:\\u0022\\u0022,\\u0022video_card3\\u0022:\\u0022\\u0022,\\u0022video_card4\\u0022:\\u0022\\u0022,\\u0022launcher_type\\u0022:\\u0022PC_java\\u0022,\\u0022pay_channel\\u0022:\\u0022netease\\u0022}\",\"version\":{\"version\":\"$APPVER$\",\"launcher_md5\":\"\",\"updater_md5\":\"\"}}";
      packet = packet.replace("$OS_NAME$", osName)
      .replace("$OS_VER$", osVer)
      .replace("$MACA$", macAddr)
      .replace("$udid_sa$", saUdid)
      .replace("$APPVER$", appVer)
      .replace("$Disk$", disk)
      .replace("$SDKUID$", sdkUid)
      .replace("$SdkUid$", sdkUid)
      .replace("$sessionid$", sessionId)
      .replace("$SDK_Verion$", sdkVersion)
      .replace("$UDID$", udid)
      .replace("$deviceid$", deviceId)
      .replace("$A1D$", aid)
      .replace("$OTPToken$", otpToken);
      byte[] data = x19Crypt.httpEncrypt(packet.getBytes());
      RequestBody body = RequestBody.create(data, JSON_MEDIA_TYPE);
      Request request = new Request.Builder()
      .addHeader("User-Agent", UA)
      .addHeader("Content-Type", "application/json")
      .url(serverUrlList.getString("CoreServerUrl") + "/authentication-otp")
      .post(body)
      .build();
      Response response = client.newCall(request).execute();
      return response.isSuccessful() ? new JSONObject(new JSONObject(x19Crypt.parseLoginResponse(response.body().string().replaceAll("-","").getBytes())).getString("entity")) : null;
  }
    
  public static JSONObject loginOtp(String sAuthJson) throws Exception {
      RequestBody body = RequestBody.create(sAuthJson, JSON_MEDIA_TYPE);
      Request request = new Request.Builder()
      .addHeader("User-Agent", UA)
      .addHeader("Content-Type", "application/json")
      .url(serverUrlList.getString("CoreServerUrl") + "/login-otp")
      .post(body)
      .build();
      Response response = client.newCall(request).execute();
      return response.isSuccessful() ? new JSONObject(new JSONObject(response.body().string()).getString("entity")) : null;
  }
    
  public static JSONObject cookiesLogin(String cookies) throws Exception {
      JSONObject sAuthJson = new JSONObject(new JSONObject(cookies).getString("sauth_json"));
      JSONObject loginOtpRetEntity = loginOtp(cookies);
      JSONObject authOtpRetEntityEntity = authenticationOtp("android", "OPPO A5",
        x19Crypt.randStringRunes(12),"BFEBFBFF000406E34EA2FD9D","1.10.7.22905",
        x19Crypt.randStringRunes(8),
        sAuthJson.getString("sdkuid"),sAuthJson.getString("sessionid"),"1.0.0",sAuthJson.getString("udid"),
        sAuthJson.getString("deviceid"),
        Integer.toString(loginOtpRetEntity.getInt("aid")),loginOtpRetEntity.getString("otp_token"));
      return authOtpRetEntityEntity;
  }

public static Pair<String, Integer> pt4399Login(String username, String password) {
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .cookieJar(new CookieJar() {
                private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
                
                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    cookieStore.put(url.host(), cookies);
                }
                
                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .build();
    
    try {
        String captchasessionid = "captchaReq" + x19Crypt.randStringRunes(19);
        String sessionId = UUID.randomUUID().toString();
        
        // 第一次登录请求
        RequestBody firstLoginBody = RequestBody.create(
                "postLoginHandler=default&externalLogin=qq&bizId=2100001792&appId=kid_wdsj&gameId=wd&sec=1&password=" + 
                URLEncoder.encode(password, "UTF-8") + "&username=" + URLEncoder.encode(username, "UTF-8"),
                MediaType.parse("application/x-www-form-urlencoded"));
        
        Request firstRequest = new Request.Builder()
                .url("http://ptlogin.4399.com/ptlogin/login.do?v=1")
                .post(firstLoginBody)
                .addHeader("Cookie", "ptusertype=kid_wdsj.4399_login; phlogact=l123456; USESSIONID=" + sessionId)
                .build();
        
        Response loginResponse = client.newCall(firstRequest).execute();
        String loginResponseContent = loginResponse.body().string();
        
        // 检查是否需要验证码
        if (loginResponseContent.contains("<div id=\"Msg\" class=\"login_hor login_err_tip\">") && 
            loginResponseContent.contains("<div id=\"Msg\" class=\"login_hor login_err_tip\"></div>")) {
            
            for (int i = 0; i < 5; i++) {
                // 获取验证码图片
                Request captchaRequest = new Request.Builder()
                        .url("http://ptlogin.4399.com/ptlogin/captcha.do?captchaId=" + captchasessionid)
                        .build();
                
                Response captchaResponse = client.newCall(captchaRequest).execute();
                byte[] codeImg = captchaResponse.body().bytes();
                
                // 识别验证码（需要实现 ServerAuth.parseCodeFromImage 方法）
                String result = "ServerAuth.ParseCodeFromImage(codeImg)";//ServerAuth.parseCodeFromImage(codeImg);
                if (result != null && result.length() == 4) {
                    String yanzhengma = result;
                    RequestBody retryBody = RequestBody.create(
                            "postLoginHandler=default&externalLogin=qq&bizId=2100001792&appId=kid_wdsj&gameId=wd&sec=1&password=" + 
                            URLEncoder.encode(password, "UTF-8") + "&username=" + URLEncoder.encode(username, "UTF-8") + 
                            "&sessionId=" + captchasessionid + "&inputCaptcha=" + yanzhengma,
                            MediaType.parse("application/x-www-form-urlencoded"));
                    
                    Request retryRequest = new Request.Builder()
                            .url("http://ptlogin.4399.com/ptlogin/login.do?v=1")
                            .post(retryBody)
                            .addHeader("Cookie", "ptusertype=kid_wdsj.4399_login; phlogact=l123456; USESSIONID=" + sessionId)
                            .build();
                    
                    loginResponse = client.newCall(retryRequest).execute();
                    loginResponseContent = loginResponse.body().string();
                    
                    if (!loginResponseContent.contains("<div id=\"Msg\" class=\"login_hor login_err_tip\"></div>")) {
                        break;
                    }
                }
            }
        }
        
        // 检查验证码识别结果
        if (loginResponseContent.contains("<div id=\"Msg\" class=\"login_hor login_err_tip\"></div>")) {
            return new Pair<>("验证码识别失败且登录过于频繁", 1);
        }
        
        // 检查其他错误信息
        if (loginResponseContent.contains("<div id=\"Msg\" class=\"login_hor login_err_tip\">")) {
            int startIndex = loginResponseContent.indexOf("<div id=\"Msg\" class=\"login_hor login_err_tip\">\r\n\t\t\t\t\t\t\t\t\t");
            int endIndex = loginResponseContent.indexOf("\r\n\t\t\t\t\t\t\t\t</div>");
            
            if (startIndex != -1 && endIndex != -1) {
                String errorMsg = loginResponseContent.substring(
                        startIndex + "<div id=\"Msg\" class=\"login_hor login_err_tip\">\r\n\t\t\t\t\t\t\t\t\t".length(),
                        endIndex).trim();
                return new Pair<>(errorMsg, 2);
            }
        }
        
        // 检查登录成功
        if (loginResponse.isSuccessful()) {
            // 获取Cookie
            List<String> cookies = loginResponse.headers("Set-Cookie");
            String uauthValue = null;
            
            for (String cookie : cookies) {
                if (cookie.contains("Uauth=")) {
                    int start = cookie.indexOf("Uauth=") + 6;
                    int end = cookie.indexOf(";", start);
                    uauthValue = cookie.substring(start, end != -1 ? end : cookie.length());
                    uauthValue = java.net.URLDecoder.decode(uauthValue, "UTF-8");
                    break;
                }
            }
            
            if (uauthValue == null) {
                return new Pair<>("请检查账号密码是否输入正确", 3);
            }
            
            String[] values = uauthValue.split("\\|");
            if (values.length < 5) {
                return new Pair<>("无效的认证信息", 3);
            }
            
            String randTime = values[4];
            
            // 发送检查请求
            Request checkRequest = new Request.Builder()
                    .url("http://ptlogin.4399.com/ptlogin/checkKidLoginUserCookie.do?appId=kid_wdsj&gameUrl=http://cdn.h5wan.4399sj.com/microterminal-h5-frame?game_id=500352&rand_time=" + 
                         randTime + "&nick=null&onLineStart=false&show=1&isCrossDomain=1&retUrl=http%253A%252F%252Fptlogin.4399.com%252Fresource%252Fucenter.html")
                    .post(RequestBody.create("", null))
                    .addHeader("Cookie", "phlogact=l123456; USESSIONID=" + sessionId + "; Uauth=" + uauthValue + "; Puser=" + username)
                    .build();
            
            Response checkResponse = client.newCall(checkRequest).execute();
            
            if (!checkResponse.isSuccessful()) {
                return new Pair<>("检查请求失败", 4);
            }
            
            // 获取信息请求
            String queryStr = checkRequest.url().query();
            Request infoRequest = new Request.Builder()
                    .url("https://microgame.5054399.net/v2/service/sdk/info?callback=&queryStr=" + URLEncoder.encode(queryStr, "UTF-8"))
                    .addHeader("Cookie", "phlogact=l123456; USESSIONID=" + sessionId + "; Uauth=" + uauthValue + "; Puser=" + username)
                    .build();
            
            Response infoResponse = client.newCall(infoRequest).execute();
            String infoContent = infoResponse.body().string();
            
            JSONObject jsonObj = new JSONObject(infoContent);
            int code = jsonObj.getInt("code");
            String msg = jsonObj.getString("msg");
            
            if (code == 10000) {
                JSONObject data = jsonObj.getJSONObject("data");
                String sdkLoginData = data.getString("sdk_login_data");
                String udid = x19Crypt.randStringRunes(32);
                
                Map<String, String> parameters = new HashMap<>();
                for (String parameter : sdkLoginData.split("&")) {
                    String[] parts = parameter.split("=");
                    if (parts.length == 2) {
                        parameters.put(parts[0], parts[1]);
                    }
                }
                
                String time = parameters.get("time");
                String uid = parameters.get("uid");
                String token = parameters.get("token");
                
                String sauth = "{\"sauth_json\" : \"{\\\"gameid\\\":\\\"x19\\\",\\\"login_channel\\\":\\\"4399pc\\\",\\\"app_channel\\\":\\\"4399pc\\\",\\\"platform\\\":\\\"pc\\\",\\\"sdkuid\\\":\\\"" + uid + 
                        "\\\",\\\"sessionid\\\":\\\"" + token + "\\\",\\\"sdk_version\\\":\\\"1.0.0\\\",\\\"udid\\\":\\\"" + udid + 
                        "\\\",\\\"deviceid\\\":\\\"" + udid + "\\\",\\\"aim_info\\\":\\\"{\\\\\\\"aim\\\\\\\":\\\\\\\"127.0.0.1\\\\\\\",\\\\\\\"country\\\\\\\":\\\\\\\"CN\\\\\\\",\\\\\\\"tz\\\\\\\":\\\\\\\"+0800\\\\\\\",\\\\\\\"tzid\\\\\\\":\\\\\\\"\\\\\\\"}\\\",\\\"client_login_sn\\\":\\\"" + 
                        UUID.randomUUID().toString().replace("-", "").toUpperCase() + 
                        "\\\",\\\"gas_token\\\":\\\"\\\",\\\"source_platform\\\":\\\"pc\\\",\\\"ip\\\":\\\"127.0.0.1\\\",\\\"userid\\\":\\\"" + 
                        username.toLowerCase() + "\\\",\\\"realname\\\":\\\"{\\\\\\\"realname_type\\\\\\\":\\\\\\\"0\\\\\\\"}\\\",\\\"timestamp\\\":\\\"" + time + "\\\"}\"}";
                
                return new Pair<>(sauth, 0);
            }
            
            return new Pair<>(msg, 4);
        }
        
        return new Pair<>("未知错误", 5);
        
    } catch (IOException e) {
        return new Pair<>("网络请求失败: " + e.getMessage(), 5);
    } catch (Exception e) {
        return new Pair<>("处理失败: " + e.getMessage(), 5);
    }
}

public static class x19Crypt {
    private static final String[] _keys = {
        "MK6mipwmOUedplb6",
        "OtEylfId6dyhrfdn",
        "VNbhn5mvUaQaeOo9",
        "bIEoQGQYjKd02U0J",
        "fuaJrPwaH2cfXXLP",
        "LEkdyiroouKQ4XN1",
        "jM1h27H4UROu427W",
        "DhReQada7gZybTDk",
        "ZGXfpSTYUvcdKqdY",
        "AZwKf7MWZrJpGR5W",
        "amuvbcHw38TcSyPU",
        "SI4QotspbjhyFdT0",
        "VP4dhjKnDGlSJtbB",
        "UXDZx4KhZywQ2tcn",
        "NIK73ZNvNqzva4kd",
        "WeiW7qU766Q1YQZI"
    };

    public static byte[] pickKey(byte query) {
        int index = ((query >> 4) & 0xf);
        return _keys[index].getBytes(StandardCharsets.UTF_8);
    }

    public static String parseLoginResponse(byte[] body) throws Exception {
        if (body.length < 0x12) {
            throw new IllegalArgumentException("Input body too short");
        }
        
        byte[] iv = Arrays.copyOfRange(body, 0, 16);
        byte[] encryptedData = Arrays.copyOfRange(body, 16, body.length - 1);
        byte[] key = pickKey(body[body.length - 1]);
        
        byte[] result = aesCbcDecrypt(key, encryptedData, iv);
        
        int scissor = 0;
        int scissorPos = result.length - 1;
        while (scissor < 16 && scissorPos >= 0) {
            if (result[scissorPos] != 0x00) {
                scissor++;
            }
            scissorPos--;
        }

        return new String(Arrays.copyOfRange(result, 0, scissorPos + 1), StandardCharsets.UTF_8);
    }

    public static byte[] httpEncrypt(byte[] bodyIn) {
        try {
            // 计算需要填充的长度
            int blockSize = 16;
            int paddingLength = blockSize - (bodyIn.length % blockSize);
            if (paddingLength == 0) {
                paddingLength = blockSize; // 如果正好是块大小的倍数，需要填充一整块
            }
            
            // 创建填充后的数组
            byte[] body = new byte[bodyIn.length + paddingLength];
            System.arraycopy(bodyIn, 0, body, 0, bodyIn.length);
            
            // 填充随机字节（PKCS7 风格）
            byte[] randFill = randStringRunes(paddingLength).getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(randFill, 0, body, bodyIn.length, paddingLength);
            
            Random random = new Random();
            byte keyQuery = (byte) ((random.nextInt(16) << 4) | 2);
            byte[] initVector = randStringRunes(0x10).getBytes(StandardCharsets.US_ASCII);
            byte[] encrypted = aesCbcEncrypt(pickKey(keyQuery), body, initVector);
            
            byte[] result = new byte[16 + encrypted.length + 1];
            System.arraycopy(initVector, 0, result, 0, 16);
            System.arraycopy(encrypted, 0, result, 16, encrypted.length);
            result[result.length - 1] = keyQuery;
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static String computeDynamicToken(String path, byte[] body, String token) throws Exception {
        StringBuilder payload = new StringBuilder();
        payload.append(completeMD5Hex(token.getBytes(StandardCharsets.UTF_8)));
        payload.append(new String(body, StandardCharsets.UTF_8));
        payload.append("0eGsBkhl");
        payload.append(path.endsWith("?") ? path.substring(0, path.length() - 1) : path);
        
        byte[] sum = completeMD5Hex(payload.toString().getBytes(StandardCharsets.UTF_8))
                .getBytes(StandardCharsets.UTF_8);
        
        String binaryString = toBinaryString(sum);
        binaryString = binaryString.substring(6) + binaryString.substring(0, 6);
        
        for (int i = 0; i < sum.length; i++) {
            String section = binaryString.substring(i * 8, (i + 1) * 8);
            byte by = 0;
            for (int j = 0; j < 8; j++) {
                if (section.charAt(7 - j) == '1') {
                    by = (byte) (by | (1 << (j & 0x1f)));
                }
            }
            sum[i] = (byte) (by ^ sum[i]);
        }
        
        String b64Encoded = android.util.Base64.encodeToString(sum, android.util.Base64.NO_WRAP);
        String result = b64Encoded.substring(0, 16).replace("+", "m").replace("/", "o") + "1";
        
        return result;
    }

    // AES CBC 加密（使用 Android 自带的 PKCS5Padding，与 PKCS7 兼容）
    private static byte[] aesCbcEncrypt(byte[] key, byte[] data, byte[] iv) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(data);
    }

    // AES CBC 解密（使用 Android 自带的 PKCS5Padding，与 PKCS7 兼容）
    private static byte[] aesCbcDecrypt(byte[] key, byte[] data, byte[] iv) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(data);
    }

    public static String randStringRunes(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String completeMD5Hex(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(data);
        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static String toBinaryString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            for (int i = 7; i >= 0; i--) {
                sb.append((b >> i) & 1);
            }
        }
        return sb.toString();
    }

    // 如果需要处理填充移除，可以添加这个方法
    private static byte[] removePKCS7Padding(byte[] data) {
        if (data == null || data.length == 0) {
            return data;
        }
        
        int padding = data[data.length - 1] & 0xFF;
        if (padding > 0 && padding <= 16) {
            // 检查填充是否有效
            boolean validPadding = true;
            for (int i = data.length - padding; i < data.length; i++) {
                if (data[i] != padding) {
                    validPadding = false;
                    break;
                }
            }
            if (validPadding) {
                return Arrays.copyOfRange(data, 0, data.length - padding);
            }
        }
        return data;
    }
}
    
public static class x19SignExtensions {
    private static final String KEY = "942894570397f6d1c9cca2535ad18a2b";
    
    public static String encrypt(String data) {
        return "!x19sign!" + encrypt(data, KEY);
    }

    public static String decrypt(String data) {
        return data.startsWith("!x19sign!") ? 
               decrypt(data.substring("!x19sign!".length()), KEY) : data;
    }
    
    private static String encrypt(String data, String key) {
        byte[] dataBytes = padRight(data, 32).getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = padRight(key, 32).getBytes(StandardCharsets.UTF_8);
        long[] encrypted = encryptData(toLongArray(dataBytes), toLongArray(keyBytes));
        return toHexString(encrypted);
    }

    private static String decrypt(String data, String key) {
        if (data == null || data.trim().isEmpty()) {
            return data;
        }
        
        byte[] keyBytes = padRight(key, 32).getBytes(StandardCharsets.UTF_8);
        long[] decrypted = decryptData(hexStringToLongArray(data), toLongArray(keyBytes));
        byte[] resultBytes = toByteArray(decrypted);
        return new String(resultBytes, StandardCharsets.UTF_8).trim();
    }

    private static long[] encryptData(long[] data, long[] key) {
        int n = data.length;
        if (n < 1) {
            return data;
        }
        
        long z = data[n - 1];
        long y = data[0];
        long delta = 0x9E3779B9L; // 2654435769L
        long sum = 0L;
        long q = 6L + 52L / n;
        
        while (q-- > 0) {
            sum += delta;
            long e = (sum >> 2) & 3L;
            
            for (int p = 0; p < n - 1; p++) {
                y = data[p + 1];
                z = data[p] += (((z >> 5) ^ (y << 2)) + ((y >> 3) ^ (z << 4))) ^ 
                               ((sum ^ y) + (key[(int)((p & 3L) ^ e)] ^ z));
            }
            
            y = data[0];
            z = data[n - 1] += (((z >> 5) ^ (y << 2)) + ((y >> 3) ^ (z << 4))) ^ 
                               ((sum ^ y) + (key[(int)(((n - 1) & 3L) ^ e)] ^ z));
        }
        
        return data;
    }

    private static long[] decryptData(long[] data, long[] key) {
        int n = data.length;
        if (n < 1) {
            return data;
        }
        
        long z = data[n - 1];
        long y = data[0];
        long delta = 0x9E3779B9L; // 2654435769L
        long q = 6L + 52L / n;
        long sum = q * delta;
        
        while (sum != 0) {
            long e = (sum >> 2) & 3L;
            
            for (int p = n - 1; p > 0; p--) {
                z = data[p - 1];
                y = data[p] -= (((z >> 5) ^ (y << 2)) + ((y >> 3) ^ (z << 4))) ^ 
                              ((sum ^ y) + (key[(int)((p & 3L) ^ e)] ^ z));
            }
            
            z = data[n - 1];
            y = data[0] -= (((z >> 5) ^ (y << 2)) + ((y >> 3) ^ (z << 4))) ^ 
                          ((sum ^ y) + (key[(int)((0 & 3L) ^ e)] ^ z));
            sum -= delta;
        }
        
        return data;
    }

    private static long[] toLongArray(byte[] byteArray) {
        int length = byteArray.length;
        int numLongs = (length % 8 == 0) ? length / 8 : length / 8 + 1;
        long[] result = new long[numLongs];
        
        for (int i = 0; i < numLongs - 1; i++) {
            result[i] = bytesToLong(byteArray, i * 8);
        }
        
        // Handle last long which might be padded
        byte[] lastBytes = new byte[8];
        System.arraycopy(byteArray, (numLongs - 1) * 8, lastBytes, 0, 
                        length - (numLongs - 1) * 8);
        result[numLongs - 1] = bytesToLong(lastBytes, 0);
        
        return result;
    }

    private static byte[] toByteArray(long[] longArray) {
        List<Byte> byteList = new ArrayList<>();
        
        for (long l : longArray) {
            byte[] bytes = longToBytes(l);
            for (byte b : bytes) {
                byteList.add(b);
            }
        }
        
        // Remove trailing zeros
        while (!byteList.isEmpty() && byteList.get(byteList.size() - 1) == 0) {
            byteList.remove(byteList.size() - 1);
        }
        
        byte[] result = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            result[i] = byteList.get(i);
        }
        
        return result;
    }

    private static String toHexString(long[] longArray) {
        StringBuilder sb = new StringBuilder();
        for (long l : longArray) {
            String hex = Long.toHexString(l);
            // Pad to 16 characters (8 bytes)
            while (hex.length() < 16) {
                hex = "0" + hex;
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private static long[] hexStringToLongArray(String hexString) {
        int length = hexString.length();
        int numLongs = length / 16;
        long[] result = new long[numLongs];
        
        for (int i = 0; i < numLongs; i++) {
            String hex = hexString.substring(i * 16, (i + 1) * 16);
            result[i] = Long.parseLong(hex, 16);
        }
        
        return result;
    }

    private static String padRight(String s, int length) {
        if (s == null) {
            s = "";
        }
        if (s.length() >= length) {
            return s;
        }
        
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < length) {
            sb.append('\0');
        }
        return sb.toString();
    }

    private static long bytesToLong(byte[] bytes, int offset) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            if (offset + i < bytes.length) {
                value |= ((long) bytes[offset + i] & 0xff) << (8 * i);
            }
        }
        return value;
    }

    private static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 0; i < 8; i++) {
            result[i] = (byte) (l >> (8 * i));
        }
        return result;
    }
}
}
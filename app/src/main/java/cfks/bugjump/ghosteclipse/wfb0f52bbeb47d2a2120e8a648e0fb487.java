package cfks.bugjump.ghosteclipse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.provider.Settings;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.PopTip;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;


import jiesheng.存储卡操作;
import org.json.JSONException;
import org.json.JSONObject;

import static android.graphics.Typeface.BOLD;
	/*
	本模板仅供学习研究
	本模板仅供学习研究
	界面有特多bug点，推荐自己去写界面
	界面有特多bug点，推荐自己去写界面
	界面有特多bug点，推荐自己去写界面	
	 微云端网络验证官网:https://llua.cn
	 1.加密选择RC4-2
	 2.记得把登录，解绑的WY_APPKEY ，WY_APPID,WY_RC4KEY替换掉
	 3.开启数据签名开关
	 4.开启(签名放在Data里)
	 */

public class wfb0f52bbeb47d2a2120e8a648e0fb487 {
	SurfaceHolder holder;
    Canvas canvas;
	static String wy_url = "https://wy.llua.cn";//API接口
	static String WY_APPID = "70314"; // 项目id
	static String WY_APPKEY = "b89f555399aea4fad1cfe"; //项目key
	static String WY_RC4KEY = "g689837d6be74f9c751754a10";//RC4密钥
    static LinearLayout linearLayouts;
    static EditText edittext;
    static  AlertDialog.Builder dialogs,dialogs1;
    static AlertDialog alertDialogs,alertDialogs1;
    static AlertDialog alertDialogss;
    static AlertDialog dialog = null;
	public Intent intent;	
	static String 当前版本号="1.0";
    static String 卡密;

	public static void Start(final Context context) {
	    更新(context);//检测更新与公告
		Login(context);
    }

	
	public static String 获取机器码(Context context) {
	    return  android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

	public static boolean isVpnUsed() {//vpn检测
        try {
            List<NetworkInterface> nis = Collections.list(NetworkInterface.getNetworkInterfaces());
            if (nis != null) {
                for (NetworkInterface intf : nis) {
                    if (!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
                        continue;
                    }
                    if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())) {
                        return true; // The VPN is up
                    }
                }
            }
        } catch (Throwable e) {
			e.printStackTrace();
        }
        return false;
    }
    public static void 更新(final Context context) {
        new Thread(new Runnable() {
                @Override
                public void run() {
					String sign="/api/?app=" + WY_APPID + "&id=ini"; // 公告
					try {
						String content=RC4Util.decryRC4(UrlPost(wy_url + sign, ""), WY_RC4KEY, "UTF-8");
						JSONObject jsonObject = new JSONObject(content);
						String data=jsonObject.getString("msg");
						JSONObject json = new JSONObject(data);						
						String version = json.getString("version"); //最新版本
						String app_update_show = json.getString("app_update_show"); //更新内容
						final String app_update_url = json.getString("app_update_url"); //最新地址
						String app_update_must = json.getString("app_update_must"); //强制更新	
						if (!version.equals(当前版本号)) {
							Looper.prepare();
                            MessageDialog.build()
                            .setTitle("有新版本")
                            .setMessage(app_update_show)
                            .setCancelable(!app_update_must.equals("y"))
                            .setOkButton("立即更新",(a,b)->{
                                Uri uri = Uri.parse(app_update_url);
								Intent intent = new Intent(Intent.ACTION_VIEW, uri);
								context.startActivity(intent);
                                return false;
                            })
                            .show();
							Looper.loop();
						}else{
							公告(context);
						}
					} catch (Exception e) {
					}
				}
			}).start(); 
    }

	public static void 公告(final Context context) {
        new Thread(new Runnable() {
                @Override
                public void run() {
					String sign="/api/?app=" + WY_APPID + "&id=notice"; // 公告
					try {
						String content=RC4Util.decryRC4(UrlPost(wy_url + sign, ""), WY_RC4KEY, "UTF-8");
						JSONObject jsonObject = new JSONObject(content);
						String data=jsonObject.getString("msg");
						JSONObject json = new JSONObject(data);						
						String appgg=json.optString("app_gg");
						Looper.prepare();
                        MessageDialog.build()
                        .setTitle("公告")
                        .setMessage(appgg)
                        .setCancelable(false)
                        .setOkButton(((Activity) context).getString(android.R.string.ok))
                        .show();
						Looper.loop();
					} catch (Exception e) {
					}
				}
			}).start(); 
    }

	public static void 登录(final String 卡密, final Context context) {
		new Thread(new Runnable() {
				@Override
				public void run() {
					String sign = wy_url + "/api/?id=kmlogin"; // 卡密登录
					String content;
					String random;
					String uuid; //系统机器码
					uuid = 获取机器码(context);
					
					Long time = System.currentTimeMillis() / 1000;
					String signs = encodeMD5("kami=" + 卡密 + "&markcode=" + uuid + "&t=" + time + "&" + WY_APPKEY);
					String body="&app=" + WY_APPID + "&kami=" + 卡密 + "&markcode=" + uuid + "&t=" + time + "&sign=" + signs;		
					String 提交内容=sign + body;
					
						random = UUID.randomUUID().toString().replace("-", "") + WY_APPKEY + uuid;
                        try {
							String data = "data=" + RC4Util.encryRC4String(body, WY_RC4KEY, "UTF-8");
							content = RC4Util.decryRC4(UrlPost(提交内容 + "&app=" + WY_APPID, data + "&value=" + random), WY_RC4KEY, "UTF-8");
							JSONObject jsonObject = new JSONObject(content);
							String code=jsonObject.getString("g004c9f7e04b924f654eda52330587338");//是否登录成功
							String Message=jsonObject.getString("pd518890dd411b8c83ea1a89494d702c5");
							String check=jsonObject.optString("ha9cba3ebf658511cfd3e70852887bc1e");//校验密钥
							Long timee=jsonObject.optLong("u4f5df809023a1c6874cba834485dbe1a");//服务器时间戳
							
							if (code.equals("114514")) {//登录成功
							    JSONObject json = new JSONObject(Message);
							    Long kamid=json.optLong("bda8316e7722fa7abb3d4a9f90af050b2");
							    if (!check.equals(encodeMD5(""+timee.toString()+""+WY_APPKEY+""+random+""))) {
								    Looper.prepare();							
								    PopTip.show("非法操作").iconError();
								    Looper.loop();
							    }else if (timee - time > 30) {
							        Looper.prepare();							
							        PopTip.show("数据过期").iconError();
							        Looper.loop();
							    } else if (timee - time < -30) {
							        Looper.prepare();							
							        PopTip.show("数据过期").iconError();
							        Looper.loop();
							    }else{
									Long vip=json.optLong("y78b51c328009cb6a2c2f831df24a2ad0");
									GregorianCalendar gc=new GregorianCalendar();
									gc.setTimeInMillis(vip * 1000);
									SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									Looper.prepare();
									PopTip.show("登录成功\n到期时间:" + df.format(gc.getTime())).iconSuccess();
									写入(存储卡操作.取私有目录路径((Activity) context) + "/km", 卡密);
									alertDialogss.dismiss();
									Looper.loop();
								}
							} else {
								Looper.prepare();
								PopTip.show(Message).iconError();
								Looper.loop();
							}
						} catch (JSONException e) {
							Looper.prepare();
							PopTip.show("错误"+e).iconError();
							Looper.loop();
						} catch (Exception e) {
                        }
                    }

                

            }).start(); 
    }

	public static void 解绑(final String 卡密, final Context context) {

		new Thread(new Runnable() {         
				@Override
				public void run() {
					String sign = wy_url + "/api/?id=kmdismiss"; // 卡密解绑
					String content;
					String random;
					String uuid;
					uuid = 获取机器码(context);
					
					random = UUID.randomUUID().toString().replace("-", "") + WY_APPKEY + uuid;			
					Long time = System.currentTimeMillis() / 1000;
					String signs = encodeMD5("kami=" + 卡密 + "&markcode=" + uuid + "&t=" + time + "&" + WY_APPKEY);
					String body="&app=" + WY_APPID + "&kami=" + 卡密 + "&markcode=" + uuid + "&t=" + time + "&sign=" + signs;		
					String 提交内容=sign + body;
					
                        try {
							String data = "data=" + RC4Util.encryRC4String(body, WY_RC4KEY, "UTF-8");
							content = RC4Util.decryRC4(UrlPost(提交内容 + "&app=" + WY_APPID, data + "&value=" + random), WY_RC4KEY, "UTF-8");
							JSONObject jsonObject = new JSONObject(content);
							String code=jsonObject.getString("code");//是否解绑成功
							String Message=jsonObject.getString("msg");
							if (code.equals("200")) {//解绑成功
								JSONObject json = new JSONObject(Message);						
								String num=json.getString("num");
								Looper.prepare();
								PopTip.show("解绑成功\n当前卡密解绑次数剩余 " + num + " 次").iconSuccess();
								Looper.loop();
							} else {
								Looper.prepare();
								PopTip.show(Message).iconError();
								Looper.loop();
							}
						} catch (JSONException e) {
							Looper.prepare();
							PopTip.show("服务器连接失败").iconError();
							Looper.loop();
						} catch (Exception e) {
						}
					}

				
			}).start(); 
    }

	static void Login(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, 5);

        GradientDrawable background = new GradientDrawable();
        background.setColor(0xFFF6F6F6);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 20);
        params.leftMargin = 0;

        GradientDrawable buttonBackground = new GradientDrawable();
        buttonBackground.setStroke(2, 0xFFDCDCDC);
        buttonBackground.setColor(0xFFF6F6F6);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        layout.setBackground(background);

        LinearLayout layout2 = new LinearLayout(context);
        layout2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        layout2.setPadding(0, 0, 0, 0);

        LinearLayout layout3 = new LinearLayout(context);
        layout3.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        layout3.setPadding(25, 25, 25, 25);   

        TextView title = new TextView(context);
        title.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        title.setGravity(Gravity.CENTER);
        title.setText("GhostEclipse");
        title.setTextColor(0xFF000000);
		title.setPadding(30, 30, 30, 30);
        title.setTextSize(20);
        title.setBackground(buttonBackground);
        layout.addView(title);
        layout.addView(layout3);
        layout.addView(layout2);

        Button button1 = new Button(context);
        button1.setLayoutParams(params);
        button1.setText("登录");
        button1.setTextColor(0xFF1278E7);
        button1.setPadding(30, 30, 30, 30);
        button1.setTextSize(15);
        button1.setBackground(buttonBackground);
        layout2.addView(button1);

        Button button2 = new Button(context);
        button2.setLayoutParams(params);
        button2.setText("解绑");
        button2.setTextColor(0xFF1278E7);
        button2.setPadding(30, 30, 30, 30);
        button2.setTextSize(15);
        button2.setBackground(buttonBackground);
        layout2.addView(button2);

        final EditText editext = new EditText(context);
		editext.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        editext.setHint("请输入卡密");
		editext.setText(读取文件(存储卡操作.取私有目录路径((Activity) context) + "/km"));
        editext.setTextColor(0xFF1278E7);
        editext.setPadding(10, 10, 10, 10);
        editext.setTextSize(15);
        editext.setBackgroundColor(0xFFF6F6F6);
        layout3.addView(editext);

        button1.setOnClickListener(new View.OnClickListener() {
                @Override 
                public void onClick(View view) {     

					if (isVpnUsed()) {
						System.exit(0);   
					}
                    卡密 = editext.getText().toString();
                    if (卡密.length() == 0) {
                        PopTip.show("请输入正确卡密格式").iconError();
                    } else {
                        登录(卡密, context);
                    }
				}});

		button2.setOnClickListener(new View.OnClickListener() {
                @Override 
                public void onClick(View view) {     
                    卡密 = editext.getText().toString();
                    if (卡密.length() == 0) {
                        PopTip.show("请输入正确卡密格式").iconError();
                    } else {
                        解绑(卡密, context);
                    }
				}});
        builder.setCancelable(false);
        builder.setView(layout);
        alertDialogss = builder.show();
	}

    public static void 创建文件夹(String path) {
        //新建一个File，传入文件夹目录
        File file = new File(path);
        //判断文件夹是否存在，如果不存在就创建，否则不创建
        if (!file.exists()) {
            //通过file的mkdirs()方法创建目录中包含却不存在的文件夹
            file.mkdirs();
        }

    }

	public static void 写入(String Files, String content) {
		try {
			FileWriter utf = new FileWriter(Files);
			utf.write(content);
			utf.close();
		} catch (IOException e) {}
	}

	public static String UrlPost(String ur, String byteString) {
        String str="";		
        try {
            URL url=new URL(ur);
            HttpURLConnection HttpURLConnection=(HttpURLConnection) url.openConnection();
            HttpURLConnection.setReadTimeout(9000);
            HttpURLConnection.setRequestMethod("POST");
            OutputStream outputStream = HttpURLConnection.getOutputStream();
            outputStream.write(byteString.getBytes());
            BufferedReader BufferedReader=new BufferedReader(new InputStreamReader(HttpURLConnection.getInputStream()));
            String String="";
            StringBuffer StringBuffer=new StringBuffer();
            while ((String = BufferedReader.readLine()) != null) {
                StringBuffer.append(String);
            }
            str = StringBuffer.toString();
        } catch (IOException e) {}
        return str;
    }
	public static String 读取文件(String path) {
        String str = "";
        try {
            File urlFile = new File(path);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            String mimeTypeLine = null;
            while ((mimeTypeLine = br.readLine()) != null) {
                str = str + mimeTypeLine;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
	public static String encodeMD5(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes("UTF-8"));
            byte messageDigest[] = md5.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                hexString.append(String.format("%02X", b));
            }
            return hexString.toString().toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

	


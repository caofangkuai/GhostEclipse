package cfks.bugjump.ghosteclipse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;
import androidx.preference.PreferenceManager;
import cfks.bugjump.ghosteclipse.databinding.ActivityGameBinding;
import cfks.bugjump.ghosteclipse.ui.WatermarkCanvas;
import cfks.bugjump.ghosteclipse.ui.SmallWatermarkCanvas;
import cfks.bugjump.ghosteclipse.ui.floatingView.SqWindowManagerFloatView;
import cfks.bugjump.ghosteclipse.relay.AccountManager;
import cfks.bugjump.ghosteclipse.ui.phoenix.DynamicIslandViewBuilder;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.dialogx.dialogs.MessageDialog;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import java.net.InetSocketAddress;
import java.util.Random;
import jiesheng.共享数据;
import jiesheng.应用操作;
import jiesheng.文件操作;
import jiesheng.系统操作;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.BedrockClientSession;
import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.PacketDirection;
import org.cloudburstmc.protocol.bedrock.codec.v818.Bedrock_v818;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockClientInitializer;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.json.JSONArray;

public class GameActivity extends AppCompatActivity {
    private ActivityGameBinding binding;
    private WindowManager wm;
    private WindowManager.LayoutParams wmlay;
    private WebView mywebview;
    private WindowManager wm1;
    private WindowManager.LayoutParams wmlay1;
    private WebView mywebview1;
    private WindowManager wm2;
    private WindowManager.LayoutParams wmlay2;
    private WindowManager wm3;
    private WindowManager.LayoutParams wmlay3;
    private WindowManager wm4;
    private WindowManager.LayoutParams wmlay4;
    private boolean isFloatingViewShow = false;
    private boolean isFirstLoad = false;
    private String musicId = "1377530437";
    private WatermarkCanvas watermarkCanvas;
    private SmallWatermarkCanvas smallWatermarkCanvas;
    private DynamicIslandViewBuilder mDynamicIslandViewBuilder;
    private ComposeView mDynamicIslandView;
    private String minecraftType = "";
    private static final String str_神秘字符串 = "CreeperBox Eating Apple";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        findViewById(R.id.game_loading).bringToFront();
        ImmersionBar.with(this)
        .transparentBar()
        .fullScreen(true)
        .fitsSystemWindows(false)
        .hideBar(BarHide.FLAG_HIDE_BAR)
        .init();
        minecraftType = Utils.getMinecraftType(this);
        共享数据.初始化数据(this,"MinecraftInfo");
        floatingWindowInit();
        connectServer();
    }
    
    @Override
    public void onWindowFocusChanged(boolean arg0) {
        super.onWindowFocusChanged(arg0);
        // TODO: Implement this method
        if(arg0 && !isFirstLoad){
            isFirstLoad = true;
            (new SqWindowManagerFloatView(this,R.drawable.ic_launcher,v->{
                if(isFloatingViewShow){
                    wm.removeView(mywebview);
                    isFloatingViewShow = false;
                }else{
                    wm.addView(mywebview, wmlay);
                    isFloatingViewShow = true;
                }
            })).show();
            wm1.addView(mywebview1, wmlay1);
            wm3.addView(smallWatermarkCanvas,wmlay3);
            wm4.addView(mDynamicIslandView,wmlay4);
        }
    }
    
    @Override
    public boolean onKeyDown(int arg0, KeyEvent arg1) {
        // TODO: Implement this method
        if(arg0 == KeyEvent.KEYCODE_BACK){
            MessageDialog.build()
            .setTitle("GhostEclipse")
            .setMessage("Do you want to quit?")
            .setOkButton(getString(android.R.string.yes),(d,v)->{
                Utils.toActivity(GameActivity.this,MainActivity.class);
                return false;
            })
            .setCancelButton(getString(android.R.string.cancel))
            .setCancelable(false)
            .show();
            return true;
        }
        return super.onKeyDown(arg0, arg1);
    }
    
    private void jsCallBack(String function,String param){
        switch(function){
            case "MusicOpen":
                mywebview1.loadUrl("javascript:playMusicByID('" + musicId + "');");
                break;
            case "MusicClose":
                mywebview1.loadUrl("javascript:stopMusic();");
                break;
            case "WatermarkOpen":
                wm2.addView(watermarkCanvas,wmlay2);
                break;
            case "WatermarkClose":
                wm2.removeView(watermarkCanvas);
                break;
        }
        if(function.endsWith("Open")){
            if(PreferenceManager.getDefaultSharedPreferences(this).getString("switch_floating_window_type","").equals("FoodByte UI")){
                mywebview1.loadUrl("javascript:arrList.addModule('" + function.replace("Open","") + "','');");
            }else{
                mDynamicIslandViewBuilder.showSwitch(function.replace("Open",""),true);
            }
        }else if(function.endsWith("Close")){
            if(PreferenceManager.getDefaultSharedPreferences(this).getString("switch_floating_window_type","").equals("FoodByte UI")){
                mywebview1.loadUrl("javascript:arrList.removeModule('" + function.replace("Close","") + "','');");
            }else{
                mDynamicIslandViewBuilder.showSwitch(function.replace("Close",""),false);
            }
        }
        if(function.equals("closeFloatingWindow")){
            wm.removeView(mywebview);
            isFloatingViewShow = false;
            return;
        }
        try{
            JSONArray paramsJson = new JSONArray(param);
            switch(function){
                case "MusicOnChange":
                    switch(paramsJson.getString(0)){
                        case "你看到的我(DJ版)":
                            musicId = "1377530437";
                            break;
                        case "Gold Dust(Radio Edit)":
                            musicId = "17244018";
                            break;
                    }
                    break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * @param log 打印的log
     */
    private void logChange(String log,String tag) {
        runOnUiThread(()->{
            TextView textView = findViewById(R.id.game_loading).findViewById(R.id.loading_output);
            textView.setMovementMethod(ScrollingMovementMethod.getInstance());
            textView.setScrollbarFadingEnabled(false);//滚动条一直显示
            String text = textView.getText().toString().trim();
            StringBuilder stringBuffer = new StringBuilder();
            StringBuilder append = stringBuffer.append(text).append("\n").append("[" + tag + "] " + log);
            textView.setText(append);
            textView.post(() -> {
                // 滚动到底部
                Layout layout = textView.getLayout();
                int padding = textView.getTotalPaddingTop() + textView.getTotalPaddingBottom();
                int line = textView.getLineCount() - 1;
                textView.scrollTo(0, layout.getLineTop(line + 1) - (textView.getHeight() - padding));
            });
        });
    }
    
    private void floatingWindowInit(){
        mywebview = new WebView(this);
      mywebview.getSettings().setJavaScriptEnabled(true); //设置允许Js
      /*设置webview控件背景透明*/
      mywebview.setBackgroundColor(Color.TRANSPARENT);
      mywebview.setWebChromeClient(new WebChromeClient());
      /* **用来可以打开网页中的链接** */
      mywebview.setWebViewClient(new WebViewClient(){
         @Override
         public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            if (uri.getScheme().equals("blockhackjs")) {
               if (uri.getAuthority().equals("callBack")) {
                  jsCallBack(uri.getQueryParameter("function"),uri.getQueryParameter("param"));
               }
               return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
         }
         @Override
         public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //在此处进行加载完成后的操作
            String js = "javascript:" + 文件操作.读入资源文件(GameActivity.this,"functions.js").replaceAll("\n","");
            //应用操作.信息框(GameActivity.this,js);
            mywebview.loadUrl(js);
         }
      });
      mywebview.requestFocus();
      mywebview.loadUrl("file:///android_asset/GhostEclipse.html"); //要载入的布局网页
      wm=(WindowManager) getSystemService(Context.WINDOW_SERVICE);
      wmlay = new WindowManager.LayoutParams();
      wmlay.type = WindowManager.LayoutParams.FIRST_SUB_WINDOW;
      wmlay.format=PixelFormat.RGBA_8888; //悬浮窗口背景设为透明
      wmlay.gravity=Gravity.RIGHT | Gravity.TOP;
      wmlay.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
      wmlay.x = 0;
      wmlay.y = 0;
      wmlay.width = 600;
      wmlay.height = WindowManager.LayoutParams.MATCH_PARENT;

      //音乐和状态悬浮窗
      mywebview1 = new WebView(this);
      mywebview1.getSettings().setJavaScriptEnabled(true); //设置允许Js
      /*设置webview控件背景透明*/
      mywebview1.setBackgroundColor(Color.TRANSPARENT);
      mywebview1.setWebChromeClient(new WebChromeClient());
      mywebview1.requestFocus();
      mywebview1.loadUrl("file:///android_asset/MusicAndState.html"); //要载入的布局网页
      mywebview1.getSettings().setMediaPlaybackRequiresUserGesture(false);
      wm1=(WindowManager) getSystemService(Context.WINDOW_SERVICE);
      wmlay1 = new WindowManager.LayoutParams();
      wmlay.type = WindowManager.LayoutParams.FIRST_SUB_WINDOW;
      wmlay1.format=PixelFormat.RGBA_8888; //悬浮窗口背景设为透明
      wmlay1.gravity=Gravity.RIGHT | Gravity.TOP;
      wmlay1.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
      | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
      | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
      | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
      wmlay1.x = 0;
      wmlay1.y = 0;
      wmlay1.width = WindowManager.LayoutParams.MATCH_PARENT;
      wmlay1.height = WindowManager.LayoutParams.MATCH_PARENT;
      
      //水印悬浮窗
      wm2=(WindowManager) getSystemService(Context.WINDOW_SERVICE);
      wmlay2 = new WindowManager.LayoutParams();
      wmlay2.type = WindowManager.LayoutParams.FIRST_SUB_WINDOW;
      wmlay2.format=PixelFormat.RGBA_8888; //悬浮窗口背景设为透明
      wmlay2.gravity=Gravity.LEFT | Gravity.TOP;
      wmlay2.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
      | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
      | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
      | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
      wmlay2.x = 0;
      wmlay2.y = 0;
      wmlay2.width = WindowManager.LayoutParams.MATCH_PARENT;
      wmlay2.height = WindowManager.LayoutParams.MATCH_PARENT;
      watermarkCanvas = new WatermarkCanvas(this,PreferenceManager.getDefaultSharedPreferences(this).getString("watermark",""));
      //左下角水印悬浮窗
      wm3=(WindowManager) getSystemService(Context.WINDOW_SERVICE);
      wmlay3 = new WindowManager.LayoutParams();
      wmlay3.type = WindowManager.LayoutParams.FIRST_SUB_WINDOW;
      wmlay3.format=PixelFormat.RGBA_8888; //悬浮窗口背景设为透明
      wmlay3.gravity=Gravity.LEFT | Gravity.TOP;
      wmlay3.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
      | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
      | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
      | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
      wmlay3.x = 0;
      wmlay3.y = 0;
      wmlay3.width = WindowManager.LayoutParams.MATCH_PARENT;
      wmlay3.height = WindowManager.LayoutParams.MATCH_PARENT;
      smallWatermarkCanvas = new SmallWatermarkCanvas(this);
      
      //phoen1x悬浮窗
      wm4=(WindowManager) getSystemService(Context.WINDOW_SERVICE);
      wmlay4 = new WindowManager.LayoutParams();
      wmlay4.type = WindowManager.LayoutParams.FIRST_SUB_WINDOW;
      wmlay4.format=PixelFormat.RGBA_8888; //悬浮窗口背景设为透明
      wmlay4.gravity=Gravity.CENTER_HORIZONTAL | Gravity.TOP;
      wmlay4.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
      | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
      | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
      | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
      wmlay4.y = Utils.dp2px(20);
      wmlay4.width = WindowManager.LayoutParams.WRAP_CONTENT;
      wmlay4.height = WindowManager.LayoutParams.WRAP_CONTENT;
      mDynamicIslandViewBuilder = new DynamicIslandViewBuilder();
      mDynamicIslandView = mDynamicIslandViewBuilder.build(this);
      mDynamicIslandViewBuilder.updateScale(1.0f);
      mDynamicIslandViewBuilder.updateText(PreferenceManager.getDefaultSharedPreferences(this).getString("watermark",""));
    }
    
    private String getValue(String key){
        return 共享数据.取文本(minecraftType + "_" + key);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO: Implement this method
        mDynamicIslandViewBuilder.onDestroy();
    }
    
    private void connectServer(){
        new Thread(()->{
            switch(minecraftType){
                case "BE":
                    connectBEServer();
                    break;
                default:
                    logChange(minecraftType + " is not supported now","ERROR");
                    break;
            }
        }).start();
    }
    
    private void connectBEServer(){
        logChange("Microsoft Account: " + AccountManager.accounts.get(0).toString(true),"INFO");
        String ip = getValue("ip");
        int port = Integer.parseInt(getValue("port"));
        logChange("Try to connect to " + ip + ":" + port + " ...","INFO");
        long clientGUID = (new Random()).nextLong();
        new Bootstrap()
        .channelFactory(RakChannelFactory.client(NioDatagramChannel.class))
        .option(RakChannelOption.RAK_GUID, clientGUID)
        .option(RakChannelOption.RAK_REMOTE_GUID, clientGUID)
        .option(RakChannelOption.RAK_CONNECT_TIMEOUT, 690000L)
        .group(new NioEventLoopGroup())
        .handler(new BedrockClientInitializer(){
            @Override
            protected void initSession(BedrockClientSession session) {
                // Connection established
                // Make sure to set the packet codec version you wish to use before sending out packets
                session.setCodec(Bedrock_v818.CODEC);//Bedrock_v818	1.21.90
                // Remember to set a packet handler so you receive incoming packets
                session.setPacketHandler(new BedrockPacketHandler(){
                    
                });
                // Now send packets...
            }
            
            @Override
            protected void preInitChannel(Channel channel) {
                try{
                    channel.attr(PacketDirection.ATTRIBUTE).set(PacketDirection.SERVER_BOUND);
                    super.preInitChannel(channel);
                }catch(Exception e){
                    e.printStackTrace();
                    logChange(e.toString(),"ERROR");
                }
            }
            
            /*
            @Override
            public BedrockClientSession createSession0(BedrockPeer peer,int subClientId) {
                
            }
            */
        })
        .connect(new InetSocketAddress(ip,port))
        .syncUninterruptibly();
    }
}
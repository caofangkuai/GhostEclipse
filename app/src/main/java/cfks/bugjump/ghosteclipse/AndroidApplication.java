package cfks.bugjump.ghosteclipse;

import android.app.Application;
import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogxmaterialyou.style.MaterialYouStyle;
import com.yc.toollib.tool.*;
import com.yc.toollib.crash.*;

public class AndroidApplication extends Application {
    private static AndroidApplication mInstance;
    
    @Override
    public void onCreate() {
        super.onCreate();
        // TODO: Implement this method
        mInstance = this;
        
        DialogX.init(this);
        DialogX.globalStyle = new MaterialYouStyle();
        DialogX.autoRunOnUIThread = true;
        DialogX.onlyOnePopTip = false;
        
        CrashHandler.getInstance().init(this, new CrashListener() {
            @Override
            public void againStartApp() {
                CrashToolUtils.startCrashListActivity(AndroidApplication.this);
            }
            @Override
            public void recordException(Throwable ex) {
            
            }
        });
    }
    
    public static AndroidApplication getInstance() {
        return mInstance;
    }
}
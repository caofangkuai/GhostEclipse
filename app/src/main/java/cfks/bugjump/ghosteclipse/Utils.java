package cfks.bugjump.ghosteclipse;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.content.Intent;

public class Utils {
    public static void toActivity(Activity act,Class target){
        Intent intent = new Intent();
        intent.setClass(act, target);
        act.startActivity(intent);
        act.finish();
        act.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    
    public static void toGameActivity(Activity act,String type){
        Intent intent = new Intent();
        intent.setClass(act, GameActivity.class);
        intent.putExtra("type",type);
        act.startActivity(intent);
        act.finish();
        act.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    
    public static String getMinecraftType(Activity act){
        return act.getIntent().getStringExtra("type");
    }
    
    public static int dp2px(int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics()));
    }
    
    public static String stringToUnicode(String str) {
        char[] utfBytes = str.toCharArray();
        StringBuilder unicodeBytes = new StringBuilder();
        for (char utfByte : utfBytes) {
            String hexB = Integer.toHexString(utfByte);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes.append("\\u").append(hexB);
        }
        return unicodeBytes.toString();
    }
}
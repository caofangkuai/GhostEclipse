package cfks.bugjump.ghosteclipse.ui.fragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.preference.Preference;
import com.kiylx.m3preference.ui.BaseSettingsFragment;
import cfks.bugjump.ghosteclipse.R;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.PopTip;
import jiesheng.应用操作;

public class SettingsFragment extends BaseSettingsFragment{
    @Override
    public void onCreatePreferences(Bundle arg0, String arg1) {
        // TODO: Implement this method
        setPreferencesFromResource(R.xml.settings_preferences,arg1);
    }
    
    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        // TODO: Implement this method
        switch(preference.getKey()){
            case "music_list":
                PopTip.show("Not supported for now").iconWarning();
                break;
            case "about":
                MessageDialog.show("About", "GhostEclipse v" + getVersionName(getContext()) + " By caofangkuai", getContext().getString(android.R.string.ok));
                break;
        }
        return super.onPreferenceTreeClick(preference);
    }
    
    private String getVersionName(Context ctx){
        try {
			PackageManager packageManager = ctx.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(ctx.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
    }
}
package cfks.bugjump.ghosteclipse.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import java.io.File;

public class ContextUtils {

    private static final String SHARED_PREFERENCES_KEY = "ProtoHax_Caches";

    public static void writeString(Context context, String key, String property) {
        context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                .edit()
                .putString(key, property)
                .apply();
    }

    public static String readString(Context context, String key) {
        return context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                .getString(key, null);
    }

    public static boolean readBoolean(Context context, String key, boolean defaultValue) {
        try {
            return context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                    .getBoolean(key, defaultValue);
        } catch (Throwable t) {
            return "true".equals(context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                    .getString(key, null));
        }
    }

    public static void writeBoolean(Context context, String key, boolean value) {
        context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(key, value)
                .apply();
    }

    public static String readStringOrDefault(Context context, String key, String defaultValue) {
        String value = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                .getString(key, null);
        return value != null ? value : defaultValue;
    }

    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context context, int resId) {
        toast(context, context.getString(resId));
    }

    public static void shareTextAsFile(Context context, String text, String title) {
        File file = new File(new File(context.getCacheDir(), "share"), "ProtoHax-" + System.currentTimeMillis() + ".log");
        file.getParentFile().mkdirs();
        try {
            java.nio.file.Files.write(file.toPath(), text.getBytes());
            file.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, context.getPackageName(), file));
        shareIntent.setType("text/x-log");
        context.startActivity(Intent.createChooser(shareIntent, title));
    }

    public static boolean hasInternetPermission(PackageInfo packageInfo) {
        String[] permissions = packageInfo.requestedPermissions;
        return permissions != null && java.util.Arrays.stream(permissions).anyMatch(permission -> permission.equals(Manifest.permission.INTERNET));
    }

    public static ApplicationInfo getApplicationInfo(PackageManager packageManager, String packageName) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0L));
        } else {
            return packageManager.getApplicationInfo(packageName, 0);
        }
    }

    public static PackageInfo getPackageInfo(PackageManager packageManager, String packageName) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0L));
        } else {
            return packageManager.getPackageInfo(packageName, 0);
        }
    }

    public static boolean isAppExists(PackageManager packageManager, String packageName) {
        try {
            getApplicationInfo(packageManager, packageName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getApplicationName(PackageManager packageManager, String packageName) throws Exception {
        ApplicationInfo info = getApplicationInfo(packageManager, packageName);
        return packageManager.getApplicationLabel(info).toString();
    }

    public static boolean isNightMode(Context context) {
        return (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    public static int getColor(Context context, int day, int night) {
        return isNightMode(context) ? night : day;
    }
}
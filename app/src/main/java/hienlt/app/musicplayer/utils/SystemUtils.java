package hienlt.app.musicplayer.utils;

import android.app.ActivityManager;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

import hienlt.app.musicplayer.R;

/**
 * Created by hienl_000 on 4/20/2016.
 */
public class SystemUtils {

    private static DeviceType DEVICE_TYPE = null;

    public enum DeviceType {

        Smartphone,
        Tab_7,
        Tab_9,
        Tab_10,

        /**
         * Télévision
         */
        Leanback;

        public boolean isLargeTablet() {
            return this == Tab_10 || this == Tab_9;
        }
    }

    private SystemUtils() {

    }

    public static boolean hasNavBar(Context context) {
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if(!hasMenuKey && !hasBackKey)
            return true;
        return false;
    }

    public static int getStatusBarHeight(Resources resources) {
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
            return result;
        }
        return 0;
    }
    public static int getActionBarHeight(Context context) {
        final TypedArray ta = context.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int actionBarHeight = (int) ta.getDimension(0, 0);
        return actionBarHeight;
    }

    public static int getNavigationBarHeight(Resources resources) {
        int orientation = resources.getConfiguration().orientation;

        //Only phone between 0-599 has navigationbar can move
        boolean isSmartphone = resources.getConfiguration().smallestScreenWidthDp < 600;
        if (isSmartphone && android.content.res.Configuration.ORIENTATION_LANDSCAPE == orientation)
            return 0;

        int id = resources
                .getIdentifier(orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");
        if (id > 0)
            return resources.getDimensionPixelSize(id);

        return 0;
    }

    public static int getNavigationBarWidth(Resources resources) {
        int orientation = resources.getConfiguration().orientation;

        //Only phone between 0-599 has navigationbar can move
        boolean isSmartphone = resources.getConfiguration().smallestScreenWidthDp < 600;

        if (orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE && isSmartphone) {
            int id = resources.getIdentifier("navigation_bar_width", "dimen", "android");
            if (id > 0)
                return resources.getDimensionPixelSize(id);
        }

        return 0;
    }

    public static int getDefaultActionBarHeight(Resources resources) {
        return resources.getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material);
    }

    public static boolean canEnableTranslucentDecor(Resources resources) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return false;

        int id = resources.getIdentifier("config_enableTranslucentDecor", "bool", "android");
        if (id > 0)
            return resources.getBoolean(id);

        return true;
    }

    public static void displayScreenInfos(Context context) {
        Resources r = context.getResources();
        Log.d("screen", "scaledDensity: " + r.getDisplayMetrics().scaledDensity);
        Log.d("screen", "widthPixels x heightPixels: " + r
                .getDisplayMetrics().widthPixels + " x " + r.getDisplayMetrics().heightPixels);
        Log.d("screen", "xdpi x ydpi: " + r.getDisplayMetrics().xdpi + " x " + r
                .getDisplayMetrics().ydpi);
        Log.d("screen", "screenWidthDp x screenHeightDp: " + r
                .getConfiguration().screenWidthDp + " x " + r.getConfiguration().screenHeightDp);
        Log.d("screen", "smallestScreenWidthDp: " + r.getConfiguration().smallestScreenWidthDp);
        UiModeManager uiModeManager = (UiModeManager) context
                .getSystemService(context.UI_MODE_SERVICE);
        Log.d("screen", "UIMode:" + uiModeManager
                .getCurrentModeType() + "  1= normal, 2=desk, 3=car, 4=TV, 5=appliance, 6=Watch");

    }

    public static int getSmallestDeviceWidth(Resources r) {
        return r.getConfiguration().smallestScreenWidthDp;
    }

    public static int getSmallestDeviceWidthInPx(Resources r) {
        return r.getConfiguration().smallestScreenWidthDp * (int) r.getDisplayMetrics().density;
    }


    public static boolean canAnimateTransition() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static int convertDpToPx(Resources r, int dp) {
        Resources resources = r;
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = (int)(dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
    public static int convertPxToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int dp = (int)(px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int pixelsToSp(Context context, int px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (px/scaledDensity);
    }

    public static int SpTopixels(Context context, int sp) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp*scaledDensity);
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}


package hienlt.app.musicplayer.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hienlt.app.musicplayer.R;

/**
 * Created by hienl_000 on 4/6/2016.
 */
public class Common {
    public static final String TAG = "hienlt0610";
    public static final boolean IS_SHOW_LOG = true;

    public static final String ALBUM_CACHE_FOLDER = "Album";
    public static final String ARTIST_CACHE_FOLDER = "Artist";
    public static final String LYRIC_CACHE_FOLDER = "Lyrics";
    public static final String BACKGROUND_CACHE_FOLDER = "Background";

    //Background key
    public static final String DEFAULT_BACKGROUND = "default_bkg";
    public static final String BACKGROUND_ID = "bkg_id";
    public static final String MY_BACKGROUND = "my_bkg";
    public static final String ACTION_BACKGROUND_CHANGED = "hienlt.app.musicplayer.ACTION_BACKGROUND_CHANGED";

    public static final String FIRST_LAUCH = "first_lauch";

    //name gửi list qua intent
    public static final String LIST_SONG = "list_songs";
    public static final String CURRENT_PLAY = "current_play";
    /**
     * Fast Show Toast message
     *
     * @param context
     * @param message
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Show Log with message
     *
     * @param message
     */
    public static void showLog(String message) {
        Log.e(TAG, message);
    }

    /**
     * Show Log with custom Tag and Message
     *
     * @param tagName
     * @param message
     */
    public static void showLog(String tagName, String message) {
        if(IS_SHOW_LOG)
            Log.d(tagName, message);
    }


    public static void showAlertDialog(final Context context, String title, String message) {
        //Init dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //Setting Dialog
        builder.setTitle(title);
        builder.setMessage(message);
        //Setting OK button
        builder.setPositiveButton("OK", null);
        //Show Dialog
        builder.show();
    }

    public static void showNotification(Context c, String title, String text) {

        Notification.Builder builder = new Notification.Builder(c);

        builder.setSmallIcon(R.drawable.ic_menu_camera)
                .setContentTitle(title)
                .setContentText(text);

        NotificationManager manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }

    public static String generateUUID(String str) {
        UUID uuid = UUID.nameUUIDFromBytes(str.getBytes());
        long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        return Long.toString(l, Character.MAX_RADIX);
    }

    public static String getConvertedTime(long lnValue) {     //OK
        String lcStr = "00:00:00";
        String lcSign = (lnValue >= 0 ? " " : "-");
        lnValue = lnValue * (lnValue >= 0 ? 1 : -1);

        if (lnValue > 0) {
            long lnHor = (lnValue / 3600);
            long lnHor1 = (lnValue % 3600);
            long lnMin = (lnHor1 / 60);
            long lnSec = (lnHor1 % 60);

            lcStr = lcSign + (lnHor < 10 ? "0" : "") + String.valueOf(lnHor) + ":" +
                    (lnMin < 10 ? "0" : "") + String.valueOf(lnMin) + ":" +
                    (lnSec < 10 ? "0" : "") + String.valueOf(lnSec);
        }

        return lcStr;
    }

    public static String miliSecondToString(long pTime) {
        int second = (int) (pTime/1000);
        return String.format("%02d:%02d", second/60, second % 60);
    }

    public static void setBackground(Context context, View v, Bitmap bitmap){
        Drawable drawable = ImageUtils.bitmapToDrawable(context.getResources(),bitmap);
        if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
            v.setBackgroundDrawable(drawable);
        else
            v.setBackground(drawable);
    }

    public static void setBackground(Context context, View v, Drawable drawable){
        if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
            v.setBackgroundDrawable(drawable);
        else
            v.setBackground(drawable);
    }

    public static int percentSeekbar(int msCurrent, int msSongEnd){
        return (int)((msCurrent * 100.0f) / msSongEnd);
    }

    public static int msCurrentSeekbar(int percent, int msSongEnd){
        return (msSongEnd*percent)/100;
    }

    /**
     * @return true when the caller API version is at least ICS 14
     */
    public static boolean isHoneycomb(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * @return true when the caller API version is at least ICS 14
     */
    public static boolean isIceCreamSandwich(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * @return true when the caller API version is at least jellyBean 16
     */
    public static boolean isJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * @return true when the caller API version is at least Kitkat 19
     */
    public static boolean isKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * @return true when the caller API version is at least lollipop 21
     */
    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * @return true when the caller API version is at least lollipop 21
     */
    public static boolean isMarshMallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * Returns a list with all links contained in the input
     */
    public static List<String> extractUrls(String text)
    {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }
    public static String getVideoId(@NonNull String videoUrl) {
        String reg = "(?:youtube(?:-nocookie)?\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})";
        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(videoUrl);

        if (matcher.find())
            return matcher.group(1);
        return null;
    }

    public static String getRootCacheDir() {
        String dir;
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (isSDPresent) {
            dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CornTek";
        } else {
            dir = Environment.getDataDirectory().getAbsolutePath() + File.separator + "CornTek";
        }
        return dir;
    }
}

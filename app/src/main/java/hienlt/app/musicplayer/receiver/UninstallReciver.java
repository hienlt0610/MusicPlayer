package hienlt.app.musicplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import hienlt.app.musicplayer.media.MusicService;
import hienlt.app.musicplayer.provider.MusicDBLoader;
import hienlt.app.musicplayer.utils.Common;

/**
 * Created by hienl_000 on 5/6/2016.
 */
public class UninstallReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName=intent.getData().getEncodedSchemeSpecificPart();
        if(packageName.equalsIgnoreCase(context.getPackageName())){
//            context.deleteDatabase(MusicDBLoader.DATABASE_NAME);
//            Common.showLog("Uninstall App and Delete Database");
        }
    }
}

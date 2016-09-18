package hienlt.app.musicplayer.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import hienlt.app.musicplayer.media.MusicService;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.SystemUtils;

/**
 * Created by hienl_000 on 5/11/2016.
 */
public class IncomingCall extends BroadcastReceiver {
    private static boolean incomingFlag = false;
    private static String incoming_number = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!SystemUtils.isMyServiceRunning(context, MusicService.class)) return;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            incomingFlag = false;
            Intent intentService = new Intent(context, MusicService.class);
            intentService.setAction(MusicService.ACTION_PAUSE);
            context.startService(intentService);
            Common.showLog("BroadCast: ACTION_NEW_OUTGOING_CALL");
        } else {
            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Intent intentService = new Intent(context, MusicService.class);
                    intentService.setAction(MusicService.ACTION_PAUSE);
                    context.startService(intentService);
                    Common.showLog("BroadCast: RINGING");
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    intentService = new Intent(context, MusicService.class);
                    intentService.setAction(MusicService.ACTION_TOGGLE_PLAY_PAUSE);
                    context.startService(intentService);
                    Common.showLog("incoming IDLE");
                    break;
            }
        }
    }
}

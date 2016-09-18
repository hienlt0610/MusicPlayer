package hienlt.app.musicplayer.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import hienlt.app.musicplayer.interfaces.IMusicServiceConnection;

/**
 * Created by hienl_000 on 4/16/2016.
 */
public class MusicServiceConnection {
    Context context;
    MusicService mService;
    IMusicServiceConnection connection;
    Intent intent;
    public MusicServiceConnection(Context context){
        this.context = context;
    }

    public void connect(Intent intent,IMusicServiceConnection connection){
        this.connection = connection;
        context.startService(intent);
        context.bindService(intent, mConnection, 0);
        this.intent = intent;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((MusicService.LocalBinder)service).getService();
            connection.onConnected(mService);
            context.unbindService(mConnection);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}

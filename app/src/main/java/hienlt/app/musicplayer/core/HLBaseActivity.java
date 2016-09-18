package hienlt.app.musicplayer.core;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.interfaces.IMusicServiceConnection;
import hienlt.app.musicplayer.media.MusicService;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.utils.Common;

/**
 * Created by hienl_000 on 4/23/2016.
 */
public abstract class HLBaseActivity extends AppCompatActivity {
    protected Toolbar toolbar;
    private IMusicServiceConnection iConnection = null;
    private MusicService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        settingToolbar();
    }

    private void settingToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                if (getHomeAsUpIndicator() != 0) {
                    actionBar.setHomeAsUpIndicator(getHomeAsUpIndicator());
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (iConnection != null) {
            Intent iMusicService = new Intent(this, MusicService.class);
            bindService(iMusicService, connection, 0);
        }

    }


    @Override
    protected void onStop() {
        if (iConnection != null) {
            Intent iMusicService = new Intent(this, MusicService.class);
            unbindService(connection);
        }
        super.onStop();
    }


    public void replaceFragment(Fragment frag, boolean saveInBackstack, boolean animate) {
        String backStateName = ((Object) frag).getClass().getName();

        try {
            FragmentManager manager = getSupportFragmentManager();
            boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

            if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) {
                //fragment not in back stack, create it.
                FragmentTransaction transaction = manager.beginTransaction();

                if (animate) {
                    Common.showLog("Change Fragment: animate");
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                }

                transaction.replace(R.id.container, frag, backStateName);

                if (saveInBackstack) {
                    Common.showLog("Change Fragment: addToBackTack " + backStateName);
                    transaction.addToBackStack(backStateName);
                } else {
                    Common.showLog("Change Fragment: NO addToBackTack");
                }

                transaction.commit();
            } else {
                // custom effect if fragment is already instanciated
            }
        } catch (IllegalStateException exception) {
            Common.showLog("Unable to commit fragment, could be activity as been killed in background. " + exception.toString());
        }
    }

    /**
     * Open a new Activity by class name
     *
     * @param pClass
     */
    protected void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

    /**
     * Open a new Activity and bring some parameters by bundle
     *
     * @param pClass
     * @param bundle
     */
    protected void openActivity(Class<?> pClass, Bundle bundle) {
        Intent intent = new Intent(this, pClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * Open a new Activity by action string
     *
     * @param action
     */
    protected void openActivity(String action) {
        openActivity(action, null);
    }

    /**
     * Open a new Activity by action string and bring some parameters
     *
     * @param action
     * @param pBundle
     */
    protected void openActivity(String action, Bundle pBundle) {
        Intent intent = new Intent(action);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }


    protected int getHomeAsUpIndicator() {
        return 0;
    }

    protected abstract int getLayout();

    protected void addOnMusicServiceListener(IMusicServiceConnection connection) {
        this.iConnection = connection;
    }

    public ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            mService = binder.getService();
            iConnection.onConnected(mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

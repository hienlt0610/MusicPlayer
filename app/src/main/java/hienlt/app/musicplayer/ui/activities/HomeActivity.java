package hienlt.app.musicplayer.ui.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.core.HLBaseActivity;
import hienlt.app.musicplayer.interfaces.IMusicServiceConnection;
import hienlt.app.musicplayer.media.MusicService;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.ui.fragments.HomeFragment;
import hienlt.app.musicplayer.utils.CacheManager;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.DiskLruImageCache;
import hienlt.app.musicplayer.utils.FragmentUtils;
import hienlt.app.musicplayer.utils.GaussianBlur;
import hienlt.app.musicplayer.utils.Settings;
import hienlt.app.musicplayer.utils.SystemUtils;

public class HomeActivity extends HLBaseActivity implements OnClickListener, IMusicServiceConnection {

    private static final int permsRequestCode = 100;
    public ResideMenu resideMenu;
    private ImageView imgBackground, imgAlbumArt;
    private LinearLayout currentPlayBar;
    private TextView tvSongTitle, tvArtist;
    private ImageButton btnTooglePlay, btnNext;
    private MusicService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.META_CHANGE);
        filter.addAction(MusicService.PLAY_STATE_CHANGE);
        filter.addAction(MusicService.EXIT);
        filter.addAction(Common.ACTION_BACKGROUND_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        checkFirstLauch();
        //find view
        imgBackground = (ImageView) findViewById(R.id.imgBackground);
        currentPlayBar = (LinearLayout) findViewById(R.id.current_play_bar);
        tvSongTitle = (TextView) findViewById(R.id.tvSongTitle);
        tvArtist = (TextView) findViewById(R.id.tvArtist);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnTooglePlay = (ImageButton) findViewById(R.id.btnTogglePlay);
        imgAlbumArt = (ImageView) findViewById(R.id.imgAlbumArt);
        if (SystemUtils.isMyServiceRunning(this, MusicService.class))
            addOnMusicServiceListener(this);
        //set on click
        currentPlayBar.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnTooglePlay.setOnClickListener(this);

        //init
        resideMenu = new ResideMenu(this);
        resideMenu.attachToActivity(this);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        resideMenu.setScaleValue(0.5f);
        resideMenu.setShadowVisible(true);

        if (Common.isMarshMallow()) {
            if (!(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permission, permsRequestCode);
            }
        }

        setBackground();
        initResideMenuList();
        initHome();
        updatePlayBar();

    }

    /**
     * Thực thi khi người dùng mới cài app
     */
    private void checkFirstLauch() {
        boolean isFirstLauch = Settings.getInstance().get("first_lauch", true);
        if (isFirstLauch) {
            //Set background mặc định cho người dùng
            Settings.getInstance().put(Common.DEFAULT_BACKGROUND, true);
            Settings.getInstance().put(Common.BACKGROUND_ID, R.drawable.bg2);
            Settings.getInstance().put(Common.FIRST_LAUCH, false);
        }
    }

    private void initHome() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
            FragmentUtils.addStackFragment(getSupportFragmentManager(), new HomeFragment(), false, false);
    }

    private void initResideMenuList() {
        ResideMenuItem menuSetting = new ResideMenuItem(this, R.drawable.ic_act_setting, "Cài đặt");
        ResideMenuItem menuBackground = new ResideMenuItem(this, R.drawable.ic_act_background, "Hình nền");
        ResideMenuItem menuExit = new ResideMenuItem(this, R.drawable.ic_act_exit, "Thoát");

        menuSetting.setId(1);
        menuBackground.setId(2);
        menuExit.setId(3);

        menuSetting.setOnClickListener(this);
        menuBackground.setOnClickListener(this);
        menuExit.setOnClickListener(this);

        resideMenu.addMenuItem(menuSetting, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(menuBackground, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(menuExit, ResideMenu.DIRECTION_LEFT);

//        addSeparatorAfter(menuSetting);
//        addSeparatorAfter(menuBackground);
//        addSeparatorAfter(menuExit);
//        setMargin(menuSetting);
//        setMargin(menuBackground);
//        setMargin(menuExit);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Fragment fr = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fr != null) {
            fr.onOptionsItemSelected(item);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case permsRequestCode:
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void setBackground() {
        Bitmap bitmap = null;
        boolean isDefaultBkg = Settings.getInstance().get(Common.DEFAULT_BACKGROUND, false);
        if (isDefaultBkg) {
            int bkgID = Settings.getInstance().get(Common.BACKGROUND_ID, 0);
            bitmap = BitmapFactory.decodeResource(getResources(), bkgID);
        } else {
            String path = Settings.getInstance().get(Common.MY_BACKGROUND, null);
            bitmap = BitmapFactory.decodeFile(path);
        }
        if (bitmap == null) return;

        Bitmap brightnessBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmap = GaussianBlur.getInstance(this).setRadius(5).render(bitmap, true);
        Common.setBackground(this, imgBackground, bitmap);

        Canvas canvas = new Canvas(brightnessBitmap);
        Paint alphaPaint = new Paint();
        alphaPaint.setColor(Color.BLACK);
        alphaPaint.setAlpha(130);
        canvas.drawRect(0, 0, brightnessBitmap.getWidth(), brightnessBitmap.getHeight(), alphaPaint);
        Common.setBackground(this, resideMenu, brightnessBitmap);
    }

    private void updatePlayBar() {
        if (mService == null || mService.getState() == MusicService.MusicState.Stop) {
            currentPlayBar.setVisibility(View.GONE);
            return;
        }
        currentPlayBar.setVisibility(View.VISIBLE);
        Song song = mService.getCurrentSong();
        if (song == null) return;
        tvSongTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist());
        if (mService.getState() == MusicService.MusicState.Playing)
            btnTooglePlay.setImageResource(R.drawable.ic_noti_pause);
        else
            btnTooglePlay.setImageResource(R.drawable.ic_noti_play);

        // Đang nghe nhạc trên thẻ
        if (mService.getMediaType() == MusicService.MediaType.Local) {
            DiskLruImageCache albumCache = CacheManager.getInstance().getAlbumCache();
            if (albumCache != null) {
                if (albumCache.containsKey(song.getAlbum())) {
                    Bitmap bitmap = albumCache.getBitmap(song.getAlbum());
                    imgAlbumArt.setImageBitmap(bitmap);
                } else {
                    imgAlbumArt.setImageResource(R.drawable.adele);
                }
            }
        } else if (mService.getMediaType() == MusicService.MediaType.Youtube) {
            Common.showLog(mService.getMediaType().toString());
            if (mService.youtubeInfo == null) return;
            Picasso.with(this).load(mService.youtubeInfo.getThumbnailUrl()).memoryPolicy(MemoryPolicy.NO_CACHE).error(R.drawable.adele).into(imgAlbumArt);
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_home;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.current_play_bar:
                Intent intent = new Intent(HomeActivity.this, PlaybackActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.no_change);
                break;
            case R.id.btnNext:
                Intent i = new Intent(HomeActivity.this, MusicService.class);
                i.setAction(MusicService.ACTION_NEXT);
                startService(i);
                break;
            case R.id.btnTogglePlay:
                Intent i1 = new Intent(HomeActivity.this, MusicService.class);
                i1.setAction(MusicService.ACTION_TOGGLE_PLAY_PAUSE);
                startService(i1);
                break;
            case 1:
                Intent intentSetting = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intentSetting);
                break;
            case 2:
                Intent intentBackground = new Intent(HomeActivity.this, BackgroundActivity.class);
                startActivity(intentBackground);
                break;
            case 3:
                finish();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (resideMenu.isOpened())
            resideMenu.closeMenu();
        else
            super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onConnected(MusicService service) {
        mService = service;
        updatePlayBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    /**
     * Receive the service call
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Common.showLog("BroadCast action: " + intent.getAction());
            String action = intent.getAction();
            if (action.equals(MusicService.META_CHANGE)) {
                updatePlayBar();
            }
            if (action.equals(MusicService.PLAY_STATE_CHANGE)) {
                updatePlayState();
            }
            if (action.equals(MusicService.EXIT)) {
                currentPlayBar.setVisibility(View.GONE);
            }
            if (action.equals(Common.ACTION_BACKGROUND_CHANGED)) {
                setBackground();
                if (resideMenu.isOpened())
                    resideMenu.closeMenu();
            }
        }
    };

    private void updatePlayState() {
        if (mService == null) return;
        if (mService.getState() == MusicService.MusicState.Playing) {
            btnTooglePlay.setImageResource(R.drawable.ic_noti_pause);
        } else {
            btnTooglePlay.setImageResource(R.drawable.ic_noti_play);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent(ScanActivity.ACTION_SCAN_SUCCESS);
        LocalBroadcastManager.getInstance(HomeActivity.this).sendBroadcast(intent);
    }

    private void addSeparatorAfter(ResideMenuItem menuItem) {
        LinearLayout parent = (LinearLayout) ((ViewGroup) menuItem.getParent()).getParent();

        View separator = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SystemUtils.convertDpToPx(getResources(), 2));
        params.setMargins(SystemUtils.convertDpToPx(getResources(), 5), SystemUtils.convertDpToPx(getResources(), 5), SystemUtils.convertDpToPx(getResources(), 5), SystemUtils.convertDpToPx(getResources(), 5));
        separator.setLayoutParams(params);
        separator.setBackgroundColor(0xFFCCCCCC);

        parent.addView(separator);
    }

    private void setMargin(ResideMenuItem item) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) item.getLayoutParams();
        params.setMargins(0, SystemUtils.convertDpToPx(getResources(), 15), 0, 0);
        item.setLayoutParams(params);
    }
}

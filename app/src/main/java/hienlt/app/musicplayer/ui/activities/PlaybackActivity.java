package hienlt.app.musicplayer.ui.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.adapter.PlaybackPagerAdapter;
import hienlt.app.musicplayer.core.HLBaseActivity;
import hienlt.app.musicplayer.interfaces.IMusicServiceConnection;
import hienlt.app.musicplayer.media.MusicService;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.ui.dialog.PlaylistDialog;
import hienlt.app.musicplayer.ui.fragments.LyricFragment;
import hienlt.app.musicplayer.utils.CacheManager;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.DiskLruFileCache;
import hienlt.app.musicplayer.utils.DiskLruImageCache;
import hienlt.app.musicplayer.utils.GaussianBlur;
import hienlt.app.musicplayer.utils.Settings;

/**
 * Created by hienl_000 on 4/14/2016.
 */
public class PlaybackActivity extends HLBaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, IMusicServiceConnection {

    ImageView imgBackground;
    public MusicService mService;
    private TextView tvCurrentPlay, tvEndPlay;
    private SeekBar songProgressBar;
    private Handler handler;
    private ImageButton btnPlay, btnNext, btnPrevious, btnShuffle, btnRepeat, btnPlaylist;
    ViewPager pager;
    PlaybackPagerAdapter adapter;
    LyricFragment lyricFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Find view
        imgBackground = (ImageView) findViewById(R.id.imgBackground);
        tvCurrentPlay = (TextView) findViewById(R.id.tvCurrentPlay);
        tvEndPlay = (TextView) findViewById(R.id.tvEndPlay);
        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
        btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
        btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
        pager = (ViewPager) findViewById(R.id.pager);
        handler = new Handler();

        //set listener
        btnPlay.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnShuffle.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);
        btnPlaylist.setOnClickListener(this);
        addOnMusicServiceListener(this);
        songProgressBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btnPlay:
                intent = new Intent(PlaybackActivity.this, MusicService.class);
                intent.setAction(MusicService.ACTION_TOGGLE_PLAY_PAUSE);
                startService(intent);
                break;
            case R.id.btnNext:
                intent = new Intent(PlaybackActivity.this, MusicService.class);
                intent.setAction(MusicService.ACTION_NEXT);
                startService(intent);
                break;
            case R.id.btnPrevious:
                intent = new Intent(PlaybackActivity.this, MusicService.class);
                intent.setAction(MusicService.ACTION_PREVIOUS);
                startService(intent);
                break;
            case R.id.btnShuffle:
                if (mService == null) return;
                mService.setShuffle(!mService.isShuffle(), true);
                updateShuffleRepeatState();
                break;
            case R.id.btnRepeat:
                if (mService == null) return;
                mService.setRepeat(!mService.isRepeat(), true);
                updateShuffleRepeatState();
                break;
            case R.id.btnPlaylist:
                if (mService != null) {
                    final ArrayList<Song> songs = mService.getPlaylistSong();
                    if (songs != null) {
                        final PlaylistDialog dialog = new PlaylistDialog(this);
                        dialog.setListSong(new ArrayList<Song>(songs));
                        dialog.setCurrentPlay(mService.getCurrentSong());
                        dialog.setOnDeleteClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int position = (int) v.getTag();
                                songs.remove(position);
                                dialog.replaceList(songs);
                            }
                        });
                        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (mService != null) {
                                    mService.setSongPosition(position);
                                    mService.playSong();
                                    dialog.replaceCurrentPlay(songs.get(position));
                                }
                            }
                        });
                        dialog.show();
                    }
                }
                break;

        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(mUpdateTimeTask);
        if (mService == null || mService.getState() == MusicService.MusicState.Stop) return;
        int currPlay = Common.msCurrentSeekbar(seekBar.getProgress(), mService.getDuration());
        mService.seekTo(currPlay);
        updateTimeplay();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_playback;
    }

    public void setBackground(Bitmap bitmap, boolean blur) {
        if (blur) {
            bitmap = GaussianBlur.getInstance(this).setRadius(10).render(bitmap, true);
        }
        //Common.setBackground(this, imgBackground, bitmap);
        imgBackground.setImageBitmap(bitmap);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        if (id == R.id.action_timmer) {
            Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_timer, null);
            dialog.setContentView(view);
            SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekMinute);
            final TextView tvTime = (TextView) view.findViewById(R.id.tvTime);

            if (mService != null && mService.getSecondsCountdown() > 0) {
                seekBar.setProgress((mService.getSecondsCountdown() / 60) + 1);
                tvTime.setText("Tắt nhạc sau: " + ((mService.getSecondsCountdown() / 60) + 1) + " phút");
            } else {
                seekBar.setProgress(0);
                tvTime.setText("Tắt nhạc sau: " + 0 + " phút");
            }

            dialog.show();

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    tvTime.setText("Tắt nhạc sau: " + progress + " phút");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int minutes = seekBar.getProgress();
                    if (mService != null)
                        mService.setTimer(minutes);
                }
            });

        }
        return super.onOptionsItemSelected(item);
    }

    private void updateTrackInfo() {
        if (mService == null) return;
        Song song = mService.getCurrentSong();
        if (song == null) return;

        getSupportActionBar().setTitle(song.getTitle());
        getSupportActionBar().setSubtitle(song.getArtist());
        if (mService.getState() == MusicService.MusicState.Playing)
            tvEndPlay.setText(Common.miliSecondToString(mService.getDuration()));
        if (mService.getMediaType() == MusicService.MediaType.Local) {
//            DiskLruImageCache albumCache = CacheManager.getInstance().getAlbumCache();
//            if (albumCache != null) {
//                Bitmap bitmap = null;
//                if (albumCache.containsKey(song.getAlbum())) {
//                    bitmap = albumCache.getBitmap(song.getAlbum());
//                    setBackground(bitmap, false);
//                } else {
//                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.adele);
//                    setBackground(bitmap, false);
//                }
//            }
            if (song.getAlbumPicture() != null) {
                setBackground(song.getAlbumPicture(), false);
            } else {
                boolean isDefaultBackground = Settings.getInstance().get(Common.DEFAULT_BACKGROUND, false);
                Bitmap falseBkg = BitmapFactory.decodeResource(getResources(), R.drawable.adele);
                if (isDefaultBackground) {
                    int bkgId = Settings.getInstance().get(Common.BACKGROUND_ID, 0);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), bkgId);
                    if (bitmap != null)
                        setBackground(bitmap, false);
                    else
                        setBackground(falseBkg, false);
                } else {
                    String path = Settings.getInstance().get(Common.MY_BACKGROUND, "");
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    if (bitmap != null)
                        setBackground(bitmap, false);
                    else
                        setBackground(falseBkg, false);
                }

            }
        } else if (mService.getMediaType() == MusicService.MediaType.Youtube) {
            if (mService.youtubeInfo != null)
                Picasso.with(this).load(mService.youtubeInfo.getThumbnailUrl()).error(R.drawable.adele).memoryPolicy(MemoryPolicy.NO_CACHE).into(imgBackground);
        }
    }

    private void updatePlayState() {
        if (mService == null) return;
        if (mService.getState() == MusicService.MusicState.Playing) {
            btnPlay.setImageResource(R.drawable.playback_pause_selector);
        } else {
            btnPlay.setImageResource(R.drawable.playback_play_selector);
        }
    }

    private void updateShuffleRepeatState() {
        if (mService == null) return;
        if (mService.isShuffle())
            btnShuffle.setImageResource(R.drawable.img_playback_shuffle_press);
        else
            btnShuffle.setImageResource(R.drawable.img_playback_shuffle);

        if (mService.isRepeat())
            btnRepeat.setImageResource(R.drawable.img_playback_repeat_press);
        else
            btnRepeat.setImageResource(R.drawable.img_playback_repeat);
    }

    private void updateTimeplay() {
        handler.removeCallbacks(mUpdateTimeTask);
        handler.post(mUpdateTimeTask);
        mUpdateTimeTask.run();
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            if (mService != null) {
                if (mService.getState() == MusicService.MusicState.Playing) {
                    tvCurrentPlay.setText(Common.miliSecondToString(mService.getCurrentPosition()));
                    songProgressBar.setProgress(Common.percentSeekbar(mService.getCurrentPosition(), mService.getDuration()));

                    if (lyricFragment == null) {
                        lyricFragment = (LyricFragment) adapter.getPagerFragment(pager, 0);
                    } else {
                        lyricFragment.seekLrcToTime(mService.getCurrentPosition());
                    }
                }
                songProgressBar.setSecondaryProgress(mService.getBufferingPercent());
                handler.postDelayed(mUpdateTimeTask, 200);
            }
        }
    };

    @Override
    public void onConnected(MusicService service) {
        mService = service;

        // Khởi tạo viewpager
        initViewPager();
        // Update thông tin
        updateTrackInfo();
        // Update trạng thái nút bấm
        updatePlayState();
        // Update thời gian phát nhạc
        updateTimeplay();
        updateShuffleRepeatState();
    }

    private void initViewPager() {
        adapter = new PlaybackPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.META_CHANGE);
        filter.addAction(MusicService.PLAY_STATE_CHANGE);
        filter.addAction(MusicService.EXIT);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    /**
     * Receive the service call
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Common.showLog("BroadCast action: " + intent.getAction() + " - " + mService.getState().toString());
            String action = intent.getAction();
            if (action.equals(MusicService.META_CHANGE)) {
                updateTrackInfo();
                lyricFragment = (LyricFragment) adapter.getPagerFragment(pager, 0);
                lyricFragment.updateLyric();
            }
            if (action.equals(MusicService.PLAY_STATE_CHANGE)) {
                updateTimeplay();
                updatePlayState();
                if (mService.getState() == MusicService.MusicState.Preparing || mService.getState() == MusicService.MusicState.Stop) {
                    songProgressBar.setProgress(0);
                    songProgressBar.setSecondaryProgress(0);
                    tvCurrentPlay.setText("00:00");
                }
            }
            if (action.equals(MusicService.EXIT)) {
                finish();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playing, menu);
        return true;
    }

    public void onLrcSeeked(int ms) {
        if (mService == null) return;
        if (mService.getState() == MusicService.MusicState.Stop) return;
        mService.seekTo(ms);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.no_change, R.anim.slide_out_down);
    }
}

package hienlt.app.musicplayer.media;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.models.YoutubeInfo;
import hienlt.app.musicplayer.network.VolleyConnection;
import hienlt.app.musicplayer.provider.MusicDBLoader;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.ui.activities.PlaybackActivity;
import hienlt.app.musicplayer.ui.dialog.PlaylistDialog;
import hienlt.app.musicplayer.utils.CacheManager;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.DiskLruFileCache;
import hienlt.app.musicplayer.utils.DiskLruImageCache;
import hienlt.app.musicplayer.utils.Settings;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnInfoListener, MediaPlayer.OnSeekCompleteListener {
    // Binder given to clients
    private IBinder mBinder = new LocalBinder();

    //Music Player
    private MediaPlayer mediaPlayer;

    public YoutubeInfo youtubeInfo;

    public int getBufferingPercent() {
        return bufferingPercent;
    }

    //Enum State music
    public enum MusicState {
        Preparing,
        Stop,
        Playing,
        Pause,
        Buffering
    }

    public enum MediaType {
        Local,
        Youtube
    }

    //State of Music player
    private MusicState state = MusicState.Stop;
    //Type of MusicPlayer
    private MediaType mediaType = MediaType.Local;
    //Current position music playing
    private int songPosn;
    //random
    private Random rand;
    //Playlist Music
    private ArrayList<Song> listSongs;
    //history played
    //Flag is shuffle
    private boolean isShuffle = false;
    //Flag is repeat
    private boolean isRepeat = false;
    //Notification ID
    private static final int NOTIFY_ID = 1;
    //Max history store
    private static final int MAX_HISTORY_STORE = 10;
    //List history played
    private Vector<Integer> historys;
    private Timer timer;
    private int secondsCountdown = 0;
    private int bufferingPercent = 0;

    public static final String ACTION_PLAY = "hienlt.app.musicplayer.ACTION_PLAY";
    public static final String ACTION_TOGGLE_PLAY_PAUSE = "hienlt.app.musicplayer.ACTION_TOGGLE_PLAY_PAUSE";
    public static final String ACTION_PAUSE = "hienlt.app.musicplayer.ACTION_PAUSE";
    public static final String ACTION_NEXT = "hienlt.app.musicplayer.ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "hienlt.app.musicplayer.ACTION_PREVIOUS";
    public static final String ACTION_EXIT = "hienlt.app.musicplayer.ACTION_EXIT";
    public static final String ACTION_PARSE_YOUTUBE = "hienlt.app.musicplayer.ACTION_PARSE_YOUTUBE";

    public static final String META_CHANGE = "hienlt.app.musicplayer.META_CHANGE";
    public static final String PLAY_STATE_CHANGE = "hienlt.app.musicplayer.STATE_CHANGE";
    public static final String EXIT = "hienlt.app.musicplayer.EXIT";

    @Override
    public void onCreate() {
        super.onCreate();
        Common.showLog("Service onCreate");
        initAnother();
        initMusicPlayer();
    }

    private void initAnother() {
        songPosn = 0;
        rand = new Random();
        historys = new Vector<>();
        isShuffle = Settings.getInstance().get("is_shuffle", false);
        isRepeat = Settings.getInstance().get("is_repeat", false);
    }

    /**
     * Initialize MusicPlayer
     */
    private void initMusicPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(MusicService.this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
    }

    /**
     * Action to ic_noti_play the song
     */
    public void playSong() {
        if (mediaPlayer == null) return;
        if (listSongs == null || listSongs.size() == 0) return;
        Song song = listSongs.get(songPosn);
        try {
            mediaPlayer.reset();
            String url = song.getLocalDataSource().trim();
            mediaPlayer.setDataSource(url);
            song.setAlbumPicture(getSongPicture(song));
            mediaPlayer.prepareAsync();
            notifyClients(PLAY_STATE_CHANGE);
            setState(MusicState.Preparing);
            //Tự động tải lyric
            autoFindDownloadLyric();
        } catch (Exception e) {
            Common.showLog("Error setting data source");
        }

    }

    private void autoFindDownloadLyric() {
        if (getState() == MusicState.Stop) return;
        if (getCurrentSong() == null) return;
        if (CacheManager.getInstance().getLyricCache().containsKey(getCurrentSong().getTitle()))
            return;
        String songTitle = Uri.encode(getCurrentSong().getTitle());
        String artist = Uri.encode(getCurrentSong().getArtist());
        String keySearch = songTitle + "+" + artist;
        String url = "http://mp3.zing.vn/suggest/search?term=" + keySearch;
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response == null) return;
                try {
                    JSONObject songs = response.getJSONObject("song");
                    JSONArray listSong = songs.getJSONArray("list");
                    if (listSong.length() == 0) {
                        tryToFindMusic();
                        return;
                    }
                    String songId = listSong.getJSONObject(0).getString("object_id");
                    requestSongInfo(songId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Common.showLog(error.toString());
                if (error instanceof NoConnectionError) {
                }
            }
        });
        VolleyConnection.getInstance(this).addRequestToQueue(request);
    }

    public void tryToFindMusic() {
        String songTitle = Uri.encode(getCurrentSong().getTitle() + " ");
        String artist = Uri.encode(" " + getCurrentSong().getArtist());
        String keySearch = songTitle + "+" + artist;
        String url = "http://mp3.zing.vn/tim-kiem/bai-hat.html?q=" + keySearch;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document document = Jsoup.parse(response);
                Element item = document.select("div.item-song").first();
                if (item != null) {
                    String id = item.attr("data-id");
                    try {
                        requestSongInfo(id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Common.showLog("tryToFindMusic: " + error.toString());
            }
        });
        VolleyConnection.getInstance(this).addRequestToQueue(request);
    }

    private void requestSongInfo(String songId) throws JSONException {
        JSONObject idObj = new JSONObject();
        idObj.put("id", songId);
        String url = "http://api.mp3.zing.vn/api/mobile/song/getsonginfo?requestdata=" + idObj.toString();
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject responseObj = response.getJSONObject("response");
                    if (responseObj.has("is_error")) {
                        Common.showLog(responseObj.getString("msg"));
                        return;
                    }
                    String lyricUrl = response.getString("lyrics_file");
                    String download320 = response.getJSONObject("source").getString("320");
                    Common.showLog("lyric: " + lyricUrl);
                    Common.showLog("download: " + download320);
                    DiskLruFileCache lyricCache = CacheManager.getInstance().getLyricCache();
                    if (lyricUrl != null && lyricCache != null && !lyricCache.containsKey(getCurrentSong().getTitle()))
                        downloadLyric(lyricUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        VolleyConnection.getInstance(this).addRequestToQueue(request);
    }

    private void downloadLyric(String url) {
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DiskLruFileCache lyricCache = CacheManager.getInstance().getLyricCache();
                if (lyricCache != null) {
                    lyricCache.put(getCurrentSong().getTitle(), response);
                    notifyClients(META_CHANGE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed = null;
                try {
                    parsed = new String(response.data, "UTF-8");
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };
        VolleyConnection.getInstance(this).addRequestToQueue(request);
    }

    public void stopSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            bufferingPercent = 0;
            setState(MusicState.Stop);
            notifyClients(PLAY_STATE_CHANGE);
        }
    }

    public void pauseSong() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying() && getState() == MusicState.Playing) {
                Common.showLog("isPlaying");
                mediaPlayer.pause();
                setState(MusicState.Pause);
                notifyClients(PLAY_STATE_CHANGE);
                updateNotification();
            }
        }
    }

    public void playPauseSong() {
        if (mediaPlayer != null) {
            if (getState() == MusicState.Pause) {
                mediaPlayer.start();
                startForeground(NOTIFY_ID, createNotification(getCurrentSong()));
                setState(MusicState.Playing);
                notifyClients(PLAY_STATE_CHANGE);
                updateNotification();
            }
        }
    }


    /**
     * Set the List song to Service
     *
     * @param list
     */
    public void setPlayListSong(ArrayList<Song> list) {
        this.listSongs = new ArrayList<>(list);
    }

    /**
     * Thêm danh sách bài hát vào trình phát
     *
     * @param list
     */
    public void addPlaylistSong(ArrayList<Song> list) {
        int d = 0;
        if (listSongs == null) {
            listSongs = new ArrayList<>(list);
            //playSong();
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            Song song = list.get(i);
            if (!listSongs.contains(song)) {
                listSongs.add(song);
                d++;
            }
        }
        Common.showToast(this, "Đã thêm " + d + " bài hát vào trình nghe nhạc!!!");
    }

    public void addSong(Song song) {
        if (listSongs == null) {
            listSongs = new ArrayList<>();
            listSongs.add(song);
            return;
        }
        if (!listSongs.contains(song)) {
            listSongs.add(song);
            Common.showToast(this, "Đã thêm '" + song.getTitle() + "' vào trình nghe nhạc!!!");
        }
    }

    public ArrayList<Song> getPlaylistSong() {
        return this.listSongs;
    }


    public Song getCurrentSong() {
        if (listSongs == null) return null;
        if (songPosn < 0) songPosn = 0;
        if (songPosn >= listSongs.size()) return null;
        return listSongs.get(songPosn);
    }

    public int getCurrentSongPosition() {
        if (songPosn < 0) return 0;
        if (songPosn >= listSongs.size()) return 0;
        return songPosn;
    }


    private void updateNotification() {
        Notification notify = createNotification(getCurrentSong());
        startForeground(NOTIFY_ID, notify);
    }

    private Notification createNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(android.R.drawable.stat_notify_sync);
        builder.setContentTitle(title);
        builder.setContentText(message);
        Notification notification = builder.build();
        return notification;
    }

    /**
     * Update notification in status bar
     */
    private Notification createNotification(Song song) {
        Intent notificationIntent = new Intent(this, PlaybackActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_noti_music);
        builder.setContentIntent(contentIntent);

        Notification notification = builder.build();
        RemoteViews bigViews = new RemoteViews(getPackageName(), R.layout.notification_view_expanded);
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_view);
        if (Common.isJellyBean()) {
            notification.bigContentView = bigViews;
        }
        //Set sse Button
        if (getState() == MusicState.Playing) {
            views.setImageViewResource(R.id.imgBtnPlayPause, R.drawable.ic_noti_pause);
            bigViews.setImageViewResource(R.id.imgBtnPlayPause, R.drawable.ic_noti_pause);
        } else {
            views.setImageViewResource(R.id.imgBtnPlayPause, R.drawable.ic_noti_play);
            bigViews.setImageViewResource(R.id.imgBtnPlayPause, R.drawable.ic_noti_play);
        }


        //Set textview color
        views.setTextColor(R.id.tvSongTitle, getResources().getColor(R.color.text_black));
        views.setTextColor(R.id.tvArtist, getResources().getColor(R.color.text_black));
        bigViews.setTextColor(R.id.tvSongTitle, getResources().getColor(R.color.text_black));
        bigViews.setTextColor(R.id.tvArtist, getResources().getColor(R.color.text_black));

        //Set info song in notification
        views.setTextViewText(R.id.tvSongTitle, song.getTitle());
        bigViews.setTextViewText(R.id.tvSongTitle, song.getTitle());
        views.setTextViewText(R.id.tvArtist, song.getArtist());
        bigViews.setTextViewText(R.id.tvArtist, song.getArtist());


        //set Image song
        if (getMediaType() == MediaType.Local) {
            DiskLruImageCache albumCache = CacheManager.getInstance().getAlbumCache();
            Bitmap bitmap = null;
            if (albumCache != null && albumCache.containsKey(song.getAlbum())) {
                bitmap = albumCache.getBitmap(song.getAlbum());
            } else {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.adele);
            }
            bigViews.setImageViewBitmap(R.id.imgAlbumArt, bitmap);
            views.setImageViewBitmap(R.id.imgAlbumArt, bitmap);
        } else if (getMediaType() == MediaType.Youtube && getState() != MusicState.Stop) {
//            bigViews.setImageViewResource(R.id.imgAlbumArt, R.drawable.adele);
//            views.setImageViewResource(R.id.imgAlbumArt, R.drawable.adele);
            if (youtubeInfo != null && youtubeInfo.getThumbnailUrl() != null) {
                Picasso.with(this).load(youtubeInfo.getThumbnailUrl()).memoryPolicy(MemoryPolicy.NO_CACHE).error(R.drawable.adele).into(bigViews, R.id.imgAlbumArt, NOTIFY_ID, notification);
                Picasso.with(this).load(youtubeInfo.getThumbnailUrl()).memoryPolicy(MemoryPolicy.NO_CACHE).error(R.drawable.adele).into(views, R.id.imgAlbumArt, NOTIFY_ID, notification);
            }
        }


        //Set click music control
        PendingIntent piPlayPause = PendingIntent.getService(this, 0, new Intent(ACTION_TOGGLE_PLAY_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent piPre = PendingIntent.getService(this, 0, new Intent(ACTION_PREVIOUS), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent piNext = PendingIntent.getService(this, 0, new Intent(ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent piExit = PendingIntent.getService(this, 0, new Intent(ACTION_EXIT), PendingIntent.FLAG_UPDATE_CURRENT);

        bigViews.setOnClickPendingIntent(R.id.imgBtnPlayPause, piPlayPause);
        bigViews.setOnClickPendingIntent(R.id.imgBtnPre, piPre);
        bigViews.setOnClickPendingIntent(R.id.imgBtnNext, piNext);
        bigViews.setOnClickPendingIntent(R.id.imgBtnExit, piExit);

        views.setOnClickPendingIntent(R.id.imgBtnPlayPause, piPlayPause);
        views.setOnClickPendingIntent(R.id.imgBtnNext, piNext);
        views.setOnClickPendingIntent(R.id.imgBtnExit, piExit);


        notification.contentView = views;
        return notification;
    }

    /**
     * release and stop mediaplayer
     */
    private void relaxMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.release();
            state = MusicState.Stop;
            mediaPlayer = null;
        }
    }

    /**
     * set the player position ic_noti_play
     *
     * @param songPosn
     */
    public void setSongPosition(int songPosn) {
        this.songPosn = songPosn;
    }

    /**
     * Get length of song
     *
     * @return
     */
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    /**
     * Get current ic_noti_play of song
     *
     * @return
     */
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }


    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Common.showLog("Service onStartCommand");
        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(ACTION_TOGGLE_PLAY_PAUSE)) {
                    if (getState() == MusicState.Playing)
                        pauseSong();
                    else
                        playPauseSong();
                } else if (action.equals(ACTION_PAUSE)) {
                    pauseSong();
                } else if (action.equals(ACTION_NEXT)) {
                    nextSong();
                } else if (action.equals(ACTION_PREVIOUS)) {
                    previousSong();
                } else if (action.equals(ACTION_EXIT)) {
                    stopSelf();
                } else if (action.equals(ACTION_PLAY)) {
                    ArrayList list = (ArrayList) intent.getSerializableExtra(Common.LIST_SONG);
                    int currPlay = intent.getIntExtra(Common.CURRENT_PLAY, 0);
                    if (list != null) {
                        setPlayListSong(list);
                        setMediaType(MediaType.Local);
                        setSongPosition(currPlay);
                        playSong();
                    }
                } else if (action.equals(ACTION_PARSE_YOUTUBE)) {
                    String youtubeUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
                    String api = "https://noembed.com/embed?url=" + youtubeUrl;
                    requestYoutubeInfo(api);
                }
            }
        }
        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        Common.showLog("Service onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Common.showLog("Service onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        Common.showLog("Service onDestroy");
        stopForeground(true);
        setState(MusicState.Stop);
        notifyClients(EXIT);
        relaxMediaPlayer();
        super.onDestroy();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (getCurrentPosition() == 0) return;
        Common.showLog("Music Service: onCompletion");
        if (mediaType == MediaType.Local) {
            nextSong();
        } else if (mediaType == MediaType.Youtube) {
            if (isRepeat()) {
                mediaPlayer.seekTo(0);
            } else {
                stopSelf();
            }
        }
    }

    private int getPrePosition() {
//        if(isRepeat()){
//            return songPosn;
//        }
//        if(isShuffle()){
//            int newSongPosition = songPosn;
//
//            while (newSongPosition == songPosn)
//                newSongPosition = rand.nextInt(listSongs.size());
//
//            return newSongPosition;
//        }
//        songPosn = songPosn-1;
//        if(songPosn < 0) return 0;
//        return songPosn;
        if (historys.size() <= 0) {
            int newSongPosition = songPosn;
            while (newSongPosition == songPosn)
                newSongPosition = rand.nextInt(listSongs.size());
            return newSongPosition;
        }
        int newSongPosition = historys.get(historys.size() - 1);
        historys.remove(historys.size() - 1);
        return newSongPosition;
    }

    private int getNextPosition() {
        if (songPosn == listSongs.size()-1) return 0;
        if (historys.size() > listSongs.size() - 1)
            historys.remove(0);
        if (songPosn < 0) return 0;
        if (isRepeat()) {
            return songPosn;
        }
        if (isShuffle()) {
            int newSongPosition = songPosn;

            while (newSongPosition == songPosn || historys.contains(newSongPosition))
                newSongPosition = rand.nextInt(listSongs.size());

            return newSongPosition;
        }
        songPosn = songPosn + 1;
        return songPosn;
    }

    private void previousSong() {
        if ((getState() != MusicState.Pause) && (getState() != MusicState.Playing)) return;
        if (getMediaType() == MediaType.Youtube) return;
        stopSong();
        songPosn = getPrePosition();
        playSong();
    }

    public void nextSong() {
        if ((getState() != MusicState.Pause) && (getState() != MusicState.Playing)) return;
        if (getMediaType() == MediaType.Youtube) return;
        stopSong();
        historys.add(songPosn);
        songPosn = getNextPosition();
        playSong();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Common.showLog("Music Service: onError - " + what);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (getState() != MusicState.Preparing) return;
        Common.showLog("Music Service: onPrepared");
        mediaPlayer.start();
        setState(MusicState.Playing);
        notifyClients(PLAY_STATE_CHANGE);
        notifyClients(META_CHANGE);
        updateNotification();
        if (getMediaType() == MediaType.Local)
            SongProvider.getInstance(this).insertRecentPlay(getCurrentSong());
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        setState(MusicState.Playing);
        mediaPlayer.start();
        notifyClients(PLAY_STATE_CHANGE);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            Common.showLog("MEDIA_INFO_BUFFERING_START");
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            Common.showLog("MEDIA_INFO_BUFFERING_END");
        }
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        bufferingPercent = percent;
    }


    /**
     * Notify all system know State of music player
     *
     * @param what
     * @param bundle
     */
    private void notifyClients(String what, Bundle bundle) {
        Intent intent = new Intent(what);
        if (bundle != null)
            intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void notifyClients(String what) {
        notifyClients(what, null);
    }


    /**
     * Set state of music player
     *
     * @param state
     */
    public void setState(MusicState state) {
        this.state = state;
    }

    /**
     * Get state of music player
     *
     * @return
     */
    public MusicState getState() {
        return this.state;
    }

    /**
     * Seek the music ic_noti_play to position
     *
     * @param mSec
     */
    public void seekTo(int mSec) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(mSec);
            setState(MusicState.Buffering);
            notifyClients(PLAY_STATE_CHANGE);
        }
    }

    /**
     * Set media type
     *
     * @param mediaType
     */
    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * get media type
     *
     * @return
     */
    public MediaType getMediaType() {
        return this.mediaType;
    }

    /**
     * Check the player is Shuffle playlist_dialog
     *
     * @return
     */
    public boolean isShuffle() {
        return isShuffle;
    }

    /**
     * Check the player is Shuffle playlist_dialog
     */
    public boolean isRepeat() {
        return isRepeat;
    }

    /**
     * Set player is shuffle music
     *
     * @param shuffle
     */
    public void setShuffle(boolean shuffle, boolean isSave) {
        this.isShuffle = shuffle;
        if (isSave)
            Settings.getInstance().put("is_shuffle", shuffle);
    }

    /**
     * Set player is repeat current song
     *
     * @param repeat
     */
    public void setRepeat(boolean repeat, boolean isSave) {
        this.isRepeat = repeat;
        if (isSave)
            Settings.getInstance().put("is_repeat", repeat);
    }

    /**
     * Set timer for alarm stop music player
     *
     * @param minutes
     */
    public void setTimer(int minutes) {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
            secondsCountdown = 0;
            if (minutes == 0)
                Common.showToast(this, "Đã hủy hẹn giờ tắt nhạc");
        }
        secondsCountdown = minutes * 60;
        if (minutes > 0) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    secondsCountdown -= 1;
                    if (secondsCountdown <= 0) {
                        int what = 69; //number finish countdown
                        handler.sendEmptyMessage(what);
                        timer.cancel();
                        timer.purge();
                        timer = null;
                    }
                }
            }, 0, 1000);
            Common.showToast(this, "Chương trình sẽ tắt sau: " + minutes + " phút");
        }
    }

    android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 69) {
                MusicService.this.stopSelf();
            }
            return false;
        }
    });

    public int getSecondsCountdown() {
        return secondsCountdown;
    }

    private void requestLink(final String youtubeUrl) {
        String url = "http://www.listentoyoutube.com/cc/conversioncloud.php";
        StringRequest requestLink = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.substring(1, response.length() - 1);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.has("error"))
                        Common.showToast(MusicService.this, object.getString("error"));
                    String statusUrl = object.getString("statusurl");
                    statusUrl = statusUrl + "&json";
                    if (!TextUtils.isEmpty(statusUrl)) {
                        startForeground(NOTIFY_ID, createNotification("Fetch Url: ", youtubeUrl));
                        requestGetProcess(statusUrl);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Common.showToast(MusicService.this, "Đã xảy ra lỗi, vui lòng thử lại");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Common.showToast(MusicService.this, error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("mediaurl", youtubeUrl);
                return param;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        requestLink.setRetryPolicy(policy);
        Volley.newRequestQueue(this).add(requestLink);
    }

    private void requestGetProcess(final String statusUrl) {
        StringRequest request = new StringRequest(statusUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.substring(1, response.length() - 1);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.has("error"))
                        Common.showToast(MusicService.this, object.getString("error"));
                    JSONObject objStatus = object.getJSONObject("status");
                    JSONObject objAttr = objStatus.getJSONObject("@attributes");
                    String step = objAttr.getString("step");
                    if (step.equals("finished")) {
                        stopSong();
                        String downloadurl = object.getString("downloadurl");
                        startForeground(NOTIFY_ID, createNotification("Music Player", "Buffering..."));
                        Song song = new Song();
                        song.setTitle(youtubeInfo.getTitle());
                        song.setAlbum("youtube.com");
                        song.setArtist(youtubeInfo.getAuthor());
                        song.setId(object.getString("id"));
                        song.setLocalDataSource(downloadurl);
                        setMediaType(MediaType.Youtube);
                        stopSong();
                        listSongs = new ArrayList<>();
                        listSongs.add(song);
                        setSongPosition(0);
                        playSong();
                    } else {
                        int percent = objAttr.getInt("percent");
                        String timeInfo = objAttr.getString("info");
                        String message = timeInfo + " | " + youtubeInfo.getTitle();
                        startForeground(NOTIFY_ID, createNotification(step.toUpperCase() + ":... (" + percent + "%)", message));
                        requestGetProcess(statusUrl);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Common.showToast(MusicService.this, "Đã xảy ra lỗi, vui lòng thử lại");
                    stopSelf();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Common.showToast(MusicService.this, error.toString());
                stopSelf();
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        Volley.newRequestQueue(this).add(request);
    }

    private void requestYoutubeInfo(String url) {
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    startForeground(NOTIFY_ID, createNotification("Kết nối máy chủ youtube: ", "..."));
                    youtubeInfo = new YoutubeInfo();
                    youtubeInfo.setTitle(response.getString("title"));
                    youtubeInfo.setAuthor(response.getString("author_name"));
                    youtubeInfo.setAuthorUrl(response.getString("author_url"));
                    youtubeInfo.setThumbnailUrl(response.getString("thumbnail_url"));
                    youtubeInfo.setThumbnailWidth(response.getInt("thumbnail_width"));
                    youtubeInfo.setThumbnailHeight(response.getInt("thumbnail_height"));
                    youtubeInfo.setUrl(response.getString("url"));
                    requestLink(youtubeInfo.getUrl());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Common.showToast(MusicService.this, "Đã xảy ra lỗi, vui lòng thử lại");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Common.showToast(MusicService.this, error.toString());
            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        Volley.newRequestQueue(this).add(request);
    }

    private Bitmap getSongPicture(Song song) {
        Bitmap bitmap = null;
        try {
            MP3File mp3File = (MP3File) AudioFileIO.read(new File(song.getLocalDataSource()));
            if (mp3File.hasID3v2Tag()) {
                ID3v24Tag v2Tag = mp3File.getID3v2TagAsv24();
                Artwork artwork = v2Tag.getFirstArtwork();
                if (artwork != null) {
                    byte[] bytes = artwork.getBinaryData();
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                }
            }
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}

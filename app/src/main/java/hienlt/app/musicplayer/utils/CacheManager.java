package hienlt.app.musicplayer.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by hienl_000 on 5/9/2016.
 */
public class CacheManager {
    private static CacheManager ourInstance;
    private DiskLruImageCache albumCache, artistCache;
    private DiskLruFileCache lyricCache;
    public static final String ALBUM_CACHE_FOLDER = "Album";
    public static final String ARTIST_CACHE_FOLDER = "Artist";
    public static final String LYRIC_CACHE_FOLDER = "Lyrics";

    public static CacheManager getInstance() {
        if (ourInstance == null) {
            synchronized (CacheManager.class) {
                if (ourInstance == null) {
                    ourInstance = new CacheManager();
                }
            }
        }
        return ourInstance;
    }

    private CacheManager() {
        albumCache = DiskLruImageCache.newInstance(ALBUM_CACHE_FOLDER);
        artistCache = DiskLruImageCache.newInstance(ALBUM_CACHE_FOLDER);
        lyricCache = DiskLruFileCache.newInstance(LYRIC_CACHE_FOLDER);
    }

    public String getRootCacheDir() {
        String dir;
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (isSDPresent) {
            dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CornTek";
        } else {
            dir = Environment.getDataDirectory().getAbsolutePath() + File.separator + "CornTek";
        }
        return dir;
    }

    public DiskLruImageCache getAlbumCache(){
        return albumCache;
    }

    public DiskLruImageCache getArtistCache(){
        return artistCache;
    }

    public DiskLruFileCache getLyricCache(){
        return lyricCache;
    }
}

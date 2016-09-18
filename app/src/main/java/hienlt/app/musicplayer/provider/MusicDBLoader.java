package hienlt.app.musicplayer.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hienl_000 on 4/7/2016.
 */
public class MusicDBLoader extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "musicdb";


    public static final String SONG_TABLE_NAME = "media_local";
    public static final String SONG_COLUMN_ID = "_id";
    public static final String SONG_COLUMN_LOCAL_DATA_SOURCE = "local_data_source";
    public static final String SONG_COLUMN_FOLDER = "parent_folder";
    public static final String SONG_COLUMN_TITLE = "title";
    public static final String SONG_COLUMN_TITLE_KEY = "title_key";
    public static final String SONG_COLUMN_ARTIST = "artist";
    public static final String SONG_COLUMN_ARTIST_KEY = "artist_key";
    public static final String SONG_COLUMN_ALBUM = "album";
    public static final String SONG_COLUMN_ALBUM_KEY = "album_key";
    public static final String SONG_COLUMN_GENRE = "genre";
    public static final String SONG_COLUMN_GENRE_KEY = "genre_key";
    public static final String SONG_COLUMN_DURATION = "duration";
    public static final String SONG_COLUMN_YEAR = "year";
    public static final String SONG_COLUMN_BITRATE = "bit_rate";
    public static final String SONG_COLUMN_ADDED_TIME = "added_time";
    public static final String SONG_COLUMN_FILE_SIZE = "file_size";

    public static final String LIKE_TABLE_NAME = "song_like";
    public static final String LIKE_COLUMN_ID = "like_id";
    public static final String LIKE_COLUMN_SONG_ID = "song_id";
    public static final String LIKE_COLUMN_TIME = "time_like";

    public static final String PLAYLIST_TABLE = "playlists";
    public static final String PLAYLIST_COLUMN_ID = "_id";
    public static final String PLAYLIST_COLUMN_TITLE = "title";

    public static final String PLAYLIST_DETAIL_TABLE = "playlist_detail";
    public static final String DETAIL_COLUMN_PLAYLIST_ID = "playlist_id";
    public static final String DETAIL_COLUMN_SONG_ID = "song_id";

    public static final String RECENT_TABLE_NAME = "recent_play";
    public static final String RECENT_COLUMN_ID = "recent_id";
    public static final String RECENT_COLUMN_SONG_ID = "song_id";
    public static final String RECENT_COLUMN_TIME = "time_play";

    public static final String SONG_NUMS = "sum_nums";
    public static final String IS_LIKE = "is_like";

    private Context context;
    private static MusicDBLoader ourInstance;

    private MusicDBLoader(Context context) {
        super(context, DATABASE_NAME, null, 5);
        this.context = context;
    }

    /**
     * Get instance of Song
     * @param context
     * @return
     */
    public static synchronized MusicDBLoader getInstance(Context context){
        if(ourInstance == null)
            ourInstance = new MusicDBLoader (context);
        return ourInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createRecentPlay(db);
        createPlaylistDetailTable(db);
        createSongTable(db);
        createLikeTable(db);
        createPlaylistTable(db);
    }

    private void createRecentPlay(SQLiteDatabase db) {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS "+RECENT_TABLE_NAME+" (");
        builder.append(RECENT_COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        builder.append(RECENT_COLUMN_SONG_ID+" STRING REFERENCES "+SONG_TABLE_NAME+" ("+SONG_COLUMN_ID+") ON DELETE CASCADE ON UPDATE CASCADE UNIQUE, ");
        builder.append(RECENT_COLUMN_TIME+" INTEGER");
        builder.append(")");
        db.execSQL(builder.toString());
    }

    private void createPlaylistTable(SQLiteDatabase db) {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS "+PLAYLIST_TABLE+" (");
        builder.append(PLAYLIST_COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,");
        builder.append(PLAYLIST_COLUMN_TITLE+" VARCHAR");
        builder.append(")");
        db.execSQL(builder.toString());
    }

    private void createPlaylistDetailTable(SQLiteDatabase db) {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS "+PLAYLIST_DETAIL_TABLE+" (");
        builder.append(DETAIL_COLUMN_PLAYLIST_ID+" INTEGER REFERENCES "+PLAYLIST_TABLE+" ("+PLAYLIST_COLUMN_ID+") ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,");
        builder.append(DETAIL_COLUMN_SONG_ID+" VARCHAR REFERENCES "+SONG_TABLE_NAME+" ("+SONG_COLUMN_ID+") ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,");
        builder.append("PRIMARY KEY ("+DETAIL_COLUMN_PLAYLIST_ID+", "+DETAIL_COLUMN_SONG_ID+")");
        builder.append(")");
        db.execSQL(builder.toString());
    }

    private void createLikeTable(SQLiteDatabase db) {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS "+LIKE_TABLE_NAME+" (");
        builder.append(LIKE_COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        builder.append(LIKE_COLUMN_SONG_ID+" STRING REFERENCES "+SONG_TABLE_NAME+" ("+SONG_COLUMN_ID+") ON DELETE CASCADE ON UPDATE CASCADE, ");
        builder.append(LIKE_COLUMN_TIME+" INTEGER");
        builder.append(")");
        db.execSQL(builder.toString());
    }

    private void createSongTable(SQLiteDatabase db) {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS "+SONG_TABLE_NAME+" (");
        builder.append(SONG_COLUMN_ID+" VARCHAR(12) PRIMARY KEY, ");
        builder.append(SONG_COLUMN_LOCAL_DATA_SOURCE+" VARCHAR (255), ");
        builder.append(SONG_COLUMN_FOLDER+" VARCHAR (255), ");
        builder.append(SONG_COLUMN_TITLE+" VARCHAR (100), ");
        builder.append(SONG_COLUMN_TITLE_KEY+" VARCHAR (200), ");
        builder.append(SONG_COLUMN_ARTIST+" VARCHAR (100), ");
        builder.append(SONG_COLUMN_ARTIST_KEY+" VARCHAR (100), ");
        builder.append(SONG_COLUMN_ALBUM+" VARCHAR (100), ");
        builder.append(SONG_COLUMN_ALBUM_KEY+" VARCHAR (100), ");
        builder.append(SONG_COLUMN_GENRE+" VARCHAR (100), ");
        builder.append(SONG_COLUMN_GENRE_KEY+" VARCHAR (100), ");
        builder.append(SONG_COLUMN_DURATION+" INTEGER, ");
        builder.append(SONG_COLUMN_YEAR+" INTEGER, ");
        builder.append(SONG_COLUMN_BITRATE+" INTEGER, ");
        builder.append(SONG_COLUMN_ADDED_TIME+" INTEGER, ");
        builder.append(SONG_COLUMN_FILE_SIZE+" INTEGER");
        builder.append(")");
        db.execSQL(builder.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SONG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RECENT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PLAYLIST_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PLAYLIST_DETAIL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LIKE_TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }
}

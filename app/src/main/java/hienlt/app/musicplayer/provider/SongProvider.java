package hienlt.app.musicplayer.provider;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;

import hienlt.app.musicplayer.models.Album;
import hienlt.app.musicplayer.models.Artist;
import hienlt.app.musicplayer.models.Folder;
import hienlt.app.musicplayer.models.Playlist;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.Settings;
import hienlt.app.musicplayer.utils.StringUtils;
import hienlt.app.musicplayer.utils.TimeUtils;

/**
 * Created by hienl_000 on 4/7/2016.
 */
public class SongProvider {
    private static SongProvider ourInstance;
    private Context context;
    private SQLiteDatabase db;
    private static boolean isPermissionRead;

    private SongProvider(Context context) {
        this.context = context;
    }

    /**
     * Get instance of Song
     *
     * @param context
     * @return
     */
    public static synchronized SongProvider getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new SongProvider(context);
        return ourInstance;
    }

    /**
     * Mở database
     *
     * @return
     */
    private SQLiteDatabase getDatabase() {
        return getDatabase(true);
    }

    private SQLiteDatabase getDatabase(boolean isWrite) {
        if (isWrite)
            return MusicDBLoader.getInstance(context).getWritableDatabase();
        else
            return MusicDBLoader.getInstance(context).getReadableDatabase();
    }

    /**
     * Lấy danh sách tất cả các bài hát
     *
     * @return
     */
    public ArrayList<Song> getListSong() {
        ArrayList<Song> list = new ArrayList<>();
        db = getDatabase(false);
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT " + MusicDBLoader.SONG_TABLE_NAME + ".*, ");
        builder.append(" (CASE WHEN " + MusicDBLoader.LIKE_COLUMN_ID + " IS NULL THEN 0 else 1 END) as is_like");
        builder.append(" FROM " + MusicDBLoader.SONG_TABLE_NAME + " LEFT JOIN " + MusicDBLoader.LIKE_TABLE_NAME + " ON " + MusicDBLoader.SONG_COLUMN_ID + " = " + MusicDBLoader.LIKE_COLUMN_SONG_ID);
        builder.append(" ORDER BY " + MusicDBLoader.SONG_COLUMN_TITLE + " ASC");
        Cursor cursor = db.rawQuery(builder.toString(), null);
//        Cursor cursor = db.rawQuery("SELECT * FROM " + MusicDBLoader.SONG_TABLE_NAME + " ORDER BY " + MusicDBLoader.SONG_COLUMN_TITLE + " ASC", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Song song = new Song();
                song.setTitle(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_TITLE)));
                song.setId(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ID)));
                song.setArtist(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ARTIST)));
                song.setAlbum(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ALBUM)));
                song.setAddedTime(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ADDED_TIME)));
                song.setGenre(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_GENRE)));
                song.setBitRate(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_BITRATE)));
                song.setDuration(cursor.getLong(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_DURATION)));
                song.setSize(cursor.getLong(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_FILE_SIZE)));
                song.setYear(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_YEAR)));
                song.setLocalDataSource(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_LOCAL_DATA_SOURCE)));
                song.setParentFolder(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_FOLDER)));
                int like_column = cursor.getInt(cursor.getColumnIndex("is_like"));
                boolean isLike = (like_column == 1) ? true : false;
                song.setLike(isLike);
                list.add(song);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    public ArrayList<Song> getListSongByKey(String key) {
        key = StringUtils.removeAccent(key);
        ArrayList<Song> list = new ArrayList<>();
        db = getDatabase();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT " + MusicDBLoader.SONG_TABLE_NAME + ".*, ");
        builder.append(" (CASE WHEN " + MusicDBLoader.LIKE_COLUMN_ID + " IS NULL THEN 0 else 1 END) as is_like");
        builder.append(" FROM " + MusicDBLoader.SONG_TABLE_NAME + " LEFT JOIN " + MusicDBLoader.LIKE_TABLE_NAME + " ON " + MusicDBLoader.SONG_COLUMN_ID + " = " + MusicDBLoader.LIKE_COLUMN_SONG_ID);
        builder.append(" WHERE " + MusicDBLoader.SONG_COLUMN_TITLE_KEY + " LIKE '" + key + "%'");
        builder.append(" OR " + MusicDBLoader.SONG_COLUMN_ALBUM_KEY + " LIKE '%" + key + "%'");
        builder.append(" OR " + MusicDBLoader.SONG_COLUMN_ARTIST_KEY + " LIKE '%" + key + "%'");
        builder.append(" OR " + MusicDBLoader.SONG_COLUMN_GENRE_KEY + " LIKE '%" + key + "%'");
        builder.append(" ORDER BY " + MusicDBLoader.SONG_COLUMN_TITLE + " ASC");
        Cursor cursor = db.rawQuery(builder.toString(), null);
//        Cursor cursor = db.rawQuery("SELECT * FROM " + MusicDBLoader.SONG_TABLE_NAME + " ORDER BY " + MusicDBLoader.SONG_COLUMN_TITLE + " ASC", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Song song = new Song();
                song.setTitle(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_TITLE)));
                song.setId(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ID)));
                song.setArtist(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ARTIST)));
                song.setAlbum(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ALBUM)));
                song.setAddedTime(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ADDED_TIME)));
                song.setGenre(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_GENRE)));
                song.setBitRate(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_BITRATE)));
                song.setDuration(cursor.getLong(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_DURATION)));
                song.setSize(cursor.getLong(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_FILE_SIZE)));
                song.setYear(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_YEAR)));
                song.setLocalDataSource(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_LOCAL_DATA_SOURCE)));
                song.setParentFolder(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_FOLDER)));
                int like_column = cursor.getInt(cursor.getColumnIndex("is_like"));
                boolean isLike = (like_column == 1) ? true : false;
                song.setLike(isLike);
                list.add(song);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * Lấy danh sách bài hát theo tên album
     *
     * @param albumName
     * @return
     */
    public ArrayList<Song> getListSongByAlbum(String albumName) {
        ArrayList<Song> list = new ArrayList<>();
        if (!getPermissionRead()) return list;
        db = getDatabase();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT " + MusicDBLoader.SONG_TABLE_NAME + ".*, ");
        builder.append(" (CASE WHEN " + MusicDBLoader.LIKE_COLUMN_ID + " IS NULL THEN 0 else 1 END) as is_like");
        builder.append(" FROM " + MusicDBLoader.SONG_TABLE_NAME + " LEFT JOIN " + MusicDBLoader.LIKE_TABLE_NAME + " ON " + MusicDBLoader.SONG_COLUMN_ID + " = " + MusicDBLoader.LIKE_COLUMN_SONG_ID);
        builder.append(" WHERE " + MusicDBLoader.SONG_COLUMN_ALBUM + " = '" + albumName + "'");
        Cursor cursor = db.rawQuery(builder.toString(), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Song song = new Song();
                song.setTitle(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_TITLE)));
                song.setId(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ID)));
                song.setArtist(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ARTIST)));
                song.setAlbum(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ALBUM)));
                song.setAddedTime(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ADDED_TIME)));
                song.setGenre(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_GENRE)));
                song.setBitRate(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_BITRATE)));
                song.setDuration(cursor.getLong(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_DURATION)));
                song.setSize(cursor.getLong(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_FILE_SIZE)));
                song.setYear(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_YEAR)));
                song.setLocalDataSource(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_LOCAL_DATA_SOURCE)));
                song.setParentFolder(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_FOLDER)));
                int like_column = cursor.getInt(cursor.getColumnIndex("is_like"));
                boolean isLike = (like_column == 1) ? true : false;
                song.setLike(isLike);
                list.add(song);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * Lấy danh sách bài hát theo ca sĩ
     *
     * @param artistName
     * @return
     */
    public ArrayList<Song> getListSongByArtist(String artistName) {
        ArrayList<Song> list = new ArrayList<>();
        if (!getPermissionRead()) return list;
        db = getDatabase();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT " + MusicDBLoader.SONG_TABLE_NAME + ".*, ");
        builder.append(" (CASE WHEN " + MusicDBLoader.LIKE_COLUMN_ID + " IS NULL THEN 0 else 1 END) as is_like");
        builder.append(" FROM " + MusicDBLoader.SONG_TABLE_NAME + " LEFT JOIN " + MusicDBLoader.LIKE_TABLE_NAME + " ON " + MusicDBLoader.SONG_COLUMN_ID + " = " + MusicDBLoader.LIKE_COLUMN_SONG_ID);
        builder.append(" WHERE " + MusicDBLoader.SONG_COLUMN_ARTIST + " = '" + artistName + "'");
        Cursor cursor = db.rawQuery(builder.toString(), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Song song = new Song();
                song.setTitle(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_TITLE)));
                song.setId(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ID)));
                song.setArtist(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ARTIST)));
                song.setAlbum(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ALBUM)));
                song.setAddedTime(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ADDED_TIME)));
                song.setGenre(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_GENRE)));
                song.setBitRate(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_BITRATE)));
                song.setDuration(cursor.getLong(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_DURATION)));
                song.setSize(cursor.getLong(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_FILE_SIZE)));
                song.setYear(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_YEAR)));
                song.setLocalDataSource(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_LOCAL_DATA_SOURCE)));
                song.setParentFolder(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_FOLDER)));
                int like_column = cursor.getInt(cursor.getColumnIndex("is_like"));
                boolean isLike = (like_column == 1) ? true : false;
                song.setLike(isLike);
                list.add(song);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    public ArrayList<Song> getListSongByFolder(String folderPath) {
        ArrayList<Song> list = new ArrayList<>();
        if (!getPermissionRead()) return list;
        db = getDatabase();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT " + MusicDBLoader.SONG_TABLE_NAME + ".*, ");
        builder.append(" (CASE WHEN " + MusicDBLoader.LIKE_COLUMN_ID + " IS NULL THEN 0 else 1 END) as is_like");
        builder.append(" FROM " + MusicDBLoader.SONG_TABLE_NAME + " LEFT JOIN " + MusicDBLoader.LIKE_TABLE_NAME + " ON " + MusicDBLoader.SONG_COLUMN_ID + " = " + MusicDBLoader.LIKE_COLUMN_SONG_ID);
        builder.append(" WHERE " + MusicDBLoader.SONG_COLUMN_FOLDER + " = '" + folderPath + "'");
        Cursor cursor = db.rawQuery(builder.toString(), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Song song = new Song();
                song.setTitle(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_TITLE)));
                song.setId(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ID)));
                song.setArtist(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ARTIST)));
                song.setAlbum(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ALBUM)));
                song.setAddedTime(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ADDED_TIME)));
                song.setGenre(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_GENRE)));
                song.setBitRate(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_BITRATE)));
                song.setDuration(cursor.getLong(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_DURATION)));
                song.setSize(cursor.getLong(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_FILE_SIZE)));
                song.setYear(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_YEAR)));
                song.setLocalDataSource(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_LOCAL_DATA_SOURCE)));
                song.setParentFolder(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_FOLDER)));
                int like_column = cursor.getInt(cursor.getColumnIndex("is_like"));
                boolean isLike = (like_column == 1) ? true : false;
                song.setLike(isLike);
                list.add(song);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * Lấy danh sách bài hát theo playlist id
     *
     * @param playlistId
     * @return
     */
    public ArrayList<Song> getListSongByPlaylist(int playlistId) {
        ArrayList<Song> list = new ArrayList<>();
        if (!getPermissionRead()) return list;
        db = getDatabase();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT " + MusicDBLoader.SONG_TABLE_NAME + ".*, ");
        builder.append(" (CASE WHEN " + MusicDBLoader.LIKE_COLUMN_ID + " IS NULL THEN 0 else 1 END) as " + MusicDBLoader.IS_LIKE);
        builder.append(" FROM " + MusicDBLoader.SONG_TABLE_NAME + " LEFT JOIN " + MusicDBLoader.LIKE_TABLE_NAME + " ON " + MusicDBLoader.SONG_TABLE_NAME + "." + MusicDBLoader.SONG_COLUMN_ID + " = " + MusicDBLoader.LIKE_TABLE_NAME + "." + MusicDBLoader.LIKE_COLUMN_SONG_ID + ", " + MusicDBLoader.PLAYLIST_DETAIL_TABLE);
        builder.append(" WHERE " + MusicDBLoader.SONG_TABLE_NAME + "." + MusicDBLoader.SONG_COLUMN_ID + " = " + MusicDBLoader.PLAYLIST_DETAIL_TABLE + ".").append(MusicDBLoader.DETAIL_COLUMN_SONG_ID);
        builder.append(" AND " + MusicDBLoader.PLAYLIST_DETAIL_TABLE + "." + MusicDBLoader.DETAIL_COLUMN_PLAYLIST_ID + " = " + playlistId);
        Cursor cursor = db.rawQuery(builder.toString(), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Song song = new Song();
                song.setTitle(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_TITLE)));
                song.setId(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ID)));
                song.setArtist(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ARTIST)));
                song.setAlbum(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ALBUM)));
                song.setAddedTime(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ADDED_TIME)));
                song.setGenre(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_GENRE)));
                song.setBitRate(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_BITRATE)));
                song.setDuration(cursor.getLong(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_DURATION)));
                song.setSize(cursor.getLong(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_FILE_SIZE)));
                song.setYear(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_YEAR)));
                song.setLocalDataSource(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_LOCAL_DATA_SOURCE)));
                song.setParentFolder(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_FOLDER)));
                int like_column = cursor.getInt(cursor.getColumnIndex("is_like"));
                boolean isLike = (like_column == 1) ? true : false;
                song.setLike(isLike);
                list.add(song);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * Update lại danh sách bài hát
     *
     * @param songs
     * @return
     */
    public boolean updateListSong(ArrayList<Song> songs) {
        if (!getPermissionRead()) return false;
        ArrayList<Song> listSong = getListSong();
        ArrayList<Song> listAdd = new ArrayList<>();
        ArrayList<Song> listDelete = new ArrayList<>();
        for (Song song : listSong) {
            if (!songs.contains(song)) {
                listDelete.add(song);
            }
        }
        for (Song song : songs) {
            if (!listSong.contains(song)) {
                listAdd.add(song);
            }
        }
        db = getDatabase();
        for (Song song : listDelete) {
            db.delete(MusicDBLoader.SONG_TABLE_NAME, MusicDBLoader.SONG_COLUMN_ID + "= '" + song.getId() + "'", null);
        }
        ContentValues value = new ContentValues();
        for (Song song : listAdd) {
            value.clear();
            value.put(MusicDBLoader.SONG_COLUMN_ID, song.getId());
            value.put(MusicDBLoader.SONG_COLUMN_TITLE, song.getTitle());
            value.put(MusicDBLoader.SONG_COLUMN_ALBUM, song.getAlbum());
            value.put(MusicDBLoader.SONG_COLUMN_ARTIST, song.getArtist());
            value.put(MusicDBLoader.SONG_COLUMN_GENRE, song.getGenre());
            value.put(MusicDBLoader.SONG_COLUMN_ADDED_TIME, (System.currentTimeMillis() / 1000));
            value.put(MusicDBLoader.SONG_COLUMN_BITRATE, song.getBitRate());
            value.put(MusicDBLoader.SONG_COLUMN_DURATION, song.getDuration());
            value.put(MusicDBLoader.SONG_COLUMN_FILE_SIZE, song.getSize());
            value.put(MusicDBLoader.SONG_COLUMN_FOLDER, song.getParentFolder());
            value.put(MusicDBLoader.SONG_COLUMN_LOCAL_DATA_SOURCE, song.getLocalDataSource());
            value.put(MusicDBLoader.SONG_COLUMN_YEAR, song.getYear());
            value.put(MusicDBLoader.SONG_COLUMN_TITLE_KEY, song.getTitleKey());
            value.put(MusicDBLoader.SONG_COLUMN_ALBUM_KEY, song.getAlbumKey());
            value.put(MusicDBLoader.SONG_COLUMN_GENRE_KEY, song.getGenreKey());
            value.put(MusicDBLoader.SONG_COLUMN_ARTIST_KEY, song.getArtistKey());
            db.insert(MusicDBLoader.SONG_TABLE_NAME, null, value);
        }
        db.close();
        return true;
    }

    /**
     * Get số lượng bài hát
     *
     * @return
     */
    public int getSongNum() {
        int count = 0;
        if (!getPermissionRead()) return 0;
        db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + MusicDBLoader.SONG_TABLE_NAME, null);
        if (cursor.moveToFirst())
            count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    /**
     * Set like cho 1 bài nhạc
     *
     * @param songID
     * @return
     */
    public boolean insertLike(String songID) {
        if (!getPermissionRead()) return false;
        db = getDatabase();

        Cursor c = db.query(MusicDBLoader.LIKE_TABLE_NAME, new String[]{MusicDBLoader.LIKE_COLUMN_SONG_ID}, MusicDBLoader.LIKE_COLUMN_SONG_ID + " = '" + songID + "'", null, null, null, null);
        if (c.moveToFirst()) {
            //Update time of like
            ContentValues value = new ContentValues();
            value.put(MusicDBLoader.LIKE_COLUMN_TIME, TimeUtils.getTimeStamp());
            int numUpdate = db.update(MusicDBLoader.LIKE_TABLE_NAME, value, MusicDBLoader.LIKE_COLUMN_SONG_ID + " = '" + songID + "'", null);
            c.close();
            db.close();
            if (numUpdate > 0) return true;
        } else {
            //Insert new like
            ContentValues value = new ContentValues();
            value.put(MusicDBLoader.LIKE_COLUMN_SONG_ID, songID);
            value.put(MusicDBLoader.LIKE_COLUMN_TIME, TimeUtils.getTimeStamp());
            long id = db.insert(MusicDBLoader.LIKE_TABLE_NAME, null, value);
            c.close();
            db.close();
            if (id != -1) return true;
        }
        return false;
    }

    /**
     * Unlike 1 bài nhạc
     *
     * @param songID
     * @return
     */
    public boolean removeLike(String songID) {
        if (!getPermissionRead()) return false;
        db = getDatabase();
        int num = db.delete(MusicDBLoader.LIKE_TABLE_NAME, MusicDBLoader.LIKE_COLUMN_SONG_ID + " = '" + songID + "'", null);
        db.close();
        if (num > 0) return true;
        return false;
    }

    /**
     * Lấy tất cả các album
     *
     * @return
     */
    public ArrayList<Album> getAlbums() {
        ArrayList<Album> albums = new ArrayList<>();
        db = getDatabase();

        Cursor cursor = db.rawQuery("SELECT album, artist, COUNT(*) as song_nums from media_local GROUP BY album ORDER BY album ASC", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Album album = new Album();
                album.setName(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ALBUM)));
                album.setArtist(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ARTIST)));
                album.setSongNum(cursor.getInt(cursor.getColumnIndex("song_nums")));
                albums.add(album);
                cursor.moveToNext();
            }
        }
        db.close();
        return albums;
    }

    /**
     * Lấy danh danh sách các album theo tên album
     *
     * @param albumName
     * @return
     */
    public Album getAlbumByName(String albumName) {
        db = getDatabase();

        Cursor cursor = db.rawQuery("SELECT album, artist, COUNT(*) as " + MusicDBLoader.SONG_NUMS + " from media_local WHERE album = ? GROUP BY album ORDER BY " + MusicDBLoader.SONG_COLUMN_TITLE + " ASC", new String[]{albumName});
        if (cursor.moveToFirst()) {
            Album album = new Album();
            album.setName(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ALBUM)));
            album.setArtist(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ARTIST)));
            album.setSongNum(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_NUMS)));
            return album;
        }
        cursor.close();
        db.close();
        return null;
    }

    /**
     * Lấy danh sách bài hát yêu thích
     *
     * @return
     */
    public ArrayList<Song> getListSongLike() {
        ArrayList<Song> list = new ArrayList<>();
        if (!getPermissionRead()) return list;
        db = getDatabase(false);
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT " + MusicDBLoader.SONG_TABLE_NAME + ".*," + MusicDBLoader.LIKE_TABLE_NAME + "." + MusicDBLoader.LIKE_COLUMN_ID + "," + MusicDBLoader.LIKE_TABLE_NAME + "." + MusicDBLoader.LIKE_COLUMN_TIME);
        builder.append(" FROM " + MusicDBLoader.SONG_TABLE_NAME + ", " + MusicDBLoader.LIKE_TABLE_NAME);
        builder.append(" WHERE " + MusicDBLoader.SONG_TABLE_NAME + "." + MusicDBLoader.SONG_COLUMN_ID + " = " + MusicDBLoader.LIKE_TABLE_NAME + "." + MusicDBLoader.LIKE_COLUMN_SONG_ID);
        Cursor cursor = db.rawQuery(builder.toString(), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Song song = new Song();
                song.setTitle(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_TITLE)));
                song.setId(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ID)));
                song.setArtist(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ARTIST)));
                song.setAlbum(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ALBUM)));
                song.setAddedTime(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ADDED_TIME)));
                song.setGenre(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_GENRE)));
                song.setBitRate(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_BITRATE)));
                song.setDuration(cursor.getLong(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_DURATION)));
                song.setSize(cursor.getLong(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_FILE_SIZE)));
                song.setYear(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_YEAR)));
                song.setLocalDataSource(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_LOCAL_DATA_SOURCE)));
                song.setParentFolder(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_FOLDER)));
                song.setLike(true);
                list.add(song);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    public ArrayList<Song> getListSongRecentPlay() {
        ArrayList<Song> list = new ArrayList<>();
        if (!getPermissionRead()) return list;
        db = getDatabase();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT " + MusicDBLoader.SONG_TABLE_NAME + ".*, ");
        builder.append(" (CASE WHEN " + MusicDBLoader.LIKE_COLUMN_ID + " IS NULL THEN 0 else 1 END) as " + MusicDBLoader.IS_LIKE + ", " + MusicDBLoader.RECENT_TABLE_NAME + "." + MusicDBLoader.RECENT_COLUMN_TIME);
        builder.append(" FROM " + MusicDBLoader.SONG_TABLE_NAME + " LEFT JOIN " + MusicDBLoader.LIKE_TABLE_NAME + " ON " + MusicDBLoader.SONG_TABLE_NAME + "." + MusicDBLoader.SONG_COLUMN_ID + " = " + MusicDBLoader.LIKE_TABLE_NAME + "." + MusicDBLoader.LIKE_COLUMN_SONG_ID + ", " + MusicDBLoader.RECENT_TABLE_NAME);
        builder.append(" WHERE " + MusicDBLoader.SONG_TABLE_NAME + "." + MusicDBLoader.SONG_COLUMN_ID + " = " + MusicDBLoader.RECENT_TABLE_NAME + ".").append(MusicDBLoader.RECENT_COLUMN_SONG_ID);
        builder.append(" ORDER BY " + MusicDBLoader.RECENT_COLUMN_TIME + " DESC");
        Cursor cursor = db.rawQuery(builder.toString(), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Song song = new Song();
                song.setTitle(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_TITLE)));
                song.setId(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ID)));
                song.setArtist(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ARTIST)));
                song.setAlbum(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ALBUM)));
                song.setAddedTime(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ADDED_TIME)));
                song.setGenre(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_GENRE)));
                song.setBitRate(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_BITRATE)));
                song.setDuration(cursor.getLong(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_DURATION)));
                song.setSize(cursor.getLong(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_FILE_SIZE)));
                song.setYear(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_YEAR)));
                song.setLocalDataSource(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_LOCAL_DATA_SOURCE)));
                song.setParentFolder(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_FOLDER)));
                int like_column = cursor.getInt(cursor.getColumnIndex("is_like"));
                boolean isLike = (like_column == 1) ? true : false;
                song.setLike(isLike);
                list.add(song);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * Lấy danh sách ca sĩ
     *
     * @return
     */
    public ArrayList<Artist> getListArtists() {
        ArrayList<Artist> list = new ArrayList<>();
        db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT " + MusicDBLoader.SONG_COLUMN_ARTIST + ",COUNT(*) as " + MusicDBLoader.SONG_NUMS + " FROM " + MusicDBLoader.SONG_TABLE_NAME + " GROUP BY " + MusicDBLoader.SONG_COLUMN_ARTIST + " ORDER BY " + MusicDBLoader.SONG_COLUMN_ARTIST + " ASC", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String strArtist = cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_ARTIST));
                int song_nums = cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_NUMS));
                Artist artist = new Artist(strArtist, song_nums);
                list.add(artist);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    public ArrayList<Playlist> getAllPlaylist() {
        ArrayList<Playlist> playlists = new ArrayList<>();
        if (!getPermissionRead()) return playlists;
        db = getDatabase();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT " + MusicDBLoader.PLAYLIST_COLUMN_ID + ", " + MusicDBLoader.PLAYLIST_COLUMN_TITLE + ", COUNT(" + MusicDBLoader.PLAYLIST_DETAIL_TABLE + "." + MusicDBLoader.DETAIL_COLUMN_SONG_ID + ") as " + MusicDBLoader.SONG_NUMS);
        builder.append(" FROM " + MusicDBLoader.PLAYLIST_TABLE + " LEFT JOIN ").append(MusicDBLoader.PLAYLIST_DETAIL_TABLE);
        builder.append(" ON " + MusicDBLoader.PLAYLIST_TABLE + "." + MusicDBLoader.PLAYLIST_COLUMN_ID + " = " + MusicDBLoader.PLAYLIST_DETAIL_TABLE + ".").append(MusicDBLoader.DETAIL_COLUMN_PLAYLIST_ID);
        builder.append(" GROUP BY " + MusicDBLoader.PLAYLIST_TABLE + "." + MusicDBLoader.PLAYLIST_COLUMN_ID + ", " + MusicDBLoader.PLAYLIST_TABLE + ".").append(MusicDBLoader.PLAYLIST_COLUMN_ID);
        builder.append(" ORDER BY " + MusicDBLoader.PLAYLIST_COLUMN_TITLE + " ASC");
        Cursor cursor = db.rawQuery(builder.toString(), null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Playlist pl = new Playlist();
                pl.setId(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.PLAYLIST_COLUMN_ID)));
                pl.setTitle(cursor.getString(cursor.getColumnIndex(MusicDBLoader.PLAYLIST_COLUMN_TITLE)));
                pl.setSongNum(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_NUMS)));
                playlists.add(pl);
                cursor.moveToNext();
            }
        }
        db.close();
        return playlists;
    }

    public Playlist getPlaylistByID(int plID) {
        Playlist pl = null;
        if (!getPermissionRead()) return pl;
        db = getDatabase();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT " + MusicDBLoader.PLAYLIST_COLUMN_ID + ", " + MusicDBLoader.PLAYLIST_COLUMN_TITLE + ", COUNT(" + MusicDBLoader.PLAYLIST_DETAIL_TABLE + "." + MusicDBLoader.DETAIL_COLUMN_SONG_ID + ") as " + MusicDBLoader.SONG_NUMS);
        builder.append(" FROM " + MusicDBLoader.PLAYLIST_TABLE + " LEFT JOIN ").append(MusicDBLoader.PLAYLIST_DETAIL_TABLE);
        builder.append(" ON " + MusicDBLoader.PLAYLIST_TABLE + "." + MusicDBLoader.PLAYLIST_COLUMN_ID + " = " + MusicDBLoader.PLAYLIST_DETAIL_TABLE + ".").append(MusicDBLoader.DETAIL_COLUMN_PLAYLIST_ID);
        builder.append(" WHERE " + MusicDBLoader.PLAYLIST_TABLE + "." + MusicDBLoader.PLAYLIST_COLUMN_ID + " = " + plID);
        builder.append(" GROUP BY " + MusicDBLoader.PLAYLIST_TABLE + "." + MusicDBLoader.PLAYLIST_COLUMN_ID + ", " + MusicDBLoader.PLAYLIST_TABLE + ".").append(MusicDBLoader.PLAYLIST_COLUMN_ID);
        Cursor cursor = db.rawQuery(builder.toString(), null);
        if (cursor.moveToFirst()) {
            pl = new Playlist();
            pl.setId(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.PLAYLIST_COLUMN_ID)));
            pl.setTitle(cursor.getString(cursor.getColumnIndex(MusicDBLoader.PLAYLIST_COLUMN_TITLE)));
            pl.setSongNum(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_NUMS)));
        }
        db.close();
        return pl;
    }

    /**
     * Thêm playlist
     *
     * @param playlistName
     * @return
     */
    public long insertPlaylist(String playlistName) {
        playlistName = playlistName.trim();
        if (!getPermissionRead()) return -1;
        db = getDatabase();
        ContentValues value = new ContentValues();
        value.put(MusicDBLoader.PLAYLIST_COLUMN_TITLE, playlistName);
        long id = db.insert(MusicDBLoader.PLAYLIST_TABLE, null, value);
        db.close();
        return id;
    }

    /**
     * Xóa playlist
     *
     * @param playlistID
     * @return
     */
    public boolean removePlaylist(int playlistID) {
        if (!getPermissionRead()) return false;
        db = getDatabase();
        int num = db.delete(MusicDBLoader.PLAYLIST_TABLE, MusicDBLoader.PLAYLIST_COLUMN_ID + "='" + playlistID + "'", null);
        db.close();
        return (num > 0);
    }

    /**
     * Xóa tất cả bài hát trong playlist
     *
     * @param playlistID
     * @return số lượng được xóa
     */
    public int removeAllSongFromPlaylist(int playlistID) {
        db = getDatabase();
        int num = db.delete(MusicDBLoader.PLAYLIST_DETAIL_TABLE, MusicDBLoader.DETAIL_COLUMN_PLAYLIST_ID + "=" + playlistID, null);
        db.close();
        return num;
    }

    /**
     * Xóa bài hát khỏi playlist
     *
     * @param songID
     * @return
     */
    public boolean removeSongFromPlaylist(int playlistId, String songID) {
        db = getDatabase();
        int num = db.delete(MusicDBLoader.PLAYLIST_DETAIL_TABLE, MusicDBLoader.DETAIL_COLUMN_PLAYLIST_ID + "=" + playlistId + " AND " + MusicDBLoader.DETAIL_COLUMN_SONG_ID + " = '" + songID + "'", null);
        db.close();
        return (num > 0);
    }

    /**
     * Xóa bài hát
     *
     * @param songID
     * @return
     */
    public boolean removeSong(String songID) {
        if (!getPermissionRead()) return false;
        db = getDatabase();
        int num = db.delete(MusicDBLoader.SONG_TABLE_NAME, MusicDBLoader.SONG_COLUMN_ID + "='" + songID + "'", null);
        db.close();
        Common.showLog(num + "");
        return (num > 0);
    }

    /**
     * Thêm bài hát vào playlist
     *
     * @param playlistID
     * @param songID
     * @return
     */
    public boolean insertSongToPlaylist(int playlistID, String songID) {
        if (isSongExist(playlistID, songID))
            return false;
        db = getDatabase();
        ContentValues value = new ContentValues();
        value.put(MusicDBLoader.DETAIL_COLUMN_PLAYLIST_ID, playlistID);
        value.put(MusicDBLoader.DETAIL_COLUMN_SONG_ID, songID);
        long id = db.insert(MusicDBLoader.PLAYLIST_DETAIL_TABLE, null, value);
        db.close();
        if (id != -1)
            return true;
        return false;
    }

    /**
     * Thêm bài hát vào playlist
     *
     * @param playlistID
     * @param listSong
     */
    public int insertSongToPlayList(int playlistID, ArrayList<Song> listSong) {
        ContentValues value = new ContentValues();
        int num = 0;
        for (Song song : listSong) {
            if (isSongExist(playlistID, song.getId()))
                continue;
            db = getDatabase();
            value.clear();
            value.put(MusicDBLoader.DETAIL_COLUMN_PLAYLIST_ID, playlistID);
            value.put(MusicDBLoader.DETAIL_COLUMN_SONG_ID, song.getId());
            long id = db.insert(MusicDBLoader.PLAYLIST_DETAIL_TABLE, null, value);
            if (id != -1) num++;
        }
        db.close();
        return num;
    }

    public boolean updatePlaylist(int playlistID, String playlistName) {
        if (!getPermissionRead()) return false;
        playlistName = playlistName.trim();
        db = getDatabase();
        ContentValues value = new ContentValues();
        value.put(MusicDBLoader.PLAYLIST_COLUMN_TITLE, playlistName);
        int num = db.update(MusicDBLoader.PLAYLIST_TABLE, value, MusicDBLoader.PLAYLIST_COLUMN_ID + "=" + playlistID, null);
        db.close();
        return (num > 0);
    }

    public ArrayList<Folder> getListFolder() {
        ArrayList<Folder> folders = new ArrayList<>();
        db = getDatabase();
        Cursor cursor = db.query(MusicDBLoader.SONG_TABLE_NAME, new String[]{MusicDBLoader.SONG_COLUMN_FOLDER, "count(*) as " + MusicDBLoader.SONG_NUMS}, null, null, MusicDBLoader.SONG_COLUMN_FOLDER, null, MusicDBLoader.SONG_COLUMN_FOLDER + " asc");
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                File file = new File(cursor.getString(cursor.getColumnIndex(MusicDBLoader.SONG_COLUMN_FOLDER)));
                Folder folder = new Folder();
                folder.setFolderName(file.getName());
                folder.setFolderPath(file.getAbsolutePath());
                folder.setSongNum(cursor.getInt(cursor.getColumnIndex(MusicDBLoader.SONG_NUMS)));
                folders.add(folder);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return folders;
    }

    /**
     * Kiểm tra bài hát có tồn tại trong playlist
     *
     * @param playlistID
     * @param songID
     * @return
     */
    public boolean isSongExist(int playlistID, String songID) {
        boolean isExist = false;
        db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT " + MusicDBLoader.DETAIL_COLUMN_SONG_ID + " FROM " + MusicDBLoader.PLAYLIST_DETAIL_TABLE + " WHERE " + MusicDBLoader.DETAIL_COLUMN_SONG_ID + " = '" + songID + "' AND " + MusicDBLoader.DETAIL_COLUMN_PLAYLIST_ID + " = " + playlistID, null);
        if (cursor.moveToFirst())
            isExist = true;
        db.close();
        return isExist;
    }

    /**
     * Kiểm tra bài hát tồn tại
     *
     * @param songID
     * @return
     */
    public boolean isSongExist(String songID) {
        boolean isExist = false;
        db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT " + MusicDBLoader.SONG_COLUMN_ID + " FROM " + MusicDBLoader.SONG_TABLE_NAME + " WHERE " + MusicDBLoader.SONG_COLUMN_ID + " = '" + songID + "'", null);
        if (cursor.moveToFirst())
            isExist = true;
        db.close();
        return isExist;
    }

    public boolean isFolderExist(String folderPath) {
        boolean isExist = false;
        db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT " + MusicDBLoader.SONG_COLUMN_ID + " FROM " + MusicDBLoader.SONG_TABLE_NAME + " WHERE " + MusicDBLoader.SONG_COLUMN_FOLDER + " = '" + folderPath + "'", null);
        if (cursor.moveToFirst())
            isExist = true;
        db.close();
        return isExist;
    }

    public long insertRecentPlay(Song song) {
        db = getDatabase();
        ContentValues values = new ContentValues();
        values.put(MusicDBLoader.RECENT_COLUMN_SONG_ID, song.getId());
        values.put(MusicDBLoader.RECENT_COLUMN_TIME, TimeUtils.getTimeStamp());
        long id = db.insertWithOnConflict(MusicDBLoader.RECENT_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return id;
    }

    /**
     * Kiểm tra quyền truy xuất thẻ nhớ
     *
     * @return
     */
    public boolean getPermissionRead() {
        if (Common.isMarshMallow())
            return (context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        return true;
    }

    public void open() {
        db = MusicDBLoader.getInstance(context).getWritableDatabase();
    }

    public void close() {
        db.close();
    }

}

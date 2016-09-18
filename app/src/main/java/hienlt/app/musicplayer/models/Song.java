package hienlt.app.musicplayer.models;

import android.graphics.Bitmap;

import java.io.Serializable;

import hienlt.app.musicplayer.utils.FileUtils;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.StringUtils;

/**
 * Created by hienl_000 on 4/4/2016.
 */
public class Song implements Serializable {

    private StringBuilder builder = new StringBuilder();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocalDataSource() {
        return localDataSource;
    }

    public void setLocalDataSource(String localDataSource) {
        this.localDataSource = localDataSource;
    }

    public String getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(String parentFolder) {
        this.parentFolder = parentFolder;
    }

    private String id;
    private String localDataSource;
    private String parentFolder;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getSizeDisplay(){
        return FileUtils.bytesIntoHumanReadable(getSize());
    }

    private long size;
    private String title;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    private String artist;

    public String getAlbum() {
        return album.trim();
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    private String album;

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    private String genre;
    private long duration;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    private int year;

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    private int bitRate;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private String comment;

    public int getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(int addedTime) {
        this.addedTime = addedTime;
    }

    private int addedTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the duration of the song, in miliseconds.
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * Returns the duration of the song, in miliseconds.
     */
    public long getDuration() {
        return duration;
    }

    public String getDurationDisplay(){
        return Common.getConvertedTime(getDuration());
    }

    public long getDurationSeconds() {
        return getDuration() / 1000;
    }

    public long getDurationMinutes() {
        return getDurationSeconds() / 60;
    }

    public Bitmap getAlbumPicture() {
        return albumPicture;
    }

    public void setAlbumPicture(Bitmap albumPicture) {
        this.albumPicture = albumPicture;
    }

    private Bitmap albumPicture;

    private boolean like;

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public String getTitleKey(){
        builder.setLength(0);
        builder.append(StringUtils.removeAccent(getTitle()).toUpperCase());
        builder.append(" ");
        builder.append(StringUtils.removeWhileSpace(getTitle()).toUpperCase());
        builder.append(" ");
        builder.append(StringUtils.firstLetterWord(getTitle()).toUpperCase());
        return builder.toString();
    }

    public String getArtistKey(){
        builder.setLength(0);
        builder.append(StringUtils.removeAccent(getArtist()).toUpperCase());
        builder.append(" ");
        builder.append(StringUtils.removeWhileSpace(getArtist()).toUpperCase());
        builder.append(" ");
        builder.append(StringUtils.firstLetterWord(getArtist()).toUpperCase());
        return builder.toString();
    }

    public String getAlbumKey(){
        builder.setLength(0);
        builder.append(StringUtils.removeAccent(getAlbum()).toUpperCase());
        builder.append(" ");
        builder.append(StringUtils.removeWhileSpace(getAlbum()).toUpperCase());
        builder.append(" ");
        builder.append(StringUtils.firstLetterWord(getAlbum()).toUpperCase());
        return builder.toString();
    }

    public String getGenreKey(){
        builder.setLength(0);
        builder.append(StringUtils.removeAccent(getGenre()).toUpperCase());
        builder.append(" ");
        builder.append(StringUtils.removeWhileSpace(getGenre()).toUpperCase());
        builder.append(" ");
        builder.append(StringUtils.firstLetterWord(getGenre()).toUpperCase());
        return builder.toString();
    }

    @Override
    public boolean  equals (Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            Song song = (Song) object;
            if (this.getId().equals(song.getId())){
                result = true;
            }
        }
        return result;
    }
}

package hienlt.app.musicplayer.models;

/**
 * Created by hienl_000 on 4/28/2016.
 */
public class Artist {
    private String artist;
    private int songNum;

    public Artist(String artist, int songNum) {
        this.songNum = songNum;
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getSongNum() {
        return songNum;
    }

    public void setSongNum(int songNum) {
        this.songNum = songNum;
    }
}

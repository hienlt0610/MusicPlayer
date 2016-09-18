package hienlt.app.musicplayer.models;

import java.util.ArrayList;

/**
 * Created by hienl_000 on 5/3/2016.
 */
public class Playlist {
    private int id;
    private String title;
    private int songNum;

    public Playlist() {
    }

    public Playlist(int id, String title){
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSongNum() {
        return songNum;
    }

    public void setSongNum(int songNum) {
        this.songNum = songNum;
    }

    @Override
    public boolean  equals (Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            Playlist playlist = (Playlist) object;
            if (this.getId() == playlist.getId()){
                result = true;
            }
        }
        return result;
    }
}

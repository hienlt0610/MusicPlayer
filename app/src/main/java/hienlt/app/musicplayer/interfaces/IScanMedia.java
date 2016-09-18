package hienlt.app.musicplayer.interfaces;

import java.util.ArrayList;

import hienlt.app.musicplayer.models.Song;

/**
 * Created by hienl_000 on 4/7/2016.
 */
public interface IScanMedia {
    void foundMp3(Song song, String path);
    void finallyScanMedia(ArrayList<Song> songs);
}

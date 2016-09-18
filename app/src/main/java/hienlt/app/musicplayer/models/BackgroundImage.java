package hienlt.app.musicplayer.models;

import android.graphics.Bitmap;

/**
 * Created by hienl_000 on 5/7/2016.
 */
public class BackgroundImage {
    private int resID;
    private String path;
    private boolean isDefaultBackground;
    private Bitmap thumb;

    public BackgroundImage() {
    }
    public BackgroundImage(int resID, Bitmap bitmap){
        this.resID = resID;
        this.isDefaultBackground = true;
        this.thumb = bitmap;
    }
    public BackgroundImage(String path, Bitmap bitmap){
        this.path = path;
        this.isDefaultBackground = false;
        this.thumb = bitmap;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        this.isDefaultBackground = false;
    }

    public boolean isDefaultBackground() {
        return isDefaultBackground;
    }

    public void setIsDefaultBackground(boolean isDefaultBackground) {
        this.isDefaultBackground = isDefaultBackground;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public int getId() {
        return resID;
    }

    public void setId(int resID) {
        this.resID = resID;
        this.isDefaultBackground = true;
    }
}

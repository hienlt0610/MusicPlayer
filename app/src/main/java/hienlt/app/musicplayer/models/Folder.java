package hienlt.app.musicplayer.models;

/**
 * Created by hienl_000 on 5/4/2016.
 */
public class Folder {
    private String folderName;
    private String folderPath;
    private int songNum;

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public int getSongNum() {
        return songNum;
    }

    public void setSongNum(int songNum) {
        this.songNum = songNum;
    }
}

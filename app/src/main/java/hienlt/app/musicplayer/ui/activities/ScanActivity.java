package hienlt.app.musicplayer.ui.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.asynctasks.ScanMusicAsynctask;
import hienlt.app.musicplayer.interfaces.IScanMedia;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.utils.Common;

public class ScanActivity extends AppCompatActivity implements IScanMedia{
    ScanMusicAsynctask musicAsynctask;
    public TextView tvNumMusicScan,tvMusicUrl,tvFile;
    public static final String ACTION_SCAN_SUCCESS = "hienlt.app.musicplayer.ACTION_SCAN_SUCCESS";
    public int musicNum = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        tvNumMusicScan = (TextView) findViewById(R.id.tvNumMusicFound);
        tvMusicUrl = (TextView) findViewById(R.id.tvMusicUrl);
        tvFile = (TextView) findViewById(R.id.tvFile);


        tvNumMusicScan.setText("");
        tvFile.setText("");
        tvMusicUrl.setText("");

        musicAsynctask = new ScanMusicAsynctask(this,this);
        musicAsynctask.execute();
        setResult(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(musicAsynctask!=null)
            musicAsynctask.cancelTask();
    }

    @Override
    public void foundMp3(Song song, String path) {
        if(song!=null) {
            musicNum++;
            tvMusicUrl.setText(path);
        }
        tvNumMusicScan.setText("Tìm thấy : "+musicNum+" bài hát");
        tvFile.setText(path);
    }

    @Override
    public void finallyScanMedia(ArrayList<Song> songs) {
        tvNumMusicScan.setText("Đã cập nhật : "+musicNum+" bài hát");
        tvMusicUrl.setText("Đã scan thành công!!!");
        tvFile.setText("");
    }
}

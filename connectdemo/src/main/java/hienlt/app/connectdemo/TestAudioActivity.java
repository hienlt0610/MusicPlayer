package hienlt.app.connectdemo;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.tag.TagException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import hienlt.app.connectdemo.view.DefaultLrcBuilder;
import hienlt.app.connectdemo.view.ILrcBuilder;
import hienlt.app.connectdemo.view.ILrcView;
import hienlt.app.connectdemo.view.LrcRow;
import hienlt.app.connectdemo.view.LrcView;

public class TestAudioActivity extends AppCompatActivity {
    LrcView mLrcView;
    int time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_audio);
        mLrcView = new LrcView(this, null);
        setContentView(mLrcView);
        mLrcView.setBackgroundColor(Color.BLUE);

        String lyric = loadAssetTextAsString(this, "stc.lrc");
        ILrcBuilder builder = new DefaultLrcBuilder();
        List<LrcRow> rows = builder.getLrcRows(lyric);
        mLrcView.setLrc(rows);
        mLrcView.setListener(new ILrcView.LrcViewListener() {
            @Override
            public void onLrcSeeked(int newPosition, LrcRow row) {
                mLrcView.seekLrc(newPosition, false);
            }
        });
        final Handler handler = new Handler();
        time = 0;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                time+=200;
                mLrcView.seekLrcToTime(time);
                handler.postDelayed(this,200);
            }
        },200);
    }


    private String loadAssetTextAsString(Context context, String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = context.getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e("hienlt0610", "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("hienlt0610", "Error closing asset " + name);
                }
            }
        }

        return null;
    }
}

package hienlt.app.musicplayer.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.core.HLBaseFragment;
import hienlt.app.musicplayer.media.MusicService;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.ui.activities.PlaybackActivity;
import hienlt.app.musicplayer.ui.lyric_view.DefaultLrcBuilder;
import hienlt.app.musicplayer.ui.lyric_view.ILrcView;
import hienlt.app.musicplayer.ui.lyric_view.LrcRow;
import hienlt.app.musicplayer.ui.lyric_view.LrcView;
import hienlt.app.musicplayer.utils.CacheManager;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.DiskLruFileCache;

/**
 * Created by hienl_000 on 5/9/2016.
 */
public class LyricFragment extends HLBaseFragment implements ILrcView.LrcViewListener {
    private LrcView lrcView;
    private DiskLruFileCache fileCache;
    public static final String SONG_KEY = "song_key";
    List<LrcRow> lrcRows;
    MusicService service;

    @Override
    protected int getLayout() {
        return R.layout.fragment_lyric;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lrcView = (LrcView) view.findViewById(R.id.lyricView);
        fileCache = DiskLruFileCache.newInstance(Common.LYRIC_CACHE_FOLDER);
        lrcView.setListener(this);
    }

    public void initLyric(String lyricRaw) {
        DefaultLrcBuilder lrcBuilder = new DefaultLrcBuilder();
        lrcRows = lrcBuilder.getLrcRows(lyricRaw);
        if (lrcRows != null) {
            lrcView.setLrc(lrcRows);
        } else{
            lrcView.setLrc(null);
            lrcView.setLoadingTipText("Không có lyric");
        }
    }

    public void seekLrcToTime(long time) {
        lrcView.seekLrcToTime(time);
    }

    @Override
    public void onLrcSeeked(int newPosition, LrcRow row) {
        LrcRow lrcRow = lrcRows.get(newPosition);
        if (lrcRow != null) {
            ((PlaybackActivity) getActivity()).onLrcSeeked((int) lrcRow.time);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        service = ((PlaybackActivity) getActivity()).mService;
        if (service != null) {
            updateLyric();
        }
    }

    public void updateLyric() {
        if(service == null) return;
        Song song = service.getCurrentSong();
        if (song == null) return;
        DiskLruFileCache lyricCache = CacheManager.getInstance().getLyricCache();
        if (lyricCache != null) {
            String lyric = lyricCache.getFileContent(song.getTitle());
            if (lyric != null) {
                initLyric(lyric);
            } else
                initLyric(null);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}

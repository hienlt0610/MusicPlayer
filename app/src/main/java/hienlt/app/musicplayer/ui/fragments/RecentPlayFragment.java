package hienlt.app.musicplayer.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.util.ArrayList;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.adapter.FolderAdapter;
import hienlt.app.musicplayer.adapter.LocalSongRecyclerViewAdapter;
import hienlt.app.musicplayer.core.HLBaseFragment;
import hienlt.app.musicplayer.interfaces.IMusicServiceConnection;
import hienlt.app.musicplayer.media.MusicService;
import hienlt.app.musicplayer.media.MusicServiceConnection;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.ui.activities.PlaybackActivity;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.FragmentUtils;
import hienlt.app.musicplayer.utils.ItemClickSupport;

/**
 * Created by hienl_000 on 5/4/2016.
 */
public class RecentPlayFragment extends HLBaseFragment implements ItemClickSupport.OnItemClickListener {
    private RecyclerView recyclerView;
    private ArrayList<Song> listSong;
    private LocalSongRecyclerViewAdapter adapter;
    private MusicServiceConnection mServiceConnection;

    @Override
    protected int getLayout() {
        return R.layout.fragment_recyclerview;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mServiceConnection = new MusicServiceConnection(getActivity());

        listSong = SongProvider.getInstance(getActivity()).getListSongRecentPlay();
        adapter = new LocalSongRecyclerViewAdapter(getActivity(),listSong);
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(this);
    }

    @Override
    protected String getActionbarName() {
        return "Đã phát gần đây";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                popCurrentFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, final int position, View v) {
        Song song = listSong.get(position);
        File file = new File(song.getLocalDataSource());
        if (!file.exists()) {
            Common.showToast(getActivity(), "Bài hát này đã bị xóa, vui lòng cập nhật lại");
            return;
        }
        Intent iSelectSongPlay = new Intent(getActivity(), MusicService.class);
        mServiceConnection.connect(iSelectSongPlay, new IMusicServiceConnection() {
            @Override
            public void onConnected(MusicService service) {
                if (service.getCurrentSong() == null || !service.getCurrentSong().getId().equals(listSong.get(position).getId())) {
                    service.setSongPosition(position);
                    service.setPlayListSong(listSong);
                    service.setMediaType(MusicService.MediaType.Local);
                    service.playSong();
                }
                Intent intent = new Intent(getActivity(), PlaybackActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.no_change);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(MusicService.META_CHANGE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver,filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            listSong = SongProvider.getInstance(getActivity()).getListSongRecentPlay();
            adapter.replaceList(listSong);
        }
    };
}

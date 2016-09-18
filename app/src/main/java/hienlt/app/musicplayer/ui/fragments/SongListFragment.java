package hienlt.app.musicplayer.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.adapter.LocalSongRecyclerViewAdapter;
import hienlt.app.musicplayer.asynctasks.SongListAsynctask;
import hienlt.app.musicplayer.interfaces.IMusicServiceConnection;
import hienlt.app.musicplayer.media.MusicService;
import hienlt.app.musicplayer.media.MusicServiceConnection;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.ui.activities.PlaybackActivity;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.ItemClickSupport;

/**
 * Created by hienl_000 on 4/8/2016.
 */
public class SongListFragment extends Fragment {

    private FastScrollRecyclerView recyclerView;
    private MusicServiceConnection mServiceConnection;
    LocalSongRecyclerViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_song, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (FastScrollRecyclerView ) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(mLayoutManager);
        mServiceConnection = new MusicServiceConnection(getActivity());

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, final int position, View v) {
                final LocalSongRecyclerViewAdapter adapter = (LocalSongRecyclerViewAdapter) recyclerView.getAdapter();
                final ArrayList<Song> listSong = (ArrayList<Song>) adapter.getList();
                final Song song = adapter.getList().get(position);
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
                            ArrayList<Song> list = new ArrayList<Song>(listSong);
                            service.setPlayListSong(list);
                            service.setMediaType(MusicService.MediaType.Local);
                            service.playSong();
                        }
                        Intent intent = new Intent(getActivity(), PlaybackActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.no_change);

                    }
                });
            }
        });
    }

    public void searchSong(String key) {
        LocalSongRecyclerViewAdapter adapter = (LocalSongRecyclerViewAdapter) recyclerView.getAdapter();
        if (adapter != null)
            adapter.getFilter().filter(key);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new SongListAsynctask(getActivity(), recyclerView).execute();
    }
}

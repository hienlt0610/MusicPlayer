package hienlt.app.musicplayer.ui.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.util.ArrayList;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.adapter.LocalSongRecyclerViewAdapter;
import hienlt.app.musicplayer.adapter.PlaylistDetailListSongAdapter;
import hienlt.app.musicplayer.core.HLBaseFragment;
import hienlt.app.musicplayer.interfaces.IMusicServiceConnection;
import hienlt.app.musicplayer.media.MusicService;
import hienlt.app.musicplayer.media.MusicServiceConnection;
import hienlt.app.musicplayer.models.Folder;
import hienlt.app.musicplayer.models.Playlist;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.ui.activities.PlaybackActivity;
import hienlt.app.musicplayer.ui.dialog.SelectSongDialog;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.ItemClickSupport;

/**
 * Created by hienl_000 on 5/3/2016.
 */
public class FolderDetailFragment extends HLBaseFragment implements ItemClickSupport.OnItemClickListener {

    public static final String FOLDER_KEY = "folder_key";
    private Folder folder;
    private RecyclerView recyclerView;
    private LocalSongRecyclerViewAdapter adapter;
    private ArrayList<Song> listSong;
    private MusicServiceConnection mServiceConnection;

    public static FolderDetailFragment newInstance(String folderPath) {
        FolderDetailFragment fragment = new FolderDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FOLDER_KEY, folderPath);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_detail_playlist;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) return;
        String folderPath = bundle.getString(FOLDER_KEY);
        if (folderPath == null) return;
        if(!SongProvider.getInstance(getActivity()).isFolderExist(folderPath)) return;
        File file = new File(folderPath);
        String folderName = file.getName();
        if(folderName == null) return;
        //Set actionbar name
        setActionBarName(folderName);
        mServiceConnection = new MusicServiceConnection(getActivity());
        //Find view
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        //init
        listSong = SongProvider.getInstance(getActivity()).getListSongByFolder(folderPath);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new LocalSongRecyclerViewAdapter(getActivity(),listSong);
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(this);
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
}

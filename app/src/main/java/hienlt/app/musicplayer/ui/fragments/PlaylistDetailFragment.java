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
import hienlt.app.musicplayer.adapter.PlaylistDetailListSongAdapter;
import hienlt.app.musicplayer.core.HLBaseFragment;
import hienlt.app.musicplayer.interfaces.IMusicServiceConnection;
import hienlt.app.musicplayer.media.MusicService;
import hienlt.app.musicplayer.media.MusicServiceConnection;
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
public class PlaylistDetailFragment extends HLBaseFragment implements ItemClickSupport.OnItemClickListener {

    public static final String PLAYLIST_ID_KEY = "playlist_id_key";
    private Playlist pl;
    private RecyclerView recyclerView;
    private PlaylistDetailListSongAdapter adapter;
    private ArrayList<Song> listSong;
    private MusicServiceConnection mServiceConnection;

    public static PlaylistDetailFragment newInstance(int playlistID) {
        PlaylistDetailFragment fragment = new PlaylistDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PLAYLIST_ID_KEY, playlistID);
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
        int playlistID = bundle.getInt(PLAYLIST_ID_KEY, 0);
        pl = SongProvider.getInstance(getActivity()).getPlaylistByID(playlistID);
        if (pl == null) return;

        //Set actionbar name
        setActionBarName(pl.getTitle());

        //Find view
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mServiceConnection = new MusicServiceConnection(getActivity());

        //init
        listSong = SongProvider.getInstance(getActivity()).getListSongByPlaylist(playlistID);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PlaylistDetailListSongAdapter(getActivity(), listSong, playlistID);
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
            case R.id.action_add:
                final SelectSongDialog dialogSelect = new SelectSongDialog(getActivity());
                dialogSelect.setListSelected(listSong);
                dialogSelect.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<Song> listSelected = dialogSelect.getListSongSelected();
                        int num = SongProvider.getInstance(getActivity()).removeAllSongFromPlaylist(pl.getId());
                        SongProvider.getInstance(getActivity()).insertSongToPlayList(pl.getId(), listSelected);
                        adapter.replaceList(listSelected);
                    }
                });
                dialogSelect.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialogSelect.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_playlist, menu);
        super.onCreateOptionsMenu(menu, inflater);
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

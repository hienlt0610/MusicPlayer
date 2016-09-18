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
import hienlt.app.musicplayer.adapter.LocalSongRecyclerViewAdapter;
import hienlt.app.musicplayer.core.HLBaseFragment;
import hienlt.app.musicplayer.interfaces.IMusicServiceConnection;
import hienlt.app.musicplayer.media.MusicService;
import hienlt.app.musicplayer.media.MusicServiceConnection;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.provider.MusicDBLoader;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.ui.activities.PlaybackActivity;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.ItemClickSupport;

/**
 * Created by hienl_000 on 5/3/2016.
 */
public class LikeListFragment extends HLBaseFragment {
    private RecyclerView recyclerView;
    private ArrayList<Song> list;
    private LocalSongRecyclerViewAdapter adapter;
    private MusicServiceConnection mServiceConnection;
    @Override
    protected int getLayout() {
        return R.layout.fragment_list_like;
    }

    @Override
    protected String getActionbarName() {
        return "Danh sách bài hát yêu thích";
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        list = SongProvider.getInstance(getActivity()).getListSongLike();
        adapter = new LocalSongRecyclerViewAdapter(getActivity(),list);
        recyclerView.setAdapter(adapter);
        mServiceConnection = new MusicServiceConnection(getActivity());

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, final int position, View v) {
                Song song = list.get(position);
                File file = new File(song.getLocalDataSource());
                if (!file.exists()) {
                    Common.showToast(getActivity(), "Bài hát này đã bị xóa, vui lòng cập nhật lại");
                    return;
                }
                Intent iSelectSongPlay = new Intent(getActivity(), MusicService.class);
                mServiceConnection.connect(iSelectSongPlay, new IMusicServiceConnection() {
                    @Override
                    public void onConnected(MusicService service) {
                        if (service.getCurrentSong() == null || !service.getCurrentSong().getId().equals(list.get(position).getId())) {
                            service.setSongPosition(position);
                            service.setPlayListSong(list);
                            service.setMediaType(MusicService.MediaType.Local);
                            service.playSong();
                        }
                        Intent intent = new Intent(getActivity(), PlaybackActivity.class);
                        getActivity().startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(LocalSongRecyclerViewAdapter.ACTION_LIKE_STATE_CHANGE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver,filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null){
                String action = intent.getAction();
                if(action!=null){
                    if(action.equals(LocalSongRecyclerViewAdapter.ACTION_LIKE_STATE_CHANGE)){
                        String songID = intent.getStringExtra(MusicDBLoader.SONG_COLUMN_ID);
                        boolean isLike = intent.getBooleanExtra(MusicDBLoader.IS_LIKE,false);
                        if(songID!=null){
                            int i = 0;
                            for(Song song:list){
                                if(song.getId().equals(songID)){
                                    list.remove(i);
                                    adapter.notifyDataSetChanged();
                                    return;
                                }
                                i++;
                            }
                        }
                    }
                }
            }
        }
    };
}

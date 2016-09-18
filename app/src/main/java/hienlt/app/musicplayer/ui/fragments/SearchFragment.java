package hienlt.app.musicplayer.ui.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.adapter.LocalSongRecyclerViewAdapter;
import hienlt.app.musicplayer.core.HLBaseFragment;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.ItemClickSupport;

/**
 * Created by hienl_000 on 5/11/2016.
 */
public class SearchFragment extends HLBaseFragment implements ItemClickSupport.OnItemClickListener {
    RecyclerView recyclerView;
    LocalSongRecyclerViewAdapter adapter;
    ArrayList<Song> songArraylist;
    @Override
    protected int getLayout() {
        return R.layout.fragment_recyclerview;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        songArraylist = SongProvider.getInstance(getActivity()).getListSong();
        adapter = new LocalSongRecyclerViewAdapter(getActivity(),songArraylist);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(this);
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

    }
}

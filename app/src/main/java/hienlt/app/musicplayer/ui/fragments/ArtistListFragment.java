package hienlt.app.musicplayer.ui.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.adapter.ArtistRecyclerViewAdapter;
import hienlt.app.musicplayer.asynctasks.ArtistListAsynctask;
import hienlt.app.musicplayer.core.HLBaseFragment;
import hienlt.app.musicplayer.models.Artist;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.FragmentUtils;
import hienlt.app.musicplayer.utils.ItemClickSupport;

/**
 * Created by hienl_000 on 4/24/2016.
 */
public class ArtistListFragment extends HLBaseFragment implements ItemClickSupport.OnItemClickListener {
    private RecyclerView recyclerView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_list_artist;
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        ArtistRecyclerViewAdapter adapter = (ArtistRecyclerViewAdapter) recyclerView.getAdapter();
        ArrayList<Artist> list = adapter.getList();
        String artist = list.get(position).getArtist();
        FragmentUtils.addStackFragment(getFragmentManager(),ArtistDetailFragment.getInstance(artist),true,true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new ArtistListAsynctask(getActivity(),recyclerView).execute();
    }
}

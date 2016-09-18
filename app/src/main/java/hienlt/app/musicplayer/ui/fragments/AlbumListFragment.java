package hienlt.app.musicplayer.ui.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.adapter.AlbumRecyclerViewAdapter;
import hienlt.app.musicplayer.asynctasks.AlbumListAsynctask;
import hienlt.app.musicplayer.core.HLBaseFragment;
import hienlt.app.musicplayer.models.Album;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.utils.FragmentUtils;
import hienlt.app.musicplayer.utils.ItemClickSupport;

/**
 * Created by hienl_000 on 4/24/2016.
 */
public class AlbumListFragment extends HLBaseFragment implements ItemClickSupport.OnItemClickListener {
    RecyclerView recyclerView;
    @Override
    protected int getLayout() {
        return R.layout.fragment_list_album;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(this);
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        AlbumRecyclerViewAdapter adapter = (AlbumRecyclerViewAdapter) recyclerView.getAdapter();
        ArrayList<Album> listAlbum = adapter.getList();
        String albumName = listAlbum.get(position).getName();
        FragmentUtils.addStackFragment(getActivity().getSupportFragmentManager(),AlbumDetailFragment.getInstance(albumName),true,true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new AlbumListAsynctask(getActivity(),recyclerView).execute();
    }
}

package hienlt.app.musicplayer.ui.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.adapter.FolderAdapter;
import hienlt.app.musicplayer.adapter.PlaylistAdapter;
import hienlt.app.musicplayer.core.HLBaseFragment;
import hienlt.app.musicplayer.models.Folder;
import hienlt.app.musicplayer.models.Playlist;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.FragmentUtils;
import hienlt.app.musicplayer.utils.ItemClickSupport;

/**
 * Created by hienl_000 on 5/4/2016.
 */
public class FolderFragment extends HLBaseFragment implements ItemClickSupport.OnItemClickListener {
    private RecyclerView recyclerView;
    private ArrayList<Folder> folders;
    private FolderAdapter adapter;

    @Override
    protected int getLayout() {
        return R.layout.fragment_folder;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        folders = SongProvider.getInstance(getActivity()).getListFolder();
        adapter = new FolderAdapter(getActivity(),folders);
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
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        FragmentUtils.addStackFragment(getFragmentManager(), FolderDetailFragment.newInstance(folders.get(position).getFolderPath()), true, true);
    }
}

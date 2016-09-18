package hienlt.app.musicplayer.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.adapter.PlaylistAdapter;
import hienlt.app.musicplayer.core.HLBaseFragment;
import hienlt.app.musicplayer.models.Playlist;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.ui.dialog.SelectSongDialog;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.FragmentUtils;
import hienlt.app.musicplayer.utils.ItemClickSupport;
import hienlt.app.musicplayer.utils.SystemUtils;

/**
 * Created by hienl_000 on 5/3/2016.
 */
public class PlaylistFragment extends HLBaseFragment implements ItemClickSupport.OnItemClickListener {
    private RecyclerView recyclerView;
    private ArrayList<Playlist> pls;
    private PlaylistAdapter adapter;

    @Override
    protected int getLayout() {
        return R.layout.fragment_playlist;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        pls = SongProvider.getInstance(getActivity()).getAllPlaylist();
        Collections.sort(pls, new Comparator<Playlist>() {
            @Override
            public int compare(Playlist left, Playlist right) {
                return (right.getId() > left.getId()) ? 1 : (right.getId() < left.getId()) ? -1 : 0;
            }
        });
        adapter = new PlaylistAdapter(getActivity(), pls);
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(this);
    }

    @Override
    protected String getActionbarName() {
        return "Danh sách phát";
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_playlist, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().getSupportFragmentManager().popBackStack();
        } else if (id == R.id.action_add) {
            showDiaglogAddPlaylist();
        }
        return true;
    }

    /**
     * Hiển thị dialog thêm playlist
     */
    private void showDiaglogAddPlaylist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thêm playlist mới");
        builder.setCancelable(true);
        final EditText editText = new EditText(getActivity());
        editText.setSingleLine(true);
        int paddingTopBottom = SystemUtils.convertDpToPx(getActivity().getResources(), 5);
        int paddingLeftRight = SystemUtils.convertDpToPx(getActivity().getResources(), 15);
        builder.setView(editText, paddingLeftRight, paddingTopBottom, paddingLeftRight, paddingTopBottom);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface dialog, int arg1) {
                final String playlistName = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(playlistName)) {
                    final SelectSongDialog selectSongDialog = new SelectSongDialog(getActivity());
                    selectSongDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int id = (int) SongProvider.getInstance(getActivity()).insertPlaylist(playlistName);
                            if (id != -1) {
                                ArrayList<Song> listSelected = selectSongDialog.getListSongSelected();
                                if (listSelected.size() > 0) {
                                    SongProvider.getInstance(getActivity()).insertSongToPlayList(id, listSelected);
                                }
                                Playlist playlist = new Playlist(id, playlistName);
                                playlist.setSongNum(listSelected.size());
                                adapter.addPlaylist(playlist);
                                Common.showToast(getActivity(), "Thêm playlist '" + playlistName + "' thành công");
                            } else {
                                Common.showToast(getActivity(), "Lỗi trong quá trình thêm playlist");
                            }
                        }
                    });

                    selectSongDialog.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    selectSongDialog.show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        FragmentUtils.addStackFragment(getFragmentManager(), PlaylistDetailFragment.newInstance(pls.get(position).getId()), true, true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            Common.showLog(PlaylistFragment.class.getName()+":Hidden");
        }else{
            pls.clear();
            pls.addAll(SongProvider.getInstance(getActivity()).getAllPlaylist());
            adapter.notifyDataSetChanged();
        }
    }
}

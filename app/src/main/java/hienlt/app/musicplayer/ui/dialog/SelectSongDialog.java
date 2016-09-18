package hienlt.app.musicplayer.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.adapter.SelectDialogAdapter;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.utils.Common;

/**
 * Created by hienl_000 on 5/3/2016.
 */
public class SelectSongDialog extends AlertDialog.Builder {
    private ArrayList<Song> songs;
    private Context context;
    private ArrayList<Song> listSelected;

    SelectDialogAdapter adapter;

    public SelectSongDialog(Context context) {
        super(context);
        this.context = context;
    }

    public void setListSong(ArrayList<Song> listSong) {
        this.songs = listSong;
    }

    @Override
    public AlertDialog show() {
        setTitle("Chọn bài hát");
        View viewList = LayoutInflater.from(context).inflate(R.layout.dialog_select_song, null);
        setView(viewList);
        RecyclerView recyclerView = (RecyclerView) viewList.findViewById(R.id.recycler_view);
        songs = SongProvider.getInstance(context).getListSong();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new SelectDialogAdapter(context, songs);
        recyclerView.setAdapter(adapter);
        if(listSelected!=null)
            adapter.setListSelected(listSelected);
        return super.show();
    }

    public ArrayList<Song> getListSongSelected() {
        return adapter.getListSongSelected();
    }


    public void setListSelected(ArrayList<Song> listSelected){
        this.listSelected = listSelected;
    }
}

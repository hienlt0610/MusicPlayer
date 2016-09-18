package hienlt.app.musicplayer.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import hienlt.app.musicplayer.adapter.LocalSongRecyclerViewAdapter;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.provider.SongProvider;

/**
 * Created by hienl_000 on 5/11/2016.
 */
public class SongListAsynctask extends AsyncTask<Void,Void,ArrayList<Song>> {

    Context context;
    RecyclerView recyclerView;
    LocalSongRecyclerViewAdapter adapter;

    public SongListAsynctask(Context context, RecyclerView recyclerView){
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ArrayList<Song> songs = new ArrayList<>(0);
        adapter = new LocalSongRecyclerViewAdapter(context,songs);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected ArrayList<Song> doInBackground(Void... params) {
        ArrayList<Song> songArrayList = SongProvider.getInstance(context).getListSong();
        return songArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<Song> list) {
        super.onPostExecute(list);
        adapter.replaceList(list);
    }
}

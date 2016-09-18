package hienlt.app.musicplayer.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import hienlt.app.musicplayer.adapter.AlbumRecyclerViewAdapter;
import hienlt.app.musicplayer.adapter.LocalSongRecyclerViewAdapter;
import hienlt.app.musicplayer.models.Album;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.provider.SongProvider;

/**
 * Created by hienl_000 on 5/11/2016.
 */
public class AlbumListAsynctask extends AsyncTask<Void,Void,ArrayList<Album>> {

    Context context;
    RecyclerView recyclerView;

    public AlbumListAsynctask(Context context, RecyclerView recyclerView){
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @Override
    protected ArrayList<Album> doInBackground(Void... params) {
        ArrayList<Album> albumArrayList = SongProvider.getInstance(context).getAlbums();
        return albumArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<Album> list) {
        super.onPostExecute(list);
        AlbumRecyclerViewAdapter adapter = new AlbumRecyclerViewAdapter(context,list);
        recyclerView.setAdapter(adapter);
    }
}

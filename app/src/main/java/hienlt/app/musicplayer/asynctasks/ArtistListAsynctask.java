package hienlt.app.musicplayer.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import hienlt.app.musicplayer.adapter.ArtistRecyclerViewAdapter;
import hienlt.app.musicplayer.adapter.LocalSongRecyclerViewAdapter;
import hienlt.app.musicplayer.models.Album;
import hienlt.app.musicplayer.models.Artist;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.provider.SongProvider;

/**
 * Created by hienl_000 on 5/11/2016.
 */
public class ArtistListAsynctask extends AsyncTask<Void,Void,ArrayList<Artist>> {

    Context context;
    RecyclerView recyclerView;
    ArtistRecyclerViewAdapter adapter;

    public ArtistListAsynctask(Context context, RecyclerView recyclerView){
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        adapter = new ArtistRecyclerViewAdapter(context,new ArrayList<Artist>());
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected ArrayList<Artist> doInBackground(Void... params) {
        ArrayList<Artist> artistsArrayList = SongProvider.getInstance(context).getListArtists();
        return artistsArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<Artist> list) {
        super.onPostExecute(list);
        adapter.replaceList(list);
    }
}

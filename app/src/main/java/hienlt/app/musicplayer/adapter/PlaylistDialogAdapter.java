package hienlt.app.musicplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.provider.MusicDBLoader;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.ui.dialog.PlaylistDialog;

/**
 * Created by hienl_000 on 4/30/2016.
 */
public class PlaylistDialogAdapter extends RecyclerView.Adapter<PlaylistDialogAdapter.PlaylistDialogViewHolder> implements View.OnClickListener {
    private Context context;
    ArrayList<Song> list;
    Song currSong;
    PlaylistDialog playlistDialog;

    public PlaylistDialogAdapter(Context context, ArrayList<Song> songs, PlaylistDialog dialog){
        this.context = context;
        this.list = songs;
        this.playlistDialog = dialog;
    }
    @Override
    public PlaylistDialogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_playlist_song, parent, false);
        return new PlaylistDialogViewHolder(view);
    }

    public void setSongPlaying(Song song){
        this.currSong = song;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(PlaylistDialogViewHolder holder, int position) {
        Song song = list.get(position);
        if(currSong != null && song.getId().equals(currSong.getId())) {
            holder.viewActive.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.GONE);
        }
        else{
            holder.viewActive.setVisibility(View.INVISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        }
        holder.tvSongTitle.setText(song.getTitle());
        holder.tvArtist.setText(song.getArtist());
        holder.btnDelete.setTag(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        list.remove(position);
        Intent intent = new Intent(PlaylistDialog.ACTION_REMOVE_SONG_IN_LIST);
        intent.putExtra(MusicDBLoader.SONG_COLUMN_ID,list.get(position).getId());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        notifyDataSetChanged();
    }

    class PlaylistDialogViewHolder extends RecyclerView.ViewHolder{
        View viewActive;
        TextView tvSongTitle, tvArtist;
        ImageButton btnDelete;
        public PlaylistDialogViewHolder(View itemView) {
            super(itemView);
            viewActive = itemView.findViewById(R.id.viewActive);
            tvSongTitle = (TextView) itemView.findViewById(R.id.tvSongTitle);
            tvArtist = (TextView) itemView.findViewById(R.id.tvArtist);
            btnDelete = (ImageButton) itemView.findViewById(R.id.btnDelete);

            btnDelete.setOnClickListener(playlistDialog.onClickListener);

        }
    }

}

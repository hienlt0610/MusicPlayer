package hienlt.app.musicplayer.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.adapter.PlaylistDialogAdapter;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.utils.ItemClickSupport;
import hienlt.app.musicplayer.utils.SystemUtils;

/**
 * Created by hienl_000 on 4/30/2016.
 */
public class PlaylistDialog extends Dialog implements ItemClickSupport.OnItemClickListener {
    private RecyclerView recyclerView;
    private TextView tvSongTitle;
    private ArrayList<Song> listSong;
    private Context context;
    private PlaylistDialogAdapter adapter;
    public static final String ACTION_REMOVE_SONG_IN_LIST = "hienlt.app.musicplayer.ACTION_REMOVE_SONG_IN_LIST";
    private Song currentSong;
    public AdapterView.OnItemClickListener listener;
    public View.OnClickListener onClickListener;

    public PlaylistDialog(Context context) {
        super(context);
        this.context = context;
        listSong = new ArrayList<Song>();
    }

    public void setListSong(ArrayList<Song> songs) {
        this.listSong = songs;
    }

    public void setCurrentPlay(Song song) {
        this.currentSong = song;
    }

    public void replaceCurrentPlay(Song song) {
        adapter.setSongPlaying(song);
        tvSongTitle.setText("Play: "+song.getTitle());
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener clickListener) {
        this.listener = clickListener;
    }

    public void setOnDeleteClickListener(View.OnClickListener onDeleteClickListener) {
        this.onClickListener = onDeleteClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_playlist);
        int statusHeight = SystemUtils.getStatusBarHeight(context.getResources());
        int actionHeight = SystemUtils.getActionBarHeight(context);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels);
        int height = (int) ((context.getResources().getDisplayMetrics().heightPixels - statusHeight - actionHeight) * 0.9);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, height);
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        tvSongTitle = (TextView) findViewById(R.id.tvSongTitle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(this);
        adapter = new PlaylistDialogAdapter(context, listSong, this);
        adapter.setSongPlaying(currentSong);
        recyclerView.setAdapter(adapter);
        int offset = 0;
        offset = listSong.indexOf(currentSong);
        linearLayoutManager.scrollToPosition(offset - 4);
        tvSongTitle.setText("Play: " + currentSong.getTitle());
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        if (listener != null)
            listener.onItemClick(null, v, position, 0);

    }

    public void replaceList(ArrayList<Song> list) {
        this.listSong.clear();
        this.listSong.addAll(list);
        adapter.notifyDataSetChanged();
    }
}

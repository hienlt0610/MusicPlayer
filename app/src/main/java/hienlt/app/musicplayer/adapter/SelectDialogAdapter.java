package hienlt.app.musicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.utils.Common;

/**
 * Created by hienl_000 on 5/3/2016.
 */
public class SelectDialogAdapter extends RecyclerView.Adapter<SelectDialogAdapter.SelectDialogViewHolder> implements View.OnClickListener {

    private Context context;
    private ArrayList<Song> list;
    private boolean[] arrChecked;

    public SelectDialogAdapter(Context context, ArrayList<Song> songArraylist) {
        this.context = context;
        this.list = songArraylist;
        initArrayChecked();
    }

    private void initArrayChecked() {
        arrChecked = new boolean[list.size()];
        int size = list.size();
        for (int i = 0; i < size; i++) {
            arrChecked[i] = false;
        }
    }

    public void setListSelected(ArrayList<Song> listSelected) {
        int size = listSelected.size();
        for (int i = 0; i < size; i++) {
            Song song = listSelected.get(i);
            if(this.list.contains(song))
                arrChecked[this.list.indexOf(song)] = true;
        }
    }

    @Override
    public SelectDialogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_list_select_song, parent, false);
        return new SelectDialogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectDialogViewHolder holder, int position) {
        Song song = list.get(position);
        boolean isChecked = arrChecked[position];

        holder.tvSongName.setText(song.getTitle());
        holder.tvArtist.setText(song.getArtist());
        holder.chkSeleted.setChecked(isChecked);
        holder.chkSeleted.setTag(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public ArrayList<Song> getListSongSelected() {
        ArrayList<Song> listChecked = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (arrChecked[i] == true) {
                listChecked.add(list.get(i));
            }
        }
        return listChecked;
    }

    @Override
    public void onClick(View v) {
        int pos = (int) v.getTag();
        CheckBox checkBox = (CheckBox) v;
        arrChecked[pos] = checkBox.isChecked();
    }

    class SelectDialogViewHolder extends RecyclerView.ViewHolder {
        TextView tvSongName, tvArtist;
        CheckBox chkSeleted;

        public SelectDialogViewHolder(View itemView) {
            super(itemView);
            tvSongName = (TextView) itemView.findViewById(R.id.tvSongName);
            tvArtist = (TextView) itemView.findViewById(R.id.tvArtist);
            chkSeleted = (CheckBox) itemView.findViewById(R.id.chkSelected);
            chkSeleted.setOnClickListener(SelectDialogAdapter.this);
        }
    }
}

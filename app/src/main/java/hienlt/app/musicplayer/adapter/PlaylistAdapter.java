package hienlt.app.musicplayer.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.interfaces.IMusicServiceConnection;
import hienlt.app.musicplayer.media.MusicService;
import hienlt.app.musicplayer.media.MusicServiceConnection;
import hienlt.app.musicplayer.models.Playlist;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.SystemUtils;

/**
 * Created by hienl_000 on 5/3/2016.
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> implements View.OnClickListener {

    private Context context;
    private ArrayList<Playlist> pls;
    private MusicServiceConnection connection;

    public PlaylistAdapter(Context context, ArrayList<Playlist> pls) {
        this.context = context;
        this.pls = pls;
        connection = new MusicServiceConnection(context);
    }

    public void addPlaylist(Playlist playlist) {
        this.pls.add(0, playlist);
        notifyDataSetChanged();
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_playlist, parent, false);
        return new PlaylistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {
        Playlist playlist = pls.get(position);
        holder.tvPlaylist.setText(playlist.getTitle());
        holder.tvSongNum.setText("(" + playlist.getSongNum() + " bài hát)");
        holder.btnActionMore.setTag(position);
    }

    @Override
    public int getItemCount() {
        return pls.size();
    }

    private Playlist playlist;
    private ArrayList<Song> listSong;
    @Override
    public void onClick(final View v) {
        final int pos = (int) v.getTag();
        playlist = pls.get(pos);
        listSong = SongProvider.getInstance(context).getListSongByPlaylist(playlist.getId());
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.getMenu().add(0, 0, 0, context.getString(R.string.play));
        popupMenu.getMenu().add(0, 1, 0, context.getString(R.string.add_into_playback));
        popupMenu.getMenu().add(0, 2, 0, context.getString(R.string.rename_playlist));
        popupMenu.getMenu().add(0, 3, 0, context.getString(R.string.delete_playlist));
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 0: //Play
                        if (listSong.size() == 0) {
                            Common.showToast(context, "Không có bài hát nào");
                            break;
                        }
                        Intent serviceIntent = new Intent(context, MusicService.class);
                        connection.connect(serviceIntent, new IMusicServiceConnection() {
                            @Override
                            public void onConnected(MusicService service) {
                                service.setPlayListSong(listSong);
                                service.setMediaType(MusicService.MediaType.Local);
                                service.setSongPosition(0);
                                service.playSong();
                            }
                        });
                        break;
                    case 1: //Thêm danh sách nghe
                        if (listSong.size() == 0) {
                            Common.showToast(context, "Không có bài hát nào");
                            break;
                        }
                        serviceIntent = new Intent(context, MusicService.class);
                        connection.connect(serviceIntent, new IMusicServiceConnection() {
                            @Override
                            public void onConnected(MusicService service) {
                                service.addPlaylistSong(listSong);
                                if (service.getState() == MusicService.MusicState.Stop)
                                    service.playSong();
                            }
                        });
                        break;
                    case 2: //Đổi tên
                        showDialogRenamePlaylist();
                        break;
                    case 3: //Xóa
                        showDialogDeletePlaylist();
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void showDialogRenamePlaylist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Thay đổi tên playlist");
        builder.setCancelable(true);
        final EditText editText = new EditText(context);
        editText.setSingleLine(true);
        editText.setText(playlist.getTitle());
        editText.setSelection(playlist.getTitle().length());
        int paddingTopBottom = SystemUtils.convertDpToPx(context.getResources(), 5);
        int paddingLeftRight = SystemUtils.convertDpToPx(context.getResources(), 15);
        builder.setView(editText, paddingLeftRight, paddingTopBottom, paddingLeftRight, paddingTopBottom);

        builder.setPositiveButton("Thay đổi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPlaylistName = editText.getText().toString().trim();
                if(SongProvider.getInstance(context).updatePlaylist(playlist.getId(),newPlaylistName)){
                    int index = pls.indexOf(playlist);
                    if(index != -1){
                        Playlist updatePlaylist = pls.get(index);
                        updatePlaylist.setTitle(newPlaylistName);
                        notifyDataSetChanged();
                        Common.showToast(context,"Đổi tên thành công!!!");
                    }
                }
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }


    private void showDialogDeletePlaylist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xóa playlist");
        builder.setMessage("Bạn có muốn xóa playlist '"+playlist.getTitle()+"' chứ?");
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SongProvider.getInstance(context).removePlaylist(playlist.getId())) {
                    pls.remove(playlist);
                    notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlaylist, tvSongNum;
        ImageButton btnActionMore;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            tvPlaylist = (TextView) itemView.findViewById(R.id.tvPlaylist);
            btnActionMore = (ImageButton) itemView.findViewById(R.id.btnAction);
            tvSongNum = (TextView) itemView.findViewById(R.id.tvSongNum);
            btnActionMore.setOnClickListener(PlaylistAdapter.this);
        }
    }
}

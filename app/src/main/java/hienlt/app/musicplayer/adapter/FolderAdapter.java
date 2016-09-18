package hienlt.app.musicplayer.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import hienlt.app.musicplayer.models.Folder;
import hienlt.app.musicplayer.models.Playlist;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.SystemUtils;

/**
 * Created by hienl_000 on 5/4/2016.
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> implements View.OnClickListener{
    private Context context;
    private ArrayList<Folder> listFolder;
    private MusicServiceConnection connection;


    public FolderAdapter(Context context, ArrayList<Folder> pls) {
        this.context = context;
        this.listFolder = pls;
        connection = new MusicServiceConnection(context);
    }

    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_folder_row, parent, false);
        return new FolderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FolderViewHolder holder, int position) {
        Folder folder = listFolder.get(position);
        holder.tvFolderName.setText(folder.getFolderName());
        holder.tvFolderPath.setText(folder.getFolderPath());
        holder.tvSongNum.setText("(" + folder.getSongNum() + " bài hát)");
        holder.btnActionMore.setTag(position);
    }

    @Override
    public int getItemCount() {
        return listFolder.size();
    }

    Folder folder;
    ArrayList<Song> listSong;
    @Override
    public void onClick(View v) {
        final int pos = (int) v.getTag();
        folder = listFolder.get(pos);
        listSong = SongProvider.getInstance(context).getListSongByFolder(folder.getFolderPath());
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.getMenu().add(0, 0, 0, context.getString(R.string.play));
        popupMenu.getMenu().add(0, 1, 0, context.getString(R.string.add_into_playback));
        popupMenu.getMenu().add(0, 2, 0, context.getString(R.string.add_into_playlist));
        popupMenu.show();

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
                                service.setSongPosition(0);
                                service.playSong();
                            }
                        });
                        break;
                    case 1: //thêm vào danh sách phát
                        if (listSong.size() == 0) {
                            Common.showToast(context, "Không có bài hát nào");
                            break;
                        }
                        serviceIntent = new Intent(context, MusicService.class);
                        connection.connect(serviceIntent, new IMusicServiceConnection() {
                            @Override
                            public void onConnected(MusicService service) {
                                service.addPlaylistSong(listSong);
                                service.setMediaType(MusicService.MediaType.Local);
                                if (service.getState() == MusicService.MusicState.Stop)
                                    service.playSong();
                            }
                        });
                        break;
                    case 2: //thêm vào playlist
                        showDialogAddtoPlaylist(listSong);
                        break;

                }
                return true;
            }
        });
    }


    /**
     * Hiển thị dialog thêm bài nhạc vào playlist
     * @param listSong
     */
    protected void showDialogAddtoPlaylist(final ArrayList<Song> listSong) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.add_into_playlist));
        final ArrayList<Playlist> pls = SongProvider.getInstance(context).getAllPlaylist();
        ArrayList<String> listPlsTitle = new ArrayList<>(pls.size());
        for (Playlist playlist : pls) {
            listPlsTitle.add(playlist.getTitle());
        }
        CharSequence[] arrPls = listPlsTitle.toArray(new CharSequence[listPlsTitle.size()]);
        builder.setItems(arrPls, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //Hiển thị dialog thêm playlist và add song
                    showDiaLogCreateAddSongPlaylist(listSong);
                    return;
                }
                which -= ((AlertDialog) dialog).getListView().getHeaderViewsCount();
                int num = SongProvider.getInstance(context).insertSongToPlayList(pls.get(which).getId(), listSong);
                Common.showToast(context, "Thêm thành công "+num+" bài hát vào playlist!!!");
            }
        });
        AlertDialog dialog = builder.create();
        View header = LayoutInflater.from(context).inflate(R.layout.header_add_playlist, null);
        dialog.getListView().addHeaderView(header);
        dialog.show();
    }

    /**
     * Hiển thị dialog thêm playlist
     */
    private void showDiaLogCreateAddSongPlaylist(final ArrayList<Song> list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Thêm playlist mới");
        builder.setCancelable(true);
        final EditText editText = new EditText(context);
        editText.setSingleLine(true);
        int paddingTopBottom = SystemUtils.convertDpToPx(context.getResources(), 5);
        int paddingLeftRight = SystemUtils.convertDpToPx(context.getResources(), 15);
        builder.setView(editText, paddingLeftRight, paddingTopBottom, paddingLeftRight, paddingTopBottom);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface dialog, int arg1) {
                final String playlistName = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(playlistName)) {
                    //Thêm playlist
                    int id = (int) SongProvider.getInstance(context).insertPlaylist(playlistName);
                    if (id != -1) {
                        int num = SongProvider.getInstance(context).insertSongToPlayList(id,list);
                        Common.showToast(context, "Thêm thành công "+num+" bài hát vào playlist!!!");
                    }
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

    class FolderViewHolder extends RecyclerView.ViewHolder {
        TextView tvFolderName, tvFolderPath,tvSongNum;
        ImageButton btnActionMore;

        public FolderViewHolder(View itemView) {
            super(itemView);
            tvFolderName = (TextView) itemView.findViewById(R.id.tvFolderName);
            tvFolderPath = (TextView) itemView.findViewById(R.id.tvFolderPath);
            tvSongNum = (TextView) itemView.findViewById(R.id.tvSongNum);
            btnActionMore = (ImageButton) itemView.findViewById(R.id.btnAction);
            btnActionMore.setOnClickListener(FolderAdapter.this);
        }
    }


}

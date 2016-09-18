package hienlt.app.musicplayer.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Switch;

import java.util.ArrayList;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.ui.activities.ScanActivity;
import hienlt.app.musicplayer.utils.Common;

/**
 * Created by hienl_000 on 5/4/2016.
 */
public class PlaylistDetailListSongAdapter extends LocalSongRecyclerViewAdapter {
    private int playlistID;
    public PlaylistDetailListSongAdapter(Context context, ArrayList<Song> list, int playlistID) {
        super(context, list);
        this.playlistID = playlistID;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAction:
                final int pos = (int) v.getTag();
                final Song song = listSong.get(pos);
                ArrayList<String> menuList = new ArrayList<>();
                menuList.add(context.getString(R.string.add_into_playback));
                menuList.add(context.getString(R.string.add_into_playlist));
                menuList.add(context.getString(R.string.delete_from_playlist));
                menuList.add(context.getString(R.string.set_as_ringtone));
                menuList.add(context.getString(R.string.delete_song));
                menuList.add(context.getString(R.string.song_info));

                PopupMenu popupMenu = createPopupMenu(context, v, menuList);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case 0: //Play
                                addSongToPlayback(song);
                                break;
                            case 1: //Thêm vào playlist
                                showDialogAddtoPlaylist(song);
                                break;
                            case 2: //Xóa khỏi playlist
                                showDialogRemoveFromPlaylist(song);
                                break;
                            case 3: //Set ringtone
                                setRingTone(song);
                                break;
                            case 4: //Xóa bài hát
                                showDialogDeleteSong(song);
                                break;
                            case 5: //Thông tin bài hát
                                showDialogviewSongInfo(song);
                                break;

                        }
                        return true;
                    }
                });
                break;
        }
    }

    private void showDialogRemoveFromPlaylist(final Song song) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xóa bài hát khỏi playlist");
        builder.setMessage("Xóa bỏ '" + song.getTitle() + "' khỏi playlist ?");
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SongProvider.getInstance(context).removeSongFromPlaylist(playlistID,song.getId())) {
                    listSong.remove(song);
                    notifyDataSetChanged();
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
}

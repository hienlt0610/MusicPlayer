package hienlt.app.musicplayer.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.interfaces.IMusicServiceConnection;
import hienlt.app.musicplayer.media.MusicService;
import hienlt.app.musicplayer.media.MusicServiceConnection;
import hienlt.app.musicplayer.models.Playlist;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.provider.MusicDBLoader;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.ui.activities.ScanActivity;
import hienlt.app.musicplayer.utils.App;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.StringUtils;
import hienlt.app.musicplayer.utils.SystemUtils;

/**
 * Created by hienl_000 on 4/8/2016.
 */
public class LocalSongRecyclerViewAdapter extends RecyclerView.Adapter<LocalSongRecyclerViewAdapter.MyViewHolder> implements View.OnClickListener, OnLikeListener, Filterable,FastScrollRecyclerView.SectionedAdapter {

    protected ArrayList<Song> listSong;
    protected Context context;
    public static final String ACTION_LIKE_STATE_CHANGE = "hienlt.app.musicplayer.ACTION_LIKE_STATE_CHANGE";
    private MusicServiceConnection connection;
    String searchString = "";

    @Override
    public void liked(LikeButton likeButton) {
        int position = (int) likeButton.getTag();
        String songID = listSong.get(position).getId();
        SongProvider.getInstance(context).insertLike(songID);
        //Send broadcast like state change
        Intent intent = new Intent(ACTION_LIKE_STATE_CHANGE);
        intent.putExtra(MusicDBLoader.SONG_COLUMN_ID, songID);
        intent.putExtra(MusicDBLoader.IS_LIKE, true);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public void unLiked(LikeButton likeButton) {
        int position = (int) likeButton.getTag();
        String songID = listSong.get(position).getId();
        SongProvider.getInstance(context).removeLike(songID);
        //Send broadcast like state change
        Intent intent = new Intent(ACTION_LIKE_STATE_CHANGE);
        intent.putExtra(MusicDBLoader.SONG_COLUMN_ID, songID);
        intent.putExtra(MusicDBLoader.IS_LIKE, false);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return listSong.get(position).getTitle().substring(0,1);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvSong, tvArtist, tvBitrate;
        LinearLayout btnAction;
        LikeButton btnLike;
        View view;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvSong = (TextView) itemView.findViewById(R.id.tvSong);
            tvArtist = (TextView) itemView.findViewById(R.id.tvArtist);
            tvBitrate = (TextView) itemView.findViewById(R.id.tvBitrate);
            btnAction = (LinearLayout) itemView.findViewById(R.id.btnAction);
            btnLike = (LikeButton) itemView.findViewById(R.id.btnLike);
            btnAction.setOnClickListener(LocalSongRecyclerViewAdapter.this);
            btnLike.setOnLikeListener(LocalSongRecyclerViewAdapter.this);
            tvSong.setTextColor(ContextCompat.getColor(App.getAppContext(), R.color.text_while));
            tvArtist.setTextColor(ContextCompat.getColor(App.getAppContext(), R.color.text_while));
            view = itemView;
        }
    }

    public LocalSongRecyclerViewAdapter(Context context, ArrayList<Song> list) {
        this.context = context;
        this.listSong = list;
        connection = new MusicServiceConnection(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_local_song, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Song song = listSong.get(position);
        File file = new File(song.getLocalDataSource());
        if (!file.exists()) {
            holder.tvSong.setText("Lỗi không tìm thấy bài hát");
            holder.tvArtist.setText("");
        } else {
            holder.view.setClickable(true);
            holder.tvSong.setText(song.getTitle());
            holder.tvArtist.setText(song.getArtist());
        }
        if (song.getBitRate() == 320) {
            holder.tvBitrate.setText("320");
            holder.tvBitrate.setVisibility(View.VISIBLE);
        } else
            holder.tvBitrate.setVisibility(View.GONE);

        String titleRemoveAccent = StringUtils.removeAccent(song.getTitle()).toLowerCase();
        String artistRemoveAccent = StringUtils.removeAccent(song.getArtist()).toLowerCase();
        String stringSeachRemoveAccent = StringUtils.removeAccent(searchString).toLowerCase();
        if (titleRemoveAccent.contains(stringSeachRemoveAccent)) {
            int startPos = titleRemoveAccent.indexOf(stringSeachRemoveAccent);
            int endPos = startPos + stringSeachRemoveAccent.length();
            Spannable spanText = new SpannableString(song.getTitle());
            spanText.setSpan(new ForegroundColorSpan(Color.YELLOW), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.tvSong.setText(spanText, TextView.BufferType.SPANNABLE);
        }
        if (artistRemoveAccent.contains(stringSeachRemoveAccent)) {
            int startPos = artistRemoveAccent.indexOf(stringSeachRemoveAccent);
            int endPos = startPos + stringSeachRemoveAccent.length();
            Spannable spanText = new SpannableString(song.getArtist());
            spanText.setSpan(new ForegroundColorSpan(Color.YELLOW), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.tvArtist.setText(spanText, TextView.BufferType.SPANNABLE);
        }

        holder.btnLike.setLiked(song.isLike());


        holder.btnAction.setTag(position);
        holder.btnLike.setTag(position);
    }

    @Override
    public int getItemCount() {
        return listSong.size();
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
                menuList.add(context.getString(R.string.set_as_ringtone));
                menuList.add(context.getString(R.string.delete_song));
                menuList.add(context.getString(R.string.song_info));

                PopupMenu popupMenu = createPopupMenu(context, v, menuList);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            //Thêm vào danh sách phát
                            case 0:
                                addSongToPlayback(song);
                                break;
                            //Thêm vào playlist
                            case 1:
                                showDialogAddtoPlaylist(song);
                                break;
                            //Đặt làm nhạc chuông
                            case 2:
                                setRingTone(song);
                                break;
                            //Xóa nhạc
                            case 3:
                                showDialogDeleteSong(song);
                                break;
                            //Thông tin bài hát
                            case 4:
                                showDialogviewSongInfo(song);
                                break;
                        }
                        return true;
                    }
                });
                break;
        }

    }

    protected void setRingTone(Song song) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, song.getLocalDataSource());
        values.put(MediaStore.MediaColumns.TITLE, song.getTitle());
        values.put(MediaStore.MediaColumns.SIZE, song.getSize());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.ARTIST, song.getArtist());
        values.put(MediaStore.Audio.Media.DURATION, song.getDuration());
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        //Insert it into the database
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(song.getLocalDataSource());
        Uri newUri = context.getContentResolver().insert(uri, values);

        try {
            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
        } catch (Exception e) {

        }

    }

    /**
     * Hiển thị dialog Xóa bài hát
     *
     * @param song
     */
    protected void showDialogDeleteSong(final Song song) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xóa bỏ");
        builder.setMessage("Xóa '" + song.getTitle() + "' ?");
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SongProvider.getInstance(context).removeSong(song.getId())) {
                    listSong.remove(song);
                    notifyDataSetChanged();
                    Intent intent = new Intent(ScanActivity.ACTION_SCAN_SUCCESS);
                    intent.putExtra("song_nums", listSong.size());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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

    /**
     * Hiển thị dialog thêm bài nhạc vào playlist
     *
     * @param song
     */
    protected void showDialogAddtoPlaylist(final Song song) {
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
                    showDiaLogCreateAddSongPlaylist(song);
                    return;
                }
                which -= ((AlertDialog) dialog).getListView().getHeaderViewsCount();
                boolean isInsert = SongProvider.getInstance(context).insertSongToPlaylist(pls.get(which).getId(), song.getId());
                if (isInsert)
                    Common.showToast(context, "Thêm thành công!!!");
                else
                    Common.showToast(context, "Bài hát đã được thêm trước đó.");
            }
        });
        AlertDialog dialog = builder.create();
        View header = LayoutInflater.from(context).inflate(R.layout.header_add_playlist, null);
        dialog.getListView().addHeaderView(header);
        dialog.show();
    }

    protected void addSongToPlayback(final Song song) {
        Intent intent = new Intent(context, MusicService.class);
        connection.connect(intent, new IMusicServiceConnection() {
            @Override
            public void onConnected(MusicService service) {
                service.addSong(song);
                if (service.getState() == MusicService.MusicState.Stop)
                    service.playSong();
            }
        });
    }

    /**
     * Hiển thị dialog Thông tin bài hát
     *
     * @param song
     */
    protected void showDialogviewSongInfo(Song song) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Thông tin bài hát");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_song_info, null);
        TextView tvSong = (TextView) view.findViewById(R.id.tvSong);
        TextView tvArtist = (TextView) view.findViewById(R.id.tvArtist);
        TextView tvAlbum = (TextView) view.findViewById(R.id.tvAlbum);
        TextView tvGenre = (TextView) view.findViewById(R.id.tvGenre);
        TextView tvDuration = (TextView) view.findViewById(R.id.tvDuration);
        TextView tvFileSize = (TextView) view.findViewById(R.id.tvFileSize);
        TextView tvYear = (TextView) view.findViewById(R.id.tvYear);
        TextView tvBitrate = (TextView) view.findViewById(R.id.tvBitrate);
        TextView tvFileAbsolute = (TextView) view.findViewById(R.id.tvFileAbsolute);
        TextView tvFolder = (TextView) view.findViewById(R.id.tvFolder);

        tvSong.setText(song.getTitle());
        tvArtist.setText(song.getArtist());
        tvAlbum.setText(song.getAlbum());
        tvGenre.setText(song.getGenre());
        tvDuration.setText(Common.miliSecondToString(song.getDuration()));
        tvFileSize.setText(song.getSizeDisplay());
        if (song.getYear() != 0)
            tvYear.setText(song.getYear() + "");
        else
            tvYear.setVisibility(View.GONE);
        tvBitrate.setText(song.getBitRate() + " kbps");
        tvFileAbsolute.setText(song.getLocalDataSource());
        tvFolder.setText(song.getParentFolder());

        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Khởi tạo popup menu
     *
     * @param context
     * @param view
     * @param menuList
     * @return
     */
    protected PopupMenu createPopupMenu(Context context, View view, ArrayList<String> menuList) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        int i = 0;
        for (String menu : menuList) {
            popupMenu.getMenu().add(0, i, i, menu);
            i++;
        }
        return popupMenu;
    }

    /**
     * Hiển thị dialog thêm playlist
     */
    private void showDiaLogCreateAddSongPlaylist(final Song song) {
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
                        boolean boolInsert = SongProvider.getInstance(context).insertSongToPlaylist(id, song.getId());
                        if (boolInsert) {
                            Common.showToast(context, "Thêm thành công");
                        }
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

    public void replaceList(ArrayList<Song> songArraylist) {
        if (songArraylist == null) return;
        this.listSong.clear();
        this.listSong.addAll(songArraylist);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults result = new FilterResults();
                String filterString = constraint.toString().toLowerCase();
                result.values = SongProvider.getInstance(context).getListSongByKey(filterString);
                return result;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                searchString = constraint.toString();
                replaceList((ArrayList<Song>) results.values);
            }
        };
    }

    public List<Song> getList() {
        return this.listSong;
    }
}

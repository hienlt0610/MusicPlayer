package hienlt.app.musicplayer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.asynctasks.ImageCacheAsyncTask;
import hienlt.app.musicplayer.models.Album;
import hienlt.app.musicplayer.utils.App;
import hienlt.app.musicplayer.utils.CacheManager;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.DiskLruImageCache;

/**
 * Created by hienl_000 on 4/26/2016.
 */
public class AlbumRecyclerViewAdapter extends RecyclerView.Adapter<AlbumRecyclerViewAdapter.MyViewHolder> implements View.OnClickListener {

    private Context context;
    ArrayList<Album> list;
    DiskLruImageCache cache;

    public AlbumRecyclerViewAdapter(Context context, ArrayList<Album> albums) {
        this.context = context;
        this.list = albums;
        cache = CacheManager.getInstance().getAlbumCache();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAlbumArt;
        TextView tvAlbum, tvArtist, tvSongNums;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgAlbumArt = (ImageView) itemView.findViewById(R.id.imgAlbumArt);
            tvAlbum = (TextView) itemView.findViewById(R.id.tvAlbum);
            tvArtist = (TextView) itemView.findViewById(R.id.tvArtist);
            tvSongNums = (TextView) itemView.findViewById(R.id.tvSongNums);
        }
    }

    @Override
    public AlbumRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_album, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Album album = list.get(position);
        holder.tvAlbum.setText(album.getName());
        holder.tvArtist.setText(album.getArtist());
        holder.tvSongNums.setText("(" + album.getSongNum() + " bài hát)");

        if(cache!=null){
            Bitmap bitmap = cache.getBitmap(album.getName());
            if(bitmap!=null)
                holder.imgAlbumArt.setImageBitmap(bitmap);
            else
                holder.imgAlbumArt.setImageResource(R.drawable.adele);
    }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {

    }

    public ArrayList<Album> getList(){
        return this.list;
    }
}

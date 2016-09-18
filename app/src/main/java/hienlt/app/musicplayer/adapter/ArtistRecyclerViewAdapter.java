package hienlt.app.musicplayer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.models.Album;
import hienlt.app.musicplayer.models.Artist;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.DiskLruImageCache;

/**
 * Created by hienl_000 on 4/26/2016.
 */
public class ArtistRecyclerViewAdapter extends RecyclerView.Adapter<ArtistRecyclerViewAdapter.MyViewHolder> implements View.OnClickListener {

    private Context context;
    ArrayList<Artist> list;
    DiskLruImageCache cache;

    public ArtistRecyclerViewAdapter(Context context, ArrayList<Artist> list) {
        this.context = context;
        this.list = list;
        cache = DiskLruImageCache.newInstance(Common.ARTIST_CACHE_FOLDER);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAlbumArt;
        TextView tvArtist, tvSongNums;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgAlbumArt = (ImageView) itemView.findViewById(R.id.imgAlbumArt);
            tvArtist = (TextView) itemView.findViewById(R.id.tvArtist);
            tvSongNums = (TextView) itemView.findViewById(R.id.tvSongNums);
        }
    }

    @Override
    public ArtistRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_artist, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Artist artist = list.get(position);
        holder.tvArtist.setText(artist.getArtist());
        holder.tvSongNums.setText("(" + artist.getSongNum() + " bài hát)");

        if (cache != null) {
            Bitmap bitmap = cache.getBitmap(artist.getArtist());
            if (bitmap != null)
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

    public void replaceList(ArrayList<Artist> artists){
        if(artists!=null){
            this.list.clear();
            this.list.addAll(artists);
            notifyDataSetChanged();
        }
    }

    public ArrayList<Artist> getList(){
        return this.list;
    }
}

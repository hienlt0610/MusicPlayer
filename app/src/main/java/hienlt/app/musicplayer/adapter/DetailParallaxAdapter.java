package hienlt.app.musicplayer.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.poliveira.parallaxrecyclerview.ParallaxRecyclerAdapter;

import java.io.File;
import java.util.List;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.utils.App;

/**
 * Created by hienl_000 on 4/26/2016.
 */
public class DetailParallaxAdapter extends ParallaxRecyclerAdapter<Song> implements View.OnClickListener, OnLikeListener {
    List<Song> list;


    public DetailParallaxAdapter(List<Song> data) {
        super(data);
        this.list = data;
    }

    @Override
    public void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, ParallaxRecyclerAdapter<Song> parallaxRecyclerAdapter, int position) {
        AlbumDetailViewHolder holder = (AlbumDetailViewHolder) viewHolder;
        Song song = list.get(position);
        File file = new File(song.getLocalDataSource());
        if(!file.exists()){
            holder.tvSong.setText("Lỗi không tìm thấy bài hát");
            holder.tvArtist.setText("");
        }else{
            holder.tvSong.setText(song.getTitle());
            holder.tvArtist.setText(song.getArtist());
        }
        if (song.getBitRate() == 320) {
            holder.tvBitrate.setText("320");
            holder.tvBitrate.setVisibility(View.VISIBLE);
        } else
            holder.tvBitrate.setVisibility(View.GONE);

        holder.btnLike.setLiked(song.isLike());

        holder.btnAction.setTag(position);
        holder.btnLike.setTag(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolderImpl(ViewGroup viewGroup, ParallaxRecyclerAdapter<Song> parallaxRecyclerAdapter, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_local_song, viewGroup, false);
        return new AlbumDetailViewHolder(v);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void liked(LikeButton likeButton) {
        int position = (int) likeButton.getTag();
        SongProvider.getInstance(likeButton.getContext()).insertLike(list.get(position).getId());
    }

    @Override
    public void unLiked(LikeButton likeButton) {
        int position = (int) likeButton.getTag();
        SongProvider.getInstance(likeButton.getContext()).removeLike(list.get(position).getId());
    }

    public class AlbumDetailViewHolder extends RecyclerView.ViewHolder {

        TextView tvSong, tvArtist, tvBitrate;
        LinearLayout btnAction;
        LikeButton btnLike;
        View view;

        public AlbumDetailViewHolder(View itemView) {
            super(itemView);
            tvSong = (TextView) itemView.findViewById(R.id.tvSong);
            tvArtist = (TextView) itemView.findViewById(R.id.tvArtist);
            tvBitrate = (TextView) itemView.findViewById(R.id.tvBitrate);
            btnAction = (LinearLayout) itemView.findViewById(R.id.btnAction);
            btnLike = (LikeButton) itemView.findViewById(R.id.btnLike);

            btnAction.setOnClickListener(DetailParallaxAdapter.this);
            btnLike.setOnLikeListener(DetailParallaxAdapter.this);
            tvSong.setTextColor(ContextCompat.getColor(App.getAppContext(), R.color.text_while));
            tvArtist.setTextColor(ContextCompat.getColor(App.getAppContext(),R.color.text_while));

        }
    }

    @Override
    public int getItemCountImpl(ParallaxRecyclerAdapter<Song> parallaxRecyclerAdapter) {
        return list.size();
    }
}

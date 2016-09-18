package hienlt.app.musicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.models.BackgroundImage;
import hienlt.app.musicplayer.utils.Common;

/**
 * Created by hienl_000 on 5/7/2016.
 */
public class BackgroundGridAdapter extends RecyclerView.Adapter<BackgroundGridAdapter.BackgroundGridViewHolder> {

    private Context context;
    private ArrayList<BackgroundImage> images;
    private BackgroundImage selectedImage;

    public BackgroundGridAdapter(Context context, ArrayList<BackgroundImage> images) {
        this.context = context;
        this.images = images;
    }

    public void setSelectedImage(BackgroundImage image){
        this.selectedImage = image;
        notifyDataSetChanged();
    }

    @Override
    public BackgroundGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_background,parent,false);
        return new BackgroundGridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BackgroundGridViewHolder holder, int position) {
        BackgroundImage image = images.get(position);
        holder.imgBackground.setImageBitmap(image.getThumb());
        holder.overlay.setVisibility(View.GONE);
        holder.imgSelected.setVisibility(View.GONE);
        if(selectedImage!=null){
            if(selectedImage.isDefaultBackground()){
                if(image.getId() == selectedImage.getId()) {
                    holder.overlay.setVisibility(View.VISIBLE);
                    holder.imgSelected.setVisibility(View.VISIBLE);
                }else{
                    holder.overlay.setVisibility(View.GONE);
                    holder.imgSelected.setVisibility(View.GONE);
                }
            }else{
                if(image.getPath() == null) return;
                if(image.getPath().equals(selectedImage.getPath())) {
                    holder.overlay.setVisibility(View.VISIBLE);
                    holder.imgSelected.setVisibility(View.VISIBLE);
                }else{
                    holder.overlay.setVisibility(View.GONE);
                    holder.imgSelected.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class BackgroundGridViewHolder extends RecyclerView.ViewHolder{
        ImageView imgBackground, imgSelected;
        View overlay;
        public BackgroundGridViewHolder(View itemView) {
            super(itemView);
            imgBackground = (ImageView) itemView.findViewById(R.id.imgBackground);
            imgSelected = (ImageView) itemView.findViewById(R.id.imgSelected);
            overlay = itemView.findViewById(R.id.overlay);
        }
    }
}

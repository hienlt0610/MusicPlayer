package hienlt.app.musicplayer.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.models.Song;

/**
 * Created by hienl_000 on 4/13/2016.
 */
public class FragmentPlayer extends Fragment {

    ImageView imgAlbumArt;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player,container,false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgAlbumArt = (ImageView) view.findViewById(R.id.imgAlbumArt);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void updateTrackInfo(Song song){
        if(song.getAlbumPicture() !=null){
            imgAlbumArt.setImageBitmap(song.getAlbumPicture());
        }else{
            imgAlbumArt.setImageResource(R.drawable.adele);
        }
    }
}

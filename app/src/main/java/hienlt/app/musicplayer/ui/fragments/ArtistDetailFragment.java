package hienlt.app.musicplayer.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.poliveira.parallaxrecyclerview.ParallaxRecyclerAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.adapter.DetailParallaxAdapter;
import hienlt.app.musicplayer.core.HLBaseFragment;
import hienlt.app.musicplayer.interfaces.IMusicServiceConnection;
import hienlt.app.musicplayer.media.MusicService;
import hienlt.app.musicplayer.media.MusicServiceConnection;
import hienlt.app.musicplayer.models.Song;
import hienlt.app.musicplayer.provider.SongProvider;
import hienlt.app.musicplayer.ui.activities.PlaybackActivity;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.DiskLruImageCache;

/**
 * Created by hienl_000 on 4/26/2016.
 */
public class ArtistDetailFragment extends HLBaseFragment implements ParallaxRecyclerAdapter.OnClickEvent {
    public static final String ARTIST_NAME = "artist_name";
    RecyclerView recyclerView;
    DetailParallaxAdapter adapter;
    ArrayList<Song> list;
    ImageView imgAlbumArt;
    private MusicServiceConnection mServiceConnection;
    private DiskLruImageCache cache;

    @Override
    protected int getLayout() {
        return R.layout.fragment_recyclerview;
    }

    public static ArtistDetailFragment getInstance(String artistName) {
        ArtistDetailFragment albumDetailFragment = new ArtistDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARTIST_NAME, artistName);
        albumDetailFragment.setArguments(bundle);
        return albumDetailFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String artistName = getArguments().getString(ARTIST_NAME);

        if (artistName == null) getActivity().getSupportFragmentManager().popBackStack();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        View header = getActivity().getLayoutInflater().inflate(R.layout.album_detail_header, recyclerView, false);
        imgAlbumArt = (ImageView) header.findViewById(R.id.imgArt);


        list = SongProvider.getInstance(getActivity()).getListSongByArtist(artistName);
        adapter = new DetailParallaxAdapter(list);

        cache = DiskLruImageCache.newInstance(Common.ALBUM_CACHE_FOLDER);
        Bitmap bitmap = cache.getBitmap(artistName.trim());
        if (bitmap != null)
            imgAlbumArt.setImageBitmap(bitmap);
        mServiceConnection = new MusicServiceConnection(getActivity());

        adapter.setParallaxHeader(header, recyclerView);
        adapter.setData(list);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickEvent(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            popCurrentFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String getActionbarName() {
        return "Danh sách album";
    }

    @Override
    public void onClick(View view, final int position) {
        Song song = list.get(position);
        File file = new File(song.getLocalDataSource());
        if (!file.exists()) {
            Common.showToast(getActivity(), "Bài hát này đã bị xóa, vui lòng cập nhật lại");
            return;
        }
        Intent iSelectSongPlay = new Intent(getActivity(), MusicService.class);
        mServiceConnection.connect(iSelectSongPlay, new IMusicServiceConnection() {
            @Override
            public void onConnected(MusicService service) {
                if (service.getCurrentSong() == null || !service.getCurrentSong().getId().equals(list.get(position).getId())) {
                    service.setSongPosition(position);
                    service.setPlayListSong(list);
                    service.setMediaType(MusicService.MediaType.Local);
                    service.playSong();
                }
                Intent intent = new Intent(getActivity(), PlaybackActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.no_change);
            }
        });
    }
}

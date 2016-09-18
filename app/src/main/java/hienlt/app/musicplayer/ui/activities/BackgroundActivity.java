package hienlt.app.musicplayer.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.adapter.BackgroundGridAdapter;
import hienlt.app.musicplayer.core.HLBaseActivity;
import hienlt.app.musicplayer.models.BackgroundImage;
import hienlt.app.musicplayer.utils.Common;
import hienlt.app.musicplayer.utils.GaussianBlur;
import hienlt.app.musicplayer.utils.ImageUtils;
import hienlt.app.musicplayer.utils.ItemClickSupport;
import hienlt.app.musicplayer.utils.Settings;
import hienlt.app.musicplayer.utils.SystemUtils;

public class BackgroundActivity extends HLBaseActivity implements ItemClickSupport.OnItemClickListener {

    RecyclerView recyclerView;
    BackgroundGridAdapter adapter;
    ArrayList<BackgroundImage> images;
    ImageView imgBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        imgBackground = (ImageView) findViewById(R.id.imgBackground);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        setBackground(false);
        initListBackground();

        //Get current background
        boolean isDefaultBkg = Settings.getInstance().get(Common.DEFAULT_BACKGROUND, false);
        BackgroundImage currBackground = new BackgroundImage();
        if(isDefaultBkg){
            int bkgID = Settings.getInstance().get(Common.BACKGROUND_ID,0);
            currBackground.setId(bkgID);
        }else{
            String path = Settings.getInstance().get(Common.MY_BACKGROUND, null);
            currBackground.setPath(path);
        }

        adapter = new BackgroundGridAdapter(this, images);
        adapter.setSelectedImage(currBackground);
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(this);
    }

    private void initListBackground() {
        images = new ArrayList<>();
        images.add(new BackgroundImage(R.drawable.bg1, BitmapFactory.decodeResource(getResources(), R.drawable.bg1)));
        images.add(new BackgroundImage(R.drawable.bg2, BitmapFactory.decodeResource(getResources(), R.drawable.bg2)));
        images.add(new BackgroundImage(R.drawable.bg3, BitmapFactory.decodeResource(getResources(), R.drawable.bg3)));
        images.add(new BackgroundImage(R.drawable.bg4, BitmapFactory.decodeResource(getResources(), R.drawable.bg4)));
        images.add(new BackgroundImage(R.drawable.bg5, BitmapFactory.decodeResource(getResources(), R.drawable.bg5)));
        images.add(new BackgroundImage(R.drawable.bg6, BitmapFactory.decodeResource(getResources(), R.drawable.bg6)));
        images.add(new BackgroundImage(R.drawable.bg7, BitmapFactory.decodeResource(getResources(), R.drawable.bg7)));
        images.add(new BackgroundImage(R.drawable.bg8, BitmapFactory.decodeResource(getResources(), R.drawable.bg8)));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_background;
    }

    public void setBackground(boolean isAnimation) {
        Bitmap bitmap = null;
        boolean isDefaultBkg = Settings.getInstance().get(Common.DEFAULT_BACKGROUND, false);
        if (isDefaultBkg) {
            int bkgID = Settings.getInstance().get(Common.BACKGROUND_ID, 0);
            bitmap = BitmapFactory.decodeResource(getResources(), bkgID);
        } else {
            String path = Settings.getInstance().get(Common.MY_BACKGROUND, null);
            bitmap = BitmapFactory.decodeFile(path);
        }
        if (bitmap == null) return;
        bitmap = GaussianBlur.getInstance(this).setRadius(5).render(bitmap, true);
        if (isAnimation)
            ImageUtils.ImageViewAnimatedChange(this, imgBackground, bitmap);
        else
            imgBackground.setImageBitmap(bitmap);
    }


    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        BackgroundImage image = images.get(position);

        //Hình nền mặc định của hệ thống
        if (image.isDefaultBackground()) {
            Settings.getInstance().put(Common.DEFAULT_BACKGROUND, true);
            Settings.getInstance().put(Common.BACKGROUND_ID, image.getId());
            Intent intent = new Intent(Common.ACTION_BACKGROUND_CHANGED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            Settings.getInstance().put(Common.DEFAULT_BACKGROUND, false);
        }
        setBackground(true);
        adapter.setSelectedImage(image);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
        }
        return true;
    }
}

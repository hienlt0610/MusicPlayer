package hienlt.app.musicplayer.asynctasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.utils.DiskLruImageCache;

/**
 * Created by hienl_000 on 5/3/2016.
 */
public class ImageCacheAsyncTask extends AsyncTask<Void,Void, Bitmap> {

    ImageView imageView;
    DiskLruImageCache cache;
    String key;

    public ImageCacheAsyncTask(ImageView imageView, DiskLruImageCache cache, String key){
        this.imageView = imageView;
        this.cache = cache;
        this.key = key;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        if(cache!=null)
            return cache.getBitmap(key);
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(bitmap!=null)
            imageView.setImageBitmap(bitmap);
        else
            imageView.setImageResource(R.drawable.adele);
    }
}

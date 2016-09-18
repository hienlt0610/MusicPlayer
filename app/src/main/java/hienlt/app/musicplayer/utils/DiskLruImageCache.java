/**
 * ownCloud Android client application
 * <p/>
 * Copyright (C) 2015 ownCloud Inc.
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2,
 * as published by the Free Software Foundation.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package hienlt.app.musicplayer.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DiskLruImageCache {

    private DiskLruCache mDiskCache;
    private CompressFormat mCompressFormat;
    private int mCompressQuality;
    private static final int CACHE_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final int CACHESIZE = 50 * 1024 * 1024;
    private static final String TAG = DiskLruImageCache.class.getSimpleName();

    //    public DiskLruImageCache( Context context,String uniqueName, int diskCacheSize,
    public DiskLruImageCache(File diskCacheDir, int diskCacheSize, CompressFormat compressFormat, int quality) {

        try {
            mDiskCache = DiskLruCache.open(diskCacheDir, CACHE_VERSION, VALUE_COUNT, diskCacheSize);
            mCompressFormat = compressFormat;
            mCompressQuality = quality;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DiskLruImageCache newInstance(String subDir){
        File file = new File(Common.getRootCacheDir() + File.separator + subDir);
        if (!file.exists())
            file.mkdirs();
        return new DiskLruImageCache(file, CACHESIZE, CompressFormat.JPEG, 100);
    }

    private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor)
            throws IOException, FileNotFoundException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(editor.newOutputStream(0), IO_BUFFER_SIZE);
            return bitmap.compress(mCompressFormat, mCompressQuality, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public void put(String key, Bitmap data) {
        DiskLruCache.Editor editor = null;
        String validKey = convertToValidKey(key);
        try {
            editor = mDiskCache.edit(validKey);
            if (editor == null) {
                return;
            }

            if (writeBitmapToFile(data, editor)) {
                mDiskCache.flush();
                editor.commit();
                Common.showLog(TAG,"image put on disk cache " + validKey);
            } else {
                editor.abort();
                Common.showLog(TAG,"ERROR on: image put on disk cache " + validKey);
            }
        } catch (IOException e) {
            Common.showLog(TAG,"ERROR on: image put on disk cache " + validKey);
            try {
                if (editor != null) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }
        }

    }

    public Bitmap getBitmap(String key) {

        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        String validKey = convertToValidKey(key);
        try {

            snapshot = mDiskCache.get(validKey);
            if (snapshot == null) {
                return null;
            }
            final InputStream in = snapshot.getInputStream(0);
            if (in != null) {
                final BufferedInputStream buffIn =
                        new BufferedInputStream(in, IO_BUFFER_SIZE);
                bitmap = BitmapFactory.decodeStream(buffIn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }

        Common.showLog(TAG,"image read from disk " + validKey);

        return bitmap;

    }

    public boolean containsKey(String key) {

        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;
        String validKey = convertToValidKey(key);
        try {
            snapshot = mDiskCache.get(validKey);
            contained = snapshot != null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }

        return contained;

    }

    public void clearCache() {
        Common.showLog(TAG,"disk cache CLEARED");
        try {
            mDiskCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getCacheFolder() {
        return mDiskCache.getDirectory();
    }

    private String convertToValidKey(String key) {
        return Integer.toString(key.hashCode());
    }

    /**
     * Remove passed key from cache
     * @param key
     */
    public void removeKey(String key) {
        String validKey = convertToValidKey(key);
        try {
            mDiskCache.remove(validKey);
            Common.showLog(TAG,"removeKey from cache: " + validKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
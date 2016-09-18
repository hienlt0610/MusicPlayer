package hienlt.app.musicplayer.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by hienl_000 on 5/9/2016.
 */
public class DiskLruFileCache {
    private DiskLruCache mDiskCache;
    private static final int CACHE_VERSION = 1;
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final int CACHESIZE = 50 * 1024 * 1024;
    private static final int VALUE_COUNT = 1;
    private static final String TAG = DiskLruFileCache.class.getSimpleName();

    public DiskLruFileCache(File diskCacheDir, int diskCacheSize) {

        try {
            mDiskCache = DiskLruCache.open(diskCacheDir, CACHE_VERSION, VALUE_COUNT, diskCacheSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DiskLruFileCache newInstance(String subDir) {
        File file = new File(Common.getRootCacheDir() + File.separator + subDir);
        if (!file.exists())
            file.mkdirs();
        return new DiskLruFileCache(file, CACHESIZE);
    }

    private boolean writeToFile(String data, DiskLruCache.Editor editor)
            throws IOException, FileNotFoundException {
        boolean isSuccess = false;
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(editor.newOutputStream(0),"UTF-8"));
            writer.write(data);
            isSuccess = true;
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
        return isSuccess;
    }

    public void put(String key, String data) {
        DiskLruCache.Editor editor = null;
        String validKey = convertToValidKey(key);
        try {
            editor = mDiskCache.edit(validKey);
            if (editor == null) {
                return;
            }

            if (writeToFile(data, editor)) {
                mDiskCache.flush();
                editor.commit();
                Common.showLog(TAG,"file put on disk cache " + validKey);
            } else {
                editor.abort();
                Common.showLog(TAG,"ERROR on: file put on disk cache " + validKey);
            }
        } catch (IOException e) {
            Common.showLog(TAG,"ERROR on: file put on disk cache " + validKey);
            try {
                if (editor != null) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public String getFileContent(String key) {
        String data = null;
        DiskLruCache.Snapshot snapshot = null;
        String validKey = convertToValidKey(key);
        BufferedReader in = null;
        StringBuilder builder = new StringBuilder();
        try {
            snapshot = mDiskCache.get(validKey);
            if (snapshot == null) return null;
            final InputStream is = snapshot.getInputStream(0);
            if (is != null) {
                in = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                String str;
                boolean isFirst = true;
                while ((str = in.readLine()) != null) {
                    if (isFirst)
                        isFirst = false;
                    else
                        builder.append('\n');
                    builder.append(str);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null)
                snapshot.close();
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        Common.showLog(TAG,"file read from disk " + validKey);

        return builder.toString();
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

    private String convertToValidKey(String key) {
        return Integer.toString(key.hashCode());
    }
}

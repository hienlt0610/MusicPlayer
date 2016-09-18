package hienlt.app.musicplayer.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Size;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.images.Artwork;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import hienlt.app.musicplayer.R;

/**
 * Created by hienl_000 on 4/20/2016.
 */
public class ImageUtils {
    /**
     * Convert bitmap to Drawable
     *
     * @param resources
     * @param bitmap
     * @return
     */
    public static Drawable convert2Drawable(Resources resources, Bitmap bitmap) {
        return new BitmapDrawable(resources, bitmap);
    }

    public static Bitmap doBrightness(Bitmap src, int value) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                // increase/decrease each channel
                R += value;
                if (R > 255) {
                    R = 255;
                } else if (R < 0) {
                    R = 0;
                }

                G += value;
                if (G > 255) {
                    G = 255;
                } else if (G < 0) {
                    G = 0;
                }

                B += value;
                if (B > 255) {
                    B = 255;
                } else if (B < 0) {
                    B = 0;
                }

                // apply new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }

    /**
     * Get bitmap from view
     *
     * @param v
     * @return
     */
    public static Bitmap getScreenshot(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

    /**
     * Drawable convert to bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable != null) {
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                    : Bitmap.Config.RGB_565;
            Bitmap bitmap = Bitmap.createBitmap(w, h, config);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, w, h);
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * Bitmap convert to drawable
     *
     * @param bitmap
     * @return
     */
    public static Drawable bitmapToDrawable(Resources resources,Bitmap bitmap) {
        if (bitmap != null) {
            return new BitmapDrawable(resources,bitmap);
        } else {
            return null;
        }
    }

    /**
     * Stream convert to bitmap
     *
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static Bitmap inputStreamToBitmap(InputStream inputStream)
            throws Exception {
        if (inputStream != null) {
            return BitmapFactory.decodeStream(inputStream);
        } else {
            return null;
        }
    }

    /**
     * Bytes convert to bitmap
     *
     * @param byteArray
     * @return
     */
    public static Bitmap bytesToBitmap(byte[] byteArray) {
        if (byteArray != null && byteArray.length != 0) {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        } else {
            return null;
        }
    }

    /**
     * Bytes convert to drawable
     *
     * @param byteArray
     * @return
     */
    public static Drawable byteToDrawable(byte[] byteArray) {
        if (byteArray != null && byteArray.length > 0) {
            ByteArrayInputStream ins = new ByteArrayInputStream(byteArray);
            return Drawable.createFromStream(ins, null);
        } else {
            return null;
        }

    }

    /**
     * Bitmap convert to bytes
     * @param bm
     * @return
     */
    public static byte[] bitmapToBytes(Bitmap bm) {
        if (bm != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } else {
            return null;
        }

    }

    /**
     * Drawable convert to bytes
     *
     * @param drawable
     * @return
     */
    public static byte[] drawableToBytes(Drawable drawable) {
        if (drawable != null) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            byte[] bytes = bitmapToBytes(bitmap);
            ;
            return bytes;
        } else {
            return null;
        }

    }

    /**
     * Base64 convert to byte[]
     */
    public static byte[] base64ToBytes(String base64) throws IOException {
        if (base64 != null && !base64.equals("")) {
            byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
            return bytes;
        } else {
            return null;
        }
    }

    /**
     * Bytes convert to Base64
     */
    public static String bytesToBase64(byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            String base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
            return base64;
        } else {
            return null;
        }
    }

    /**
     * Get reflection image from bitmap
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getReflectionImageWithBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            final int reflectionGap = 4;
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.preScale(1, -1);
            Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w,
                    h / 2, matrix, false);

            Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),
                    Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmapWithReflection);
            canvas.drawBitmap(bitmap, 0, 0, null);
            Paint deafalutPaint = new Paint();
            canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);

            canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

            Paint paint = new Paint();
            LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                    bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
                    0x00ffffff, Shader.TileMode.CLAMP);
            paint.setShader(shader);
            // Set the Transfer mode to be porter duff and destination in
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            // Draw a rectangle using the paint with our linear gradient
            canvas.drawRect(0, h, w, bitmapWithReflection.getHeight()
                    + reflectionGap, paint);
            return bitmapWithReflection;
        } else {
            return null;
        }
    }

    /**
     * Get rounded corner image
     *
     * @param bitmap
     * @param roundPx 5 10
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        if (bitmap != null) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, w, h);
            final RectF rectF = new RectF(rect);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        } else {
            return null;
        }
    }

    /**
     * Resize the image
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        if (bitmap != null) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float scaleWidth = ((float) width / w);
            float scaleHeight = ((float) height / h);
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
            return newbmp;
        } else {
            return null;
        }
    }

    /**
     * Resize the drawable
     *
     * @param drawable
     * @param w
     * @param h
     * @return
     */
    public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
        if (drawable != null) {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap oldbmp = drawableToBitmap(drawable);
            Matrix matrix = new Matrix();
            float sx = ((float) w / width);
            float sy = ((float) h / height);
            matrix.postScale(sx, sy);
            Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                    matrix, true);
            return new BitmapDrawable(newbmp);
        } else {
            return null;
        }
    }

    /**
     * Check the SD card
     *
     * @return
     */
    public static boolean checkSDCardAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * Delete all picture files in sd card
     *
     * @param path
     */
    public static void deleteAllPictures(String path) {
        if (checkSDCardAvailable()) {
            File folder = new File(path);
            File[] files = folder.listFiles();
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
    }

    /**
     * Delete picture
     *
     * @param path     String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dirName";
     * @param fileName
     */
    public static boolean deletePicture(String path, String fileName) {
        if (checkSDCardAvailable()) {
            File file = new File(path + "/" + fileName);
            if (file == null || !file.exists() || file.isDirectory())
                return false;
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * Save image to the SD card
     *
     * @param photoBitmap
     * @param photoName   String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dirName";
     * @param path
     */
    public static boolean savePictureToSDCard(Bitmap photoBitmap, String path, String photoName) {
        boolean flag = false;
        if (checkSDCardAvailable()) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File photoFile = new File(path, photoName);

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(photoFile);

                if (photoBitmap != null) {
                    if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        flag = true;
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * Get images from SD card by path and the name of image
     * String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dirName";
     *
     * @param photoName
     * @return
     */
    public static Bitmap getPictureFromSDCard(String path, String photoName) {
        Bitmap photoBitmap = BitmapFactory.decodeFile(path + "/" + photoName);
        if (photoBitmap == null) {
            return null;
        } else {
            return photoBitmap;
        }
    }

    /**
     * Check if the picture exists in the SD card
     *
     * @param path
     * @param photoName String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dirName";
     * @return
     */
    public static boolean isPictureExistsInSDCard(String path, String photoName) {
        boolean flag = false;
        if (checkSDCardAvailable()) {
            File dir = new File(path);
            if (dir.exists()) {
                File folders = new File(path);
                File photoFile[] = folders.listFiles();
                for (int i = 0; i < photoFile.length; i++) {
                    String fileName = photoFile[i].getName();
//					System.out.println("isPictureExistsInSDCard:"+fileName);
                    if (fileName.equals(photoName)) {
                        flag = true;
                        break;
                    }
                }
            } else {
                flag = false;
            }
        } else {
            flag = false;
        }
        return flag;
    }

    public static Bitmap getBitmapFromMp3File(File file){
        MP3File mp3File = null;
        try {
            mp3File = (MP3File) AudioFileIO.read(file);
            if (mp3File.hasID3v2Tag()) {
                AbstractID3v2Tag v2Tag = mp3File.getID3v2Tag();
                Artwork artwork = v2Tag.getFirstArtwork();
                if (artwork != null) {
                    byte[] bytes = artwork.getBinaryData();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    return bitmap;
                }
            }
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Size getBackgroundSize(Context context){
        float density = context.getResources().getDisplayMetrics().density;
        if (density >= 4.0) {
            return new Size(1280,1920);
        }
        else if (density >= 3.0) {
            return new Size(960,1600);
        }
        else if (density >= 2.0) {
            return new Size(640,960);
        }
        else if (density >= 1.5) {
            return new Size(480,800);
        }
        else if (density >= 1.0) {
            return new Size(320,480);
        }
        else
        return new Size(240,320);
    }

    public static void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
//        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.no_change);
//        final Animation anim_in  = AnimationUtils.loadAnimation(c, R.anim.push_down_in);
//        anim_out.setAnimationListener(new Animation.AnimationListener()
//        {
//            @Override public void onAnimationStart(Animation animation) {}
//            @Override public void onAnimationRepeat(Animation animation) {}
//            @Override public void onAnimationEnd(Animation animation)
//            {
//                v.setImageBitmap(new_image);
//                anim_in.setAnimationListener(new Animation.AnimationListener() {
//                    @Override public void onAnimationStart(Animation animation) {}
//                    @Override public void onAnimationRepeat(Animation animation) {}
//                    @Override public void onAnimationEnd(Animation animation) {}
//                });
//                v.startAnimation(anim_in);
//            }
//        });
//        v.startAnimation(anim_out);
        v.setImageBitmap(new_image);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);
        v.startAnimation(fadeInAnimation);
    }
}

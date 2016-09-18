package hienlt.app.musicplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;


/**
 * Created by hienl_000 on 4/14/2016.
 */
public class GaussianBlur {
    private final int DEFAULT_RADIUS = 25;
    private final float DEFAULT_MAX_IMAGE_SIZE = 400;
    private static GaussianBlur instance;
    private Context context;
    private int radius;
    private float maxImageSize;

    private GaussianBlur(Context context) {
        this.context = context;
        setRadius(DEFAULT_RADIUS);
        setMaxImageSize(DEFAULT_MAX_IMAGE_SIZE);
    }

    public static GaussianBlur getInstance(Context context){
        if(instance == null)
            instance = new GaussianBlur(context);
        return instance;
    }

    public Bitmap render(Bitmap bitmap, boolean scaleDown) {
        RenderScript rs = RenderScript.create(context);

        if (scaleDown) {
            bitmap = scaleDown(bitmap);
        }

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Allocation inAlloc = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_GRAPHICS_TEXTURE);
        Allocation outAlloc = Allocation.createFromBitmap(rs, output);

        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, inAlloc.getElement()); // Element.U8_4(rs));
        script.setRadius(getRadius());
        script.setInput(inAlloc);
        script.forEach(outAlloc);
        outAlloc.copyTo(output);

        rs.destroy();

        return output;
    }

    public Bitmap scaleDown(Bitmap input) {
        float ratio = Math.min((float) getMaxImageSize() / input.getWidth(), (float) getMaxImageSize() / input.getHeight());
        int width = Math.round((float) ratio * input.getWidth());
        int height = Math.round((float) ratio * input.getHeight());

        return Bitmap.createScaledBitmap(input, width, height, true);
    }

    public int getRadius() {
        return radius;
    }

    public GaussianBlur setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public float getMaxImageSize() {
        return maxImageSize;
    }

    public GaussianBlur setMaxImageSize(float maxImageSize) {
        this.maxImageSize = maxImageSize;
        return this;
    }
}

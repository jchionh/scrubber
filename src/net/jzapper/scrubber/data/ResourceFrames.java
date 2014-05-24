package net.jzapper.scrubber.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import net.jzapper.scrubber.R;
import net.jzapper.scrubber.gfx.texture.Texture;
import net.jzapper.scrubber.gfx.utils.GfxUtils;

import javax.microedition.khronos.opengles.GL10;

/**
 * User: jchionh
 * Date: 5/24/14
 * Time: 4:45 PM
 *
 * Resource frames holds reference to example movie frame data
 *
 *
 */
public class ResourceFrames extends TextureFrames {

    private int[] resourceIds = {
            R.drawable.p00,
            R.drawable.p01,
            R.drawable.p02,
            R.drawable.p03,
            R.drawable.p04,
            R.drawable.p05,
            R.drawable.p06,
            R.drawable.p07,
            R.drawable.p08,
            R.drawable.p09,
            R.drawable.p10,
            R.drawable.p11,
            R.drawable.p12,
            R.drawable.p13,
            R.drawable.p14,
            R.drawable.p15,
            R.drawable.p16,
            R.drawable.p17,
            R.drawable.p18,
            R.drawable.p19,
            R.drawable.p20
    };

    /**
     * In the openGL context, we deoode the frame jpegs, and then call GfxUtils to
     * upload the decoded bitmap into the GPU texture.
     *
     * @param glUnused
     * @param context
     */
    public void setupTextures(GL10 glUnused, Context context) {
        for (int i = 0; i < NUM_FRAMES; ++i) {
            Bitmap bitmap = GfxUtils.decodeBitmapResource(context.getResources(), resourceIds[i]);
            Texture texture = new Texture();
            GfxUtils.loadTexture(glUnused, bitmap, texture, GLES20.GL_CLAMP_TO_EDGE, false);
            frames[i] = texture;
            bitmap.recycle();
        }
    }
}

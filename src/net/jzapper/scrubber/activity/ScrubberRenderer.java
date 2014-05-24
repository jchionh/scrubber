package net.jzapper.scrubber.activity;

import android.content.Context;
import net.jzapper.scrubber.data.ResourceFrames;
import net.jzapper.scrubber.data.TextureFrames;
import net.jzapper.scrubber.gfx.render.GLRenderer;
import net.jzapper.scrubber.gfx.render.Sprite;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

/**
 * User: jchionh
 * Date: 5/24/14
 * Time: 5:13 PM
 *
 * ScrubberRenderer both implements the OpenGL rendering
 * and loading the frames textures.
 *
 * It's simply doing this becuase it's convenient to do it here
 *
 */
public class ScrubberRenderer extends GLRenderer {

    private static final float DRAG_THRESHOLD = 1.0f;

    // imageQuad is the quad on screen that is rendererd
    // the image frames are displated by this quad.
    Sprite imageQuad = new Sprite();

    ArrayList<Sprite> sprites = new ArrayList<Sprite>();

    // current index to the frames
    private int currentFrame = 0;

    // example resource frames where all the textures will be in
    protected ResourceFrames resourceFrames = new ResourceFrames();

    // ctor
    public ScrubberRenderer(Context context) {
        super(context);
    }

    // we'll decode the jpegs, and upload them to the GPU
    // for open GL texture mapping here
    public void loadTextures(GL10 glUnused) {
        resourceFrames.setupTextures(glUnused, context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);

        // now load the textures
        loadTextures(gl);

        // setup out image quad
        imageQuad.setPos(530.0f, -600.0f, 0.0f);
        imageQuad.setTexture(resourceFrames.frames[currentFrame]);
        sprites.add(imageQuad);
        setRenderObjectList(sprites);
    }

    /**
     * We're scrubbing the video here simply coz its'
     * convenient to do it here
     * @param direction
     */
    public void scrub(float direction) {
        if (direction >= DRAG_THRESHOLD) {
            // left
            currentFrame--;
            if (currentFrame < 0) {
                currentFrame = 0;
            }
        } else if (direction <= -DRAG_THRESHOLD) {
            // right
            currentFrame++;
            if (currentFrame >= TextureFrames.NUM_FRAMES - 1) {
                currentFrame = TextureFrames.NUM_FRAMES - 1;
            }
        }

        // now, we simply set the texture to the appropriate frame
        // texture, and the image quad will display the texture instantaneously
        imageQuad.setTexture(resourceFrames.frames[currentFrame]);
    }
}

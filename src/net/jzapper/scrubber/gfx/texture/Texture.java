package net.jzapper.scrubber.gfx.texture;

import android.opengl.GLES20;

/**
 * User: jchionh
 * Date: 5/24/14
 * Time: 3:40 PM
 */
public class Texture {
    // unique identifier for the texture, could be a string, etc.
    public Object key = 0;
    // openGL generated texture name
    public int[] name = { 0 };
    // dimensions
    public int width = 0;
    public int height = 0;
    public int wrapMode = GLES20.GL_REPEAT;
    public boolean ready = false;
}

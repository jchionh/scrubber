package net.jzapper.scrubber.data;

import net.jzapper.scrubber.gfx.texture.Texture;

/**
 * User: jchionh
 * Date: 5/24/14
 * Time: 4:43 PM
 *
 * Texture frames is a container class that contains a buffer of frames.
 * Each frame holds a reference to a OpenGL texture
 *
 */
public class TextureFrames {
    public static final int NUM_FRAMES = 21;
    public Texture[] frames = new Texture[NUM_FRAMES];
}

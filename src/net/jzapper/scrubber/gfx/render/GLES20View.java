package net.jzapper.scrubber.gfx.render;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * User: jchionh
 * Date: 5/24/14
 * Time: 1:40 PM
 */
public class GLES20View extends GLSurfaceView {
    private static final int OPEN_GL_ES_VERSION = 2;

    /**
     * ctor
     * @param context
     */
    public GLES20View(Context context) {
        super(context);
        // we're using OpenGL ES 2.0
        setEGLContextClientVersion(OPEN_GL_ES_VERSION);
    }
}

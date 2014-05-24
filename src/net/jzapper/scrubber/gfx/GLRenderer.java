package net.jzapper.scrubber.gfx;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * User: jchionh
 * Date: 5/24/14
 * Time: 1:40 PM
 */
public class GLRenderer  implements GLSurfaceView.Renderer {

    protected Context context;

    public GLRenderer(Context context) {
        this.context = context;
        /*
        viewpoint = new FrustrumViewpoint();
        viewpoint.setPos(0.0f, 0.0f, 2.0f);
        viewpoint.setLookAt(0.0f, 0.0f, 0.0f);
        viewpoint.updateViewMatrix();

        // now init our shader programs
        shaderPrograms = new Tex2dShaderPrograms();
        */
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // just log it
        Log.v("_CREATE_", "GfxRenderer onSurfaceCreated()");

        // enable some GL stuff
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClearColor(0.5f, 0.0f, 0.5f, 1.0f);
        // GLES20.glEnable(GLES20.GL_CULL_FACE);

        // on the surface created, let's load up all our default shaders and
        // textures
        //loadShaders(gl);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // set our viewport
        GLES20.glViewport(0, 0, width, height);

        // set the screen dimensions singleton, so we can
        // query it for the values later
        //ScreenDimensions.getInstance().setDimensions(width, height);

        // and we set dimensions for our camera (viewpoint as well)
        //viewpoint.setDimensions(width, height);
        //viewpoint.setWorldTranslation(-width / 2.0f, height / 2.0f, 0.0f);
        //viewpoint.updateProjMatrix();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // we have to now load textures if there are pending loads
        //TextureLibrary.getInstance().loadTextures(gl);

        // now we process our animations
        //AnimManager.getInstance().run();

        // clear buffers!
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // draw render objects
        //drawRenderObjects(gl);
    }
}

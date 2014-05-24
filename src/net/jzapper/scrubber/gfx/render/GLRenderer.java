package net.jzapper.scrubber.gfx.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import net.jzapper.scrubber.R;
import net.jzapper.scrubber.gfx.utils.GfxUtils;
import net.jzapper.scrubber.gfx.viewpoint.FrustumViewpoint;
import net.jzapper.scrubber.gfx.viewpoint.Viewpoint;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

/**
 * User: jchionh
 * Date: 5/24/14
 * Time: 1:40 PM
 */
public class GLRenderer  implements GLSurfaceView.Renderer {

    /**
     * handles for our default shader program
     */
    public static class ShaderHandleRef {
        public int programHandle;
        public int posHandle;
        public int colorHandle;
        public int matrixHandle;
        public int texMatrixHandle;
        public int texSamplerHandle;
        public int texCoordHandle;
    }

    protected Context context;
    protected Viewpoint viewpoint;
    protected ShaderHandleRef defaultShaderHandleRef = null;
    protected int currentShaderProgram;

    // init the list of render objects to the empty list;
    ArrayList< ? extends RenderObject> renderObjects = new ArrayList<RenderObject>();

    public GLRenderer(Context context) {
        this.context = context;
        viewpoint = new FrustumViewpoint();
        viewpoint.setPos(0.0f, 0.0f, 2.0f);
        viewpoint.setLookAt(0.0f, 0.0f, 0.0f);
        viewpoint.updateViewMatrix();
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // just log it
        Log.v("_CREATE_", "GfxRenderer onSurfaceCreated()");

        // enable some GL stuff
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // here we just set the background color of our GL surface
        GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        // GLES20.glEnable(GLES20.GL_CULL_FACE);

        // on the surface created, let's load up all our default shaders and
        // textures
        loadDefaultShaders(gl);
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // set our viewport
        GLES20.glViewport(0, 0, width, height);

        // set the screen dimensions singleton, so we can
        // query it for the values later
        ScreenDimensions.getInstance().setDimensions(width, height);

        // and we set dimensions for our camera (viewpoint as well)
        viewpoint.setDimensions(width, height);
        viewpoint.setWorldTranslation(-width / 2.0f, height / 2.0f, 0.0f);
        viewpoint.updateProjMatrix();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // clear buffers!
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // draw render objects
        drawRenderObjects(gl);
    }

    protected void drawRenderObjects(GL10 glUnused) {
        // now traverse this list and render it
        int numRenderObjects = renderObjects.size();
        for (int i = 0; i < numRenderObjects; ++i) {
            RenderObject renderObject = renderObjects.get(i);
            renderObject.draw(glUnused, this);
        }
    }

    /**
     * loading the default shaders
     *
     * @param glUnused
     */
    private void loadDefaultShaders(GL10 glUnused) {
        // load up the code
        final String vtxShaderCode = GfxUtils.loadShaderSource(context,
                R.raw.def_vtx_shader);
        final String frgShaderCode = GfxUtils.loadShaderSource(context,
                R.raw.def_tex_shader);
        final int vtxShaderHandle = GfxUtils.compileShader(
                GLES20.GL_VERTEX_SHADER, vtxShaderCode);
        final int frgShaderHandle = GfxUtils.compileShader(
                GLES20.GL_FRAGMENT_SHADER, frgShaderCode);

        final int programHandle = GfxUtils.createShaderProgram(vtxShaderHandle,
                frgShaderHandle, new String[] { "a_Position", "a_Color",
                "a_TexCoord" });

        defaultShaderHandleRef = new ShaderHandleRef();
        // now let's set all the references
        defaultShaderHandleRef.programHandle = programHandle;
        defaultShaderHandleRef.posHandle = GLES20.glGetAttribLocation(
                programHandle, "a_Position");
        defaultShaderHandleRef.colorHandle = GLES20.glGetAttribLocation(
                programHandle, "a_Color");
        defaultShaderHandleRef.matrixHandle = GLES20.glGetUniformLocation(
                programHandle, "u_MVPMatrix");
        defaultShaderHandleRef.texMatrixHandle = GLES20.glGetUniformLocation(
                programHandle, "u_TexMatrix");
        defaultShaderHandleRef.texSamplerHandle = GLES20.glGetUniformLocation(
                programHandle, "u_Texture");
        defaultShaderHandleRef.texCoordHandle = GLES20.glGetAttribLocation(
                programHandle, "a_TexCoord");

        currentShaderProgram = programHandle;
        GLES20.glUseProgram(programHandle);
    }


    /**
     * get the view projection matrix from our viewpoint
     *
     * @return
     */
    public float[] getViewProjMatrix() {
        return viewpoint.getViewProjMatrix();
    }

    public ShaderHandleRef getDefaultShaderHandleRef() {
        return defaultShaderHandleRef;
    }

    /**
     * Here we set the render object list so that the rendere can traver it and
     * render.
     *
     * @param renderObjectList
     */
    public void setRenderObjectList(ArrayList<? extends RenderObject> renderObjectList) {
        this.renderObjects = renderObjectList;
    }
}

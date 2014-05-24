package net.jzapper.scrubber.gfx.render;

import android.opengl.GLES20;
import android.opengl.Matrix;
import net.jzapper.scrubber.gfx.shape.Shape;
import net.jzapper.scrubber.gfx.texture.Texture;
import net.jzapper.scrubber.gfx.utils.GfxUtils;
import net.jzapper.scrubber.gfx.render.GLRenderer.ShaderHandleRef;

import javax.microedition.khronos.opengles.GL10;
import java.nio.FloatBuffer;

/**
 * User: jchionh
 * Date: 5/24/14
 * Time: 3:44 PM
 */
public class RenderObject {

    public static final Texture EMPTY_TEXTURE = new Texture();

    public static final int TEXTURE_NONE = 0;
    public static final int NUM_FLOATS_4X4_MTX = 16;
    public static final int NUM_FLOATS_2X2_MTX = 4;

    // indices into positions
    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;
    public static final int W = 3;

    // indices into orientation
    public static final int PITCH = 0;
    public static final int YAW   = 1;
    public static final int ROLL  = 2;

    // indices into screen coord bb
    public static final int MIN_X = 0;
    public static final int MIN_Y = 1;
    public static final int MAX_X = 2;
    public static final int MAX_Y = 3;

    // clip space bounds for visibility checking
    public static final float NDC_MIN_X = -1.0f;
    public static final float NDC_MIN_Y = -1.0f;
    public static final float NDC_MAX_X =  1.0f;
    public static final float NDC_MAX_Y =  1.0f;


    // here's our transforms
    protected float[] pos = { 0.0f, 0.0f, 0.0f };
    protected float[] orientation = { 0.0f, 0.0f, 0.0f };
    protected float[] scale = { 1.0f, 1.0f, 1.0f };

    // here's our texture transforms
    protected float[] texTranslate = { 0.0f, 0.0f };
    protected float texRotate = 0.0f;
    protected float[] texScale = { 1.0f, 1.0f };

    // here's our shape
    protected Shape shape;

    // texture name for drawing textures
    protected Texture texture = EMPTY_TEXTURE;
    protected int texBindingType = GLES20.GL_TEXTURE_2D;

    // matrices for intermediate calculation
    protected float[] modelMtx = new float[NUM_FLOATS_4X4_MTX];
    protected float[] mvpMtx = new float[NUM_FLOATS_4X4_MTX];
    protected float[] texMtx = new float[NUM_FLOATS_4X4_MTX];
    protected float[] ndcBB = new float[NUM_FLOATS_2X2_MTX];
    protected float[] vizTestVtx = new float[NUM_FLOATS_2X2_MTX];

    // is visible boolean
    protected boolean isVisible = false;

    /**
     * ctor
     */
    public RenderObject() {
        // identity all our matrices
        Matrix.setIdentityM(modelMtx, 0);
        Matrix.setIdentityM(mvpMtx, 0);
        Matrix.setIdentityM(texMtx, 0);

        texture = GfxUtils.getUntexturedTexture();
    }

    /**
     * calcualte texture transform
     */
    public void calcTexMatrix() {
        Matrix.setIdentityM(texMtx, 0);
        Matrix.translateM(texMtx, 0, texTranslate[X], texTranslate[Y], 0.0f);
        Matrix.rotateM(texMtx, 0, texRotate, 0.0f, 0.0f, 1.0f);
        Matrix.scaleM(texMtx, 0, texScale[X], texScale[Y], 1.0f);
    }

    /**
     * compose the model matrix with our transforms
     */
    public void calcModelMatrix() {
        Matrix.setIdentityM(modelMtx, 0);
        Matrix.translateM(modelMtx, 0, pos[X], pos[Y], pos[Z]);
        Matrix.rotateM(modelMtx, 0, orientation[PITCH], 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(modelMtx, 0, orientation[YAW], 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(modelMtx, 0, orientation[ROLL], 0.0f, 0.0f, 1.0f);
        Matrix.scaleM(modelMtx, 0, scale[X], scale[Y], scale[Z]);
    }

    /**
     * calculate our mvpMatrix
     * @param renderCtx
     */
    public void calcMVPMatrix(GLRenderer renderCtx) {
        float[] vpMtx = renderCtx.getViewProjMatrix();
        Matrix.multiplyMM(mvpMtx, 0, vpMtx, 0, modelMtx, 0);
    }

    /**
     * calcualte the ndc axis aligned bounding box of our render object
     */
    public void calcNormSpaceBB() {
        // get our vertex buffer from our shape
        FloatBuffer vtxBuffer = shape.getVtxBuffer();
        final int limit = vtxBuffer.limit();

        // init our bb
        ndcBB[MIN_X] = Float.MAX_VALUE;
        ndcBB[MIN_Y] = Float.MAX_VALUE;
        ndcBB[MAX_X] = -Float.MAX_VALUE;
        ndcBB[MAX_Y] = -Float.MAX_VALUE;

        for (int i = 0; i < limit; i += Shape.FLOATS_PER_VERTEX) {
            // get our vtx values into a float array to
            // transform it and calculate the transformed values
            vizTestVtx[X] = vtxBuffer.get(i);
            vizTestVtx[Y] = vtxBuffer.get(i + 1);
            vizTestVtx[Z] = vtxBuffer.get(i + 2);
            vizTestVtx[W] = 1.0f;

            // transform the vtx to world coords
            Matrix.multiplyMV(vizTestVtx, 0, mvpMtx, 0, vizTestVtx, 0);

            // divide the vertex by w to get it into NDC clip space
            float ndcVtxX = vizTestVtx[X] / vizTestVtx[W];
            float ndcVtxY = vizTestVtx[Y] / vizTestVtx[W];

            // now compute the axis aligned bounding box of the shape
            ndcBB[MIN_X] = Math.min(ndcVtxX, ndcBB[MIN_X]);
            ndcBB[MIN_Y] = Math.min(ndcVtxY, ndcBB[MIN_Y]);
            ndcBB[MAX_X] = Math.max(ndcVtxX, ndcBB[MAX_X]);
            ndcBB[MAX_Y] = Math.max(ndcVtxY, ndcBB[MAX_Y]);
        }
    }

    /**
     * with the ncd bb, we can determine if our render object is on screen or not
     * @return
     */
    public boolean calcScreenVisibility() {
        // assuming screen BB has been calculated
        if (NDC_MAX_X < ndcBB[MIN_X]) {
            return false; // ndc bounds is left of BB
        }
        if (NDC_MIN_X > ndcBB[MAX_X]) {
            return false; // ndc bounds is right of BB
        }
        if (NDC_MAX_Y < ndcBB[MIN_Y]) {
            return false; // ndc bounds is above of BB
        }
        if (NDC_MIN_Y > ndcBB[MAX_Y]) {
            return false; // ndc bounds is below of BB
        }
        return true;
    }

    /**
     * set texture
     * @param texture
     */
    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    /**
     * Overridable drawTexture method
     * @param ref
     */
    public void drawTexture(ShaderHandleRef ref) {
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.name[0]);
        // pass the sampler in
        GLES20.glUniform1i(ref.texSamplerHandle, 0);
        // pass the tex coord in
        int texCoordHandle = ref.texCoordHandle;
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, shape.getTexCoordBuffer());
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        // calcualte our tex transform
        calcTexMatrix();
        // pass the tex transform in
        GLES20.glUniformMatrix4fv(ref.texMatrixHandle, 1, false, texMtx, 0);
    }

    /**
     * this is the draw call
     * it'll take the renderer as the render context and
     * call GL statements for drawing
     * @param renderCtx
     */
    public void draw(GL10 glUnused, GLRenderer renderCtx) {
        // first we need to calculate our model matrix
        calcModelMatrix();
        // then calculate our mvp matrix
        calcMVPMatrix(renderCtx);
        // then calculate our ndc BB for screen visibility testing
        calcNormSpaceBB();
        // now do the test
        isVisible = calcScreenVisibility();
        // don't render if we're not visible
        if (!isVisible) {
            return;
        }

        GLRenderer.ShaderHandleRef shaderHandleRef = renderCtx.getDefaultShaderHandleRef();
        // NOTE:
        // we assume at the moment that there's only one program to use, which is the default
        // in future, if there are more than one shader program, we need to call it here to switch
        //renderCtx.useShaderProgram(shaderHandleRef.programHandle);

        // vertices
        int posHandle = shaderHandleRef.posHandle;
        GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 0, shape.getVtxBuffer());
        GLES20.glEnableVertexAttribArray(posHandle);
        // colors
        int clrHandle = shaderHandleRef.colorHandle;
        GLES20.glVertexAttribPointer(clrHandle, 4, GLES20.GL_FLOAT, false, 0, shape.getClrBuffer());
        GLES20.glEnableVertexAttribArray(clrHandle);
        // texture
        drawTexture(shaderHandleRef);
        // set the mvp matrix into the shader
        GLES20.glUniformMatrix4fv(shaderHandleRef.matrixHandle, 1, false, mvpMtx, 0);
        // draw!
        GLES20.glDrawArrays(shape.getDrawMode(), 0, shape.getNumVertices());
    }

    public void setPos(float x, float y, float z) {
        pos[X] = x;
        pos[Y] = y;
        pos[Z] = z;
    }

    public void setOrientation(float pitch, float yaw, float roll) {
        orientation[PITCH] = pitch;
        orientation[YAW] = yaw;
        orientation[ROLL] = roll;
    }

    public void setScale(float x, float y, float z) {
        scale[X] = x;
        scale[Y] = y;
        scale[Z] = z;
    }

    public void setTexScale(float x, float y) {
        texScale[X] = x;
        texScale[Y] = y;
    }

    public void setTexTranslate(float x, float y) {
        texTranslate[X] = x;
        texTranslate[Y] = y;
    }

    public void setTexRotate(float r) {
        texRotate = r;
    }
}

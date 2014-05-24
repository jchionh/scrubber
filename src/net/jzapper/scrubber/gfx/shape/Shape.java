package net.jzapper.scrubber.gfx.shape;

import android.opengl.GLES20;
import net.jzapper.scrubber.gfx.utils.GfxUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * User: jchionh
 * Date: 5/24/14
 * Time: 3:49 PM
 */
public class Shape {
    // you know, perhaps we dont' need these
    // and just require the created buffers

    public static final int FLOATS_PER_VERTEX = 3;
    public static final int FLOATS_PER_TEX_COORD = 2;

    protected int drawMode = GLES20.GL_TRIANGLE_STRIP;
    protected int numVertices = 0;

    // here are the buffers that we need
    protected FloatBuffer vtxBuffer = null;
    protected ShortBuffer idxBuffer = null;
    protected FloatBuffer clrBuffer = null;
    protected FloatBuffer texCoordBuffer = null;

    /**
     * ctor for the Mesh
     *
     */
    public Shape() {

    }

    /**
     * the opengl drawmode
     * valid: GLES20.GL_LINE_STRIP, GLES20.GL_LINE_LOOP, GLES20.GL_POINTS,
     *        GLES20.GL_TRIANGE_STRIP, GLES20.GL_TRIANGLE_FAN, GLES20.GL_TRIANGES
     * @param drawMode
     */
    public void setDrawMode(int drawMode) {
        this.drawMode = drawMode;
    }

    /**
     * init our vertex buffer
     * after this call, our vtxBuffer should have been created
     *
     * @param  source this is a float array
     */
    public void initVtxBuffer(final float[] source) {
        numVertices = source.length / FLOATS_PER_VERTEX;
        vtxBuffer = GfxUtils.createFloatBuffer(source);
    }

    /**
     * init our color buffer
     * after this call, our color should have been created
     *
     * @param  source this is a float array
     */
    public void initClrBuffer(final float[] source) {
        clrBuffer = GfxUtils.createFloatBuffer(source);
    }

    /**
     * init our texture coordinate buffer
     * after this call, our texBuffer should have been created
     *
     * @param  source this is a float array
     */
    public void initTexCoordBuffer(final float[] source) {
        texCoordBuffer = GfxUtils.createFloatBuffer(source);
    }

    /**
     * init our index buffer
     * after this call, our idxBuffer should have been created
     *
     * @param  source this is a float array
     */
    public void initIdxBuffer(final short[] source) {
        idxBuffer = GfxUtils.createShortBuffer(source);
    }

    public int getDrawMode() {
        return drawMode;
    }

    public int getNumVertices() {
        return numVertices;
    }

    public FloatBuffer getTexCoordBuffer() {
        return texCoordBuffer;
    }

    public FloatBuffer getVtxBuffer() {
        return vtxBuffer;
    }

    public FloatBuffer getClrBuffer() {
        return clrBuffer;
    }

    public ShortBuffer getIdxBuffer() {
        return idxBuffer;
    }
}

package net.jzapper.scrubber.gfx.shape;

import android.opengl.GLES20;

/**
 * User: jchionh
 * Date: 5/24/14
 * Time: 3:49 PM
 */
public class QuadShape extends Shape {
    public static final int INDEX_TOP_LEFT  = 0;
    public static final int INDEX_BOT_LEFT  = 1;
    public static final int INDEX_TOP_RIGHT = 2;
    public static final int INDEX_BOT_RIGHT = 3;

    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;

    public static final int NUM_VERTICES = 4;

    private static final float[] VERTICES = {
            -0.5f,  0.5f, 0.0f, // top left
            -0.5f, -0.5f, 0.0f, // bot left
            0.5f,  0.5f, 0.0f, // top right
            0.5f, -0.5f, 0.0f  // bot right
    };

    private static final short[] INDICES = {
            INDEX_TOP_LEFT,
            INDEX_BOT_LEFT,
            INDEX_TOP_RIGHT,
            INDEX_BOT_RIGHT
    };

    private static final float[] VERTEX_COLORS = {
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f
    };

    private static final float[] TEX_COORDS = {
            0.0f, 0.0f, // top left
            0.0f, 1.0f, // bot left
            1.0f, 0.0f, // top right
            1.0f, 1.0f  // bot right
    };

    // we store the values here so we can use it to init buffers
    private final float[] vertices = {
            -0.5f,  0.5f, 0.0f, // top left
            -0.5f, -0.5f, 0.0f, // bot left
            0.5f,  0.5f, 0.0f, // top right
            0.5f, -0.5f, 0.0f  // bot right
    };

    private final float[] texCoords = {
            0.0f, 0.0f, // top left
            0.0f, 1.0f, // bot left
            1.0f, 0.0f, // top right
            1.0f, 1.0f  // bot right
    };

    /**
     * ctor
     */
    public QuadShape() {
        // set our draw mode
        setDrawMode(GLES20.GL_TRIANGLE_STRIP);
        // then init our buffers
        initVtxBuffer(VERTICES);
        initClrBuffer(VERTEX_COLORS);
        initIdxBuffer(INDICES);
        initTexCoordBuffer(TEX_COORDS);
    }

    /**
     * set the dimensions of this quad
     * @param width
     * @param height
     */
    public void setDimensions(float width, float height) {
        // iterate and set values into our temp float buffer
        // and set the values straight
        for (int i = 0; i < NUM_VERTICES; ++i) {
            // for every vertex, multiply with the width and height
            int row = i * FLOATS_PER_VERTEX;
            vertices[row + X] = VERTICES[row + X] * width;
            vertices[row + Y] = VERTICES[row + Y] * height;
            vertices[row + Z] = 0.0f;
        }
        // init a new vertex buffer
        initVtxBuffer(vertices);
    }

    /**
     * set the dimensions of the texture mapping
     * @param width
     * @param height
     */
    public void setTexDimensions(float width, float height) {
        // iterate and set tex coords dimensions
        for (int i = 0; i < NUM_VERTICES; ++i) {
            // for every vertex, set the tex coord values
            int row = i * FLOATS_PER_TEX_COORD;
            texCoords[row + X] = TEX_COORDS[row + X] * width;
            texCoords[row + Y] = TEX_COORDS[row + Y] * height;
        }
        // then we init our tex coords
        initTexCoordBuffer(texCoords);
    }

    /**
     * width
     * @return
     */
    public float getWidth() {
        return vertices[(INDEX_TOP_RIGHT * FLOATS_PER_VERTEX) + X] - vertices[(INDEX_TOP_LEFT * FLOATS_PER_VERTEX) + X];
    }

    /**
     * height
     * @return
     */
    public float getHeight() {
        return vertices[(INDEX_TOP_RIGHT * FLOATS_PER_VERTEX) + Y] - vertices[(INDEX_BOT_RIGHT * FLOATS_PER_VERTEX) + Y];
    }
}

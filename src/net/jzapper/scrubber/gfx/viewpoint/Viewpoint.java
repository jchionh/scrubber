package net.jzapper.scrubber.gfx.viewpoint;

import android.opengl.Matrix;
import net.jzapper.scrubber.gfx.geom.Geom;

/**
 * User: jchionh
 * Date: 5/24/14
 * Time: 2:08 PM
 */
public abstract class Viewpoint {

    // indices to position
    protected static final int X = Geom.X;
    protected static final int Y = Geom.Y;
    protected static final int Z = Geom.Z;

    // attribs of our viewpoint
    protected float[] position = { 0.0f, 0.0f, 2.0f };
    protected float[] lookat = { 0.0f, 0.0f, 0.0f };
    protected float[] up = { 0.0f, 1.0f, 0.0f };

    // world transform applied to the final view-proj matrix
    protected float[] worldTranslation = { 0.0f, 0.0f, 0.0f };
    protected float[] worldScale = { 1.0f, 1.0f, 1.0f };

    // near and far planes
    protected float near = 1.0f;
    protected float far = 100.0f;

    // w and h of the camera viewport
    protected int width;
    protected int height;

    // storage for the various matrices
    protected float[] viewMatrix = new float[16];
    protected float[] projMatrix = new float[16];
    protected float[] vpMatrix = new float[16];

    /**
     * ctor
     */
    public Viewpoint() {
        // idendity all the matrices
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setIdentityM(projMatrix, 0);
        Matrix.setIdentityM(vpMatrix, 0);
    }

    /**
     * the derived viewpoint class needs to implement this function to calculate the
     * view-projection matrix
     */
    protected abstract void calcViewProjMatrix();

    /**
     * update our view matrix and then calculate the vp matrix
     */
    public void updateViewMatrix() {
        Matrix.setLookAtM(viewMatrix, 0, position[X], position[Y], position[Z], lookat[X], lookat[Y], lookat[Z], up[X], up[Y], up[Z]);
        calcViewProjMatrix();
    }

    /**
     * update our proj matrix
     * then calculate the vp matrix
     */
    public void updateProjMatrix() {
        calcViewProjMatrix();
    }

    /**
     * set dims of the viewport (for calculating the proj matrix)
     * @param width
     * @param height
     */
    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setWorldTranslation(float x, float y, float z) {
        worldTranslation[X] = x;
        worldTranslation[Y] = y;
        worldTranslation[Z] = z;
    }

    public void setWorldScale(float x, float y, float z) {
        worldScale[X] = x;
        worldScale[Y] = y;
        worldScale[Z] = z;
    }

    public void setPos(float x, float y, float z) {
        position[X] = x;
        position[Y] = y;
        position[Z] = z;
    }

    public void setLookAt(float x, float y, float z) {
        lookat[X] = x;
        lookat[Y] = y;
        lookat[Z] = z;
    }

    public float[] getViewMatrix() {
        return viewMatrix;
    }

    public float[] getProjMatrix() {
        return projMatrix;
    }

    public float[] getViewProjMatrix() {
        return vpMatrix;
    }
}

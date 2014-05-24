package net.jzapper.scrubber.gfx.viewpoint;

import android.opengl.Matrix;

/**
 * User: jchionh
 * Date: 5/24/14
 * Time: 2:10 PM
 */
public class FrustumViewpoint extends Viewpoint {
    private static final float BOTTOM = -1.0f;
    private static final float TOP = 1.0f;

    private float aspectRatio = 1.0f;

    /**
     * update our projection matrix
     */
    @Override
    public void updateProjMatrix() {
        aspectRatio = (float) width / (float) height;
        Matrix.frustumM(projMatrix, 0, -aspectRatio, aspectRatio, BOTTOM, TOP, near, far);
        super.updateProjMatrix();
    }

    /**
     * here's the method that calculates our final view proj matrix
     */
    @Override
    protected void calcViewProjMatrix() {

        // we compute a world scale that will map all world units on the XY plane at z=0
        // to be 1-1 mapping of pixel units on screen.
        float xExtentAtZeroZ = aspectRatio * position[Z];
        float mapScale = xExtentAtZeroZ / (width / 2.0f);
        // mapScale will scale units to 1-1 pixel mapping on the XY plane
        setWorldScale(mapScale, mapScale, mapScale);

        // compute the vpMatrix here
        Matrix.multiplyMM(vpMatrix, 0, projMatrix, 0, viewMatrix, 0);

        // perform our world transform here
        Matrix.scaleM(vpMatrix, 0, worldScale[X], worldScale[Y], worldScale[Z]);
        Matrix.translateM(vpMatrix, 0, worldTranslation[X], worldTranslation[Y], worldTranslation[Z]);
    }
}

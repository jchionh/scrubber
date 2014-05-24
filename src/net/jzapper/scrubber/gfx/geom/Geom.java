package net.jzapper.scrubber.gfx.geom;

/**
 * User: jchionh
 * Date: 5/24/14
 * Time: 2:09 PM
 */
public class Geom {

    public static final int MATRIX_SIZE_4X4 = 16;
    public static final int MATRIX_SIZE_2X2 = 4;

    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;
    public static final int W = 3;

    public static final int PITCH = 0;
    public static final int YAW   = 1;
    public static final int ROLL  = 2;

    // 4x4 matrix index positions for transform
    public static final int MTX4_SCALE_X = 0;
    public static final int MTX4_SCALE_Y = 5;
    public static final int MTX4_SCALE_Z = 10;
    public static final int MTX4_TRANS_X = 12;
    public static final int MTX4_TRANS_Y = 13;
    public static final int MTX4_TRANS_Z = 14;
    public static final int MTX4_TRANS_W = 15;
}

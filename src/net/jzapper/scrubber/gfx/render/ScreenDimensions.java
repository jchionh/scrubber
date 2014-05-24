package net.jzapper.scrubber.gfx.render;

/**
 * User: jchionh
 * Date: 5/24/14
 * Time: 2:12 PM
 */
public class ScreenDimensions {

    // this is our singleton instance
    private static ScreenDimensions instance = new ScreenDimensions();

    // width and height
    private int width = 0;
    private int height = 0;

    // private ctor
    private ScreenDimensions() {

    }

    /**
     * getting the reference to the single instance
     * @return
     */
    public static ScreenDimensions getInstance() {
        return instance;
    }

    /**
     * Here we set the width and height, so that we can query it later
     *
     * @param width
     * @param height
     */
    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * get height
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * get width
     * @return
     */
    public int getWidth() {
        return width;
    }
}

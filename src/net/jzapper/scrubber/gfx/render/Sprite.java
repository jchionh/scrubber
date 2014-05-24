package net.jzapper.scrubber.gfx.render;

import net.jzapper.scrubber.gfx.shape.QuadShape;

/**
 * User: jchionh
 * Date: 5/24/14
 * Time: 3:57 PM
 */
public class Sprite extends RenderObject {
    private QuadShape quadShape = new QuadShape();

    // ctor
    public Sprite() {
        shape = quadShape;
        quadShape.setDimensions(640, 640);
    }
}

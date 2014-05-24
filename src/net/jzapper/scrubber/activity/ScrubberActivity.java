package net.jzapper.scrubber.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import net.jzapper.scrubber.R;
import net.jzapper.scrubber.gfx.render.GLES20View;

/**
 * This is our main activity that runs
 */
public class ScrubberActivity extends Activity {


    private GLES20View view;
    private ScrubberRenderer renderer;
    private GestureDetector mDetector;

    private final GestureDetector.SimpleOnGestureListener gestureListener
            = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            // Scrolling uses math based on the viewport (as opposed to math using pixels).
            // renderer also scrubs the video
            renderer.scrub(distanceX);
            return true;
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // set our android layout
        setContentView(R.layout.main);

        // setup our GLView and renderer
        view = new GLES20View(this);
        renderer = new ScrubberRenderer(this);
        view.setRenderer(renderer);

        // now, we find the container in our android layout, and attach the GL view
        // to the container
        ((ViewGroup) findViewById(R.id.glsurface_container)).addView(view);

        // create a gesture detector to receive scroll inputs
        mDetector = new GestureDetector(this,gestureListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);

    }

}

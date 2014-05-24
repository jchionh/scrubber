package net.jzapper.scrubber;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import net.jzapper.scrubber.gfx.render.GLES20View;
import net.jzapper.scrubber.gfx.render.GLRenderer;

public class ScrubberActivity extends Activity {


    GLES20View view;
    GLRenderer renderer;

    //Sprite testSprite = new Sprite();
    //ArrayList<Sprite> sprites = new ArrayList<Sprite>();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);

        // setup our view and renderer
        view = new GLES20View(this);
        renderer = new GLRenderer(this);
        view.setRenderer(renderer);
        //setContentView(view);

        ((ViewGroup) findViewById(R.id.glsurface_container)).addView(view);

        //testSprite.setPos(250.0f, -250.0f, 0.0f);
        //sprites.add(testSprite);
        //renderer.setRenderObjectList(sprites);
    }
}

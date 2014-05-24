package net.jzapper.scrubber.gfx.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.text.TextPaint;
import android.util.Log;
import net.jzapper.scrubber.gfx.render.RenderObject;
import net.jzapper.scrubber.gfx.text.TextConfig;
import net.jzapper.scrubber.gfx.texture.Texture;

import javax.microedition.khronos.opengles.GL10;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * User: jchionh
 * Date: 5/24/14
 * Time: 3:39 PM
 */
public class GfxUtils {

    /**
     * contant values declared here
     */
    private static final int FLOAT_BYTES = 4;
    private static final int SHORT_BYTES = 2;
    private static final int MAX_TEXTURE_DIMENSION = 1024;

    private static Texture untexturedTexture = RenderObject.EMPTY_TEXTURE;
    private static Texture unloadedTexture = RenderObject.EMPTY_TEXTURE;

    /**
     * Create a FloatBuffer from a array of floats.
     *
     * @param  source this is a float array
     * @return returns a FloatBuffer object that contains the values of source
     */
    public static FloatBuffer createFloatBuffer(final float[] source) {
        ByteBuffer bb = ByteBuffer.allocateDirect(source.length * FLOAT_BYTES);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(source);
        fb.position(0);
        return fb;
    }

    /**
     * Create a ShortBuffer from a array of floats.
     *
     * @param  source this is a short array
     * @return returns a ShortBuffer object that contains the values of source
     */
    public static ShortBuffer createShortBuffer(final short[] source) {
        ByteBuffer bb = ByteBuffer.allocateDirect(source.length * SHORT_BYTES);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer sb = bb.asShortBuffer();
        sb.put(source);
        sb.position(0);
        return sb;
    }

    /**
     * Load the Shader source files into a string for compilation
     *
     * @param context
     * @param resourceId
     * @return the shader files as a string.
     */
    public static String loadShaderSource(final Context context, final int resourceId) {
        final InputStream inputStream = context.getResources().openRawResource(resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = null;
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to read shader source for resId: " + resourceId, e);
        }
        return stringBuilder.toString();
    }

    /**
     *
     * @param	shaderType OpenGl ES 2.0 definition of the shaders: GLES20.GL_VERTEX_SHADER, GLES20.GL_FRAGMENT_SHADER
     * @param	shaderSource source code of the shader as a string.
     * @return	the shader handle
     */
    public static int compileShader(final int shaderType, final String shaderSource) {
        // create a shader handle for reference
        int shaderHandle = GLES20.glCreateShader(shaderType);
        if (shaderHandle == 0) {
            throw new RuntimeException("GfxUtils: Error Creating Shader.");
        }

        // now let's link and compile
        GLES20.glShaderSource(shaderHandle, shaderSource);
        // compile it
        GLES20.glCompileShader(shaderHandle);
        // check compile status
        final int [] compileStatus = new int[1];
        GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        // delete the shader if copmile failed
        if (compileStatus[0] == 0) {
            String shaderLog = GLES20.glGetShaderInfoLog(shaderHandle);
            Log.e("GfxUtils", "Error compiling shader: " + shaderLog);
            // delete it
            GLES20.glDeleteShader(shaderHandle);
            throw new RuntimeException("GfxUtils: Error compiling shader: " + shaderLog);
        }

        // no errors? return the shader handle
        return shaderHandle;
    }

    /**
     *
     * @param vertexShaderHandle	the compiled vertex shader handle
     * @param fragmentShaderHandle	the compiled fragment shader handle
     * @param attributes			the attribute names as string array
     * @return						the program handle.
     */
    public static int createShaderProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes) {
        // program handle
        int programHandle = GLES20.glCreateProgram();
        if (programHandle == 0) {
            throw new RuntimeException("GfxUtils: Error creating Shader Program.");
        }

        // link the vertex shader and fragment shader
        GLES20.glAttachShader(programHandle, vertexShaderHandle);
        GLES20.glAttachShader(programHandle, fragmentShaderHandle);

        // bind our attributes so we can refer to them later
        if (attributes != null) {
            int numAttributes = attributes.length;
            for (int i = 0; i < numAttributes; ++i) {
                GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
            }
        }

        // proceed to link the shaders to a shader program
        GLES20.glLinkProgram(programHandle);

        // check errors
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            String linkLog = GLES20.glGetProgramInfoLog(programHandle);
            Log.e("GfxUtils", "Error linking program: " + linkLog);
            GLES20.glDeleteProgram(programHandle);
            throw new RuntimeException("GfxUtils: Error linking program: " + linkLog);
        }

        // we're good, return the program handle
        return programHandle;
    }

    /**
     * Calculate our sampling size to decode the bitmap
     * @param options
     * @param maxDimension
     * @return
     */
    public static int calculateSamplingSize(BitmapFactory.Options options, int maxDimension) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int samplingSize = 1;
        float heightScale = (float) maxDimension / (float) height;
        float widthScale = (float) maxDimension / (float) width;
        float scale = Math.min(heightScale, widthScale);
        if (scale < 1.0f) {
            int reqHeight = Math.round((float)height * scale);
            int reqWidth = Math.round((float) width * scale);
            if (height > reqHeight || width > reqWidth) {
                if (width > height) {
                    samplingSize = Math.round((float) height / (float) reqHeight);
                } else {
                    samplingSize = Math.round((float) width / (float) reqWidth);
                }
            }
        }
        return samplingSize;
    }

    /**
     * Decode an image from resourceId and then get a bitmap back
     *
     * @param res
     * @param resourceId
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    public static Bitmap decodeBitmapResource(Resources res, int resourceId, int desiredWidth, int desiredHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resourceId, options);

        int maxDimension = Math.min(desiredWidth, desiredHeight);
        maxDimension = Math.min(MAX_TEXTURE_DIMENSION, maxDimension);

        options.inSampleSize = GfxUtils.calculateSamplingSize(options, maxDimension);
        options.inJustDecodeBounds = false;

        final Bitmap bitmap = BitmapFactory.decodeResource(res, resourceId, options);
        return bitmap;
    }

    /**
     * decode the resource into a bitmap, but use the original bitmap size
     * @param res
     * @param resourceId
     * @return
     */
    public static Bitmap decodeBitmapResource(Resources res, int resourceId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resourceId, options);

        int maxDimension = Math.min(options.outHeight, options.outWidth);
        maxDimension = Math.min(MAX_TEXTURE_DIMENSION, maxDimension);

        options.inSampleSize = GfxUtils.calculateSamplingSize(options, maxDimension);
        options.inJustDecodeBounds = false;

        final Bitmap bitmap = BitmapFactory.decodeResource(res, resourceId, options);
        return bitmap;
    }

    /**
     * Now we can render text into a bitmap
     * @param text
     * @param configuration
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    public static Bitmap renderTextToBitmap(String text, TextConfig configuration, int desiredWidth, int desiredHeight) {
        int bitmapWidth = desiredWidth;
        int bitmapHeight = desiredHeight;

        float heightScale = (float) MAX_TEXTURE_DIMENSION / (float) desiredHeight;
        float widthScale = (float) MAX_TEXTURE_DIMENSION / (float) desiredWidth;
        float scale = (heightScale < widthScale) ? heightScale : widthScale;
        bitmapHeight = Math.round(desiredHeight * scale);
        bitmapWidth = Math.round(desiredWidth * scale);

        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        Paint bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(configuration.bgColor);

        TextPaint txtPaint = new TextPaint();
        txtPaint.setTextSize(configuration.fontSize);
        txtPaint.setTypeface(configuration.typeface);
        txtPaint.setAntiAlias(true);
        txtPaint.setColor(configuration.fontColor);
        txtPaint.setTextAlign(Paint.Align.LEFT);

        Rect textBounds = new Rect();
        txtPaint.getTextBounds(text, 0, text.length(), textBounds);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0,  0, bitmapWidth, bitmapHeight, bgPaint);
        canvas.drawText(
                text,
                (bitmapWidth - textBounds.width()) / 2,
                (bitmapHeight - textBounds.height()) / 2 + (textBounds.height() / 2),
                //(bitmapHeight - (textBounds.height() - textBounds.bottom)),
                txtPaint);
        return bitmap;
    }

    /**
     * to delete a texture from the GPU memory
     * @param glUnused
     * @param texture
     */
    public static void destroyTexture(GL10 glUnused, Texture texture) {
        GLES20.glDeleteTextures(1, texture.name, 0);
        texture.name[0] = 0;
        texture.ready = false;
    }

    /**
     * load a texture into the GPU
     * @param glUnused
     * @param bitmap
     * @param texture
     * @param wrapMode
     * @param withBorder
     */
    public static void loadTexture(GL10 glUnused, Bitmap bitmap, Texture texture, int wrapMode, boolean withBorder) {

        Bitmap dest = bitmap;
        Bitmap borderBitmap = null;

        texture.height = bitmap.getHeight();
        texture.width = bitmap.getWidth();

        if (withBorder) {
            RectF targetRect = new RectF(1, 1, bitmap.getWidth() + 1, bitmap.getHeight() + 1);
            borderBitmap = Bitmap.createBitmap(bitmap.getWidth() + 2, bitmap.getHeight() + 2, bitmap.getConfig());
            Canvas canvas = new Canvas(borderBitmap);
            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(bitmap, null, targetRect, null);
        }

        int[] name = new int[1];
        GLES20.glGenTextures(1, name, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, name[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, wrapMode);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, wrapMode);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, (borderBitmap != null) ? borderBitmap : dest, 0);

        texture.name[0] = name[0];
        texture.ready = true;

        // recycle the border bitmap if we've created it
        if (borderBitmap != null) {
            borderBitmap.recycle();
        }
    }

    public static Texture getUntexturedTexture() {
        return untexturedTexture;
    }

    public static Texture getUnloadedTexture() {
        return unloadedTexture;
    }

    public static void setUntexturedTexture(Texture texture) {
        untexturedTexture = texture;
    }

    public static void setUnloadedTexture(Texture texture) {
        unloadedTexture = texture;
    }
}

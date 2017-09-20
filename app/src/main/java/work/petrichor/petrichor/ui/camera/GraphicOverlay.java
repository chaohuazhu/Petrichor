package work.petrichor.petrichor.ui.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

import work.petrichor.vision.CameraSource;

/**
 * @author mitscherlich
 * @date 2017/9/9
 **/

public class GraphicOverlay extends View {

    private final Object mLock = new Object();
    private int mPreviewWidth;
    private float mWidthScaleFactor = 1.0f;
    private int mPreviewHeight;
    private float mHeightScaleFactor = 1.0f;
    private int mFacing = CameraSource.CAMERA_FACING_BACK;
    private Set<Graphic> mGraphics = new HashSet<>();

    /**
     * Base class for a custom graphics object to be rendered within the graphic overlay. Subclass
     * this and implement the {} method to define the graphics element. Add instances to the overlay
     * using {}.
     * **/
    public static abstract class Graphic {

        private GraphicOverlay mOverlay;

        public Graphic(GraphicOverlay overlay) {
            mOverlay = overlay;
        }

        /**
         * Draw the graphic on the supplied canvas. Drawing should use the following methos to
         * convert to view coordinates for the graphics that are draw:
         * <ol>
         *     <li>{@link Graphic#scaleX(float)} and {@link Graphic#scaleY(float)} adjust the size
         *     of the size of supplied value from the preview scale to view scale.</li>
         *     <li>{@link Graphic#translateX(float)} and {@link Graphic#translateY(float)} adjust
         *     the coordinate from the preview's coordinate system to the view coordinate
         *     system.</li>
         * </ol>
         *
         * @param canvas drawing canvas
         * **/
        public abstract void draw(Canvas canvas);

        /**
         * Adjust a horizontal value of the supplied value from the preview scale to view scale.
         * **/
        public float scaleX(float horizontal) {
            return horizontal * mOverlay.mWidthScaleFactor;
        }

        /**
         * Adjust a vertical value of the supplied value from the preview scale to the view scale.
         * **/
        public float scaleY(float vertical) {
            return vertical * mOverlay.mHeightScaleFactor;
        }

        /**
         * Adjust the x coordinate from the preview's coordinate system to the view coordinate
         * system.
         * **/
        public float translateX(float x) {
            if (mOverlay.mFacing == CameraSource.CAMERA_FACING_FRONT)
                return mOverlay.getWidth() - scaleX(x);
            else
                return scaleX(x);
        }

        /**
         * Adjust the y coordinate from the preview's coordinate system to the view coordinate
         * system.
         * **/
        public float translateY(float y) {
            return scaleY(y);
        }
    }

    public GraphicOverlay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Removes all graphics from the overlay.
     * **/
    public void clear() {
        synchronized (mLock) {
            mGraphics.clear();
        }
        postInvalidate();
    }

    /**
     * Adds a graphic to the overlay.
     * **/
    public void add(Graphic graphic) {
        synchronized (mLock) {
            mGraphics.add(graphic);
        }
        postInvalidate();
    }

    /**
     * Removes a graphic from the overlay.
     * **/
    public void remove(Graphic graphic) {
        synchronized (mLock) {
            mGraphics.remove(graphic);
        }
        postInvalidate();
    }

    /**
     * Sets the camera attributes for size and facing direction, which informs how to transform
     * image coordinates later.
     * **/
    public void setCameraInfo(int previewWidth, int previewHeight, int facing) {
        synchronized (mLock) {
            mPreviewWidth = previewWidth;
            mPreviewHeight = previewHeight;
            mFacing = facing;
        }
        postInvalidate();
    }

    /**
     * Draws the overlay with its associated graphic objects.
     * **/
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (mLock) {
            if ((mPreviewWidth != 0) && (mPreviewHeight != 0)) {
                mWidthScaleFactor = (float) canvas.getWidth() / (float) mPreviewWidth;
                mHeightScaleFactor = (float) canvas.getHeight() / (float) mPreviewHeight;
            }

            for (Graphic graphic : mGraphics) {
                graphic.draw(canvas);
            }
        }
    }
}

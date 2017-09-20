package work.petrichor.petrichor.ui.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.io.IOException;

import work.petrichor.vision.CameraSource;
import work.petrichor.vision.common.Size;

/**
 * @author  Mitscherlich
 * @date    2017/9/9
 * **/
public class CameraSourcePreview extends ViewGroup {

    private static final String TAG = "CameraSourcePreview";

    private Context mContext;
    private SurfaceView mSurfaceView;
    private boolean isStartRequested = false;
    private boolean isSurfaceAvailable = false;

    private GraphicOverlay mOverlay;
    private CameraSource mCameraSource;

    public CameraSourcePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mSurfaceView = new SurfaceView(context);
        mSurfaceView.getHolder().addCallback(new SurfaceCallback());
        addView(mSurfaceView);
    }

    public void start(CameraSource cameraSource) throws IOException {
        if (cameraSource == null)
            stop();

        mCameraSource = cameraSource;

        if (mCameraSource != null) {
            isStartRequested = true;
            startIfReady();
        }
    }

    public void start(CameraSource cameraSource, GraphicOverlay overlay) throws IOException {
        mOverlay = overlay;
        start(cameraSource);
    }

    public void stop() {
        if (mCameraSource != null)
            mCameraSource.stop();
    }

    public void release() {
        if (mCameraSource != null) {
            mCameraSource.release();
            mCameraSource = null;
        }
    }

    private void startIfReady() throws IOException {
        if (isStartRequested && isSurfaceAvailable) {
            mCameraSource.start(mSurfaceView.getHolder());
            if (mOverlay != null) {
                Size size = mCameraSource.getPreviewSize();
                int min = Math.min(size.getWidth(), size.getHeight());
                int max = Math.max(size.getWidth(), size.getHeight());
                if (isPortraitMode())
                    mOverlay.setCameraInfo(min, max, mCameraSource.getCameraFacing());
                else
                    mOverlay.setCameraInfo(max, min, mCameraSource.getCameraFacing());
                mOverlay.clear();
            }
            isStartRequested = false;
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            isSurfaceAvailable = true;
            try {
                startIfReady();
            } catch (IOException e) {
                android.util.Log.e(TAG, "Could not start camera source.", e);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            isSurfaceAvailable = false;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = 320; int height = 240;
        if (mCameraSource != null) {
            Size size = mCameraSource.getPreviewSize();
            if (size != null) {
                width = size.getWidth();
                height = size.getHeight();
            }
        }

        // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
        if (isPortraitMode()) {
            int tmp = width;
            width = height;
            height = tmp;
        }

        final int layoutWidth = right - left;
        final int layoutHeight = bottom - top;

        // Computes height and width for potentially doing fit width.
        int childWidth = layoutWidth;
        int childHeight = (int)(((float) layoutWidth / (float) width) * height);

        // If height is too tall using fit width, does fit height instead.
        if (childHeight > layoutHeight) {
            childHeight = layoutHeight;
            childWidth = (int)(((float) layoutHeight / (float) height) * width);
        }

        for (int i = 0; i < getChildCount(); ++i)
            getChildAt(i).layout(0, 0, childWidth, childHeight);

    }

    private boolean isPortraitMode() {
        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            return false;

        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            return true;


        Log.d(TAG, "isPortraitMode returning false by default");
        return false;
    }
}

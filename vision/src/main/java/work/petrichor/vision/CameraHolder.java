package work.petrichor.vision;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import work.petrichor.vision.common.Size;

/**
 * @author  Mitscherlich
 * @date    2017/9/1
 * **/
class CameraHolder {


    private static final String TAG = "vision.CameraHolder";
    private static volatile CameraHolder holder;

    private CameraHolder() { }

    static CameraHolder getHolder() {
        if (holder == null)
            synchronized (CameraHolder.class) {
                if (holder == null)
                    holder = new CameraHolder();
            }
        return holder;
    }

    private Camera camera;
    private int cameraId;
    private boolean isPreviewing = false;

    void open(int cameraId, OpenCameraCallback cb) {
        this.cameraId = cameraId;
        if (camera != null)
            throw new RuntimeException("Camera device has already been occupied!");

        camera = Camera.open(cameraId);

        if (null == camera)
            throw new RuntimeException("Camera device initialized failure!");

        cb.hasCameraOpened();
    }

    void updatePreviewFormat() {
        Camera.Parameters params = camera.getParameters();

        // Set preview format for face detector
        int prevFormat = getPreviewFormat(params);
        params.setPreviewFormat(prevFormat);
        android.util.Log.i(TAG, "Preview format is " + ((prevFormat == ImageFormat.YV12) ? "YV12" : "NV21"));

        // Set parameters back to camera device
        camera.setParameters(params);
    }

    void updatePreviewSize(Size size) {
        Camera.Parameters params = camera.getParameters();

        // Set preview size
        params.setPreviewSize(size.getWidth(), size.getHeight());
        android.util.Log.i(TAG, "Preview size is " + size.getWidth() + " * " + size.getHeight());
    }

    void updateScreenOrientation(Activity app) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = app.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    void setPreviewDisplay(SurfaceHolder holder) throws IOException {
        camera.setPreviewDisplay(holder);
    }

    void startPreview() {
        if (isPreviewing)
            stopPreview();

        if (null == camera)
            throw new RuntimeException("Camera device is not yet available!");

        camera.startPreview();
        isPreviewing = true;
    }

    void stopPreview() {
        if (camera != null) {
            camera.stopPreview();
            isPreviewing = false;
        }
    }

    void releaseHolder() {
        if (camera == null)
            android.util.Log.w(TAG, "Camera device has already been released!");
        else {
            stopPreview();

            camera.release();
            camera = null;
        }
    }

    void doTakePicture(Camera.PictureCallback callback) {
        camera.takePicture(null, null, callback);
    }

    private int getPreviewFormat(Camera.Parameters params) {
        List<Integer> formats = params.getSupportedPreviewFormats();
        boolean token = false;
        for (Integer format : formats)
            token = (format == ImageFormat.YV12);
        return token ? ImageFormat.YV12 : ImageFormat.NV21;
    }

    List<Size> getSupportPreviewSize() {
        List<Size> sizes = new ArrayList<>();
        for (Camera.Size size : camera.getParameters().getSupportedPreviewSizes())
            sizes.add(new Size(size.width, size.height));
        return sizes;
    }

    interface OpenCameraCallback {
        void hasCameraOpened();
    }

    interface PictureCallback extends Camera.PictureCallback {
        void onPictureSaved(String path);
    }
}

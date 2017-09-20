package work.petrichor.vision;

import android.app.Activity;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

import work.petrichor.vision.common.Size;

/**
 * @author  Mitscherlich
 * @date    2017/9/1
 * **/
public class CameraSource {

    private static final String TAG = "vision.CameraSource";

    public static final int CAMERA_FACING_FRONT = 1;
    public static final int CAMERA_FACING_BACK = 0;

    private CameraSource() {

    }

    private CameraHolder holder;
    private Size size;

    private int facing;

    public CameraHolder getHolder() {
        return holder;
    }

    public static class Builder {

        private Activity app;
        private CameraSource camera;
        private CameraHolder holder;
        private Size size;

        private int facing;

        public Builder(Activity app) {
            this.app = app;
            this.holder = CameraHolder.getHolder();
        }

        public Builder setPreviewSize(int w, int h) {
            List<Size> sizes = holder.getSupportPreviewSize();
            boolean token = false;
            for (Size size: sizes)
                if (token = size.equals(new Size(w, h))) break;

            if (!token)
                throw new IllegalArgumentException("Not supported preview size!");
            else {
                this.size = new Size(w, h);
                this.holder.updatePreviewSize(size);
            }

            return this;
        }

        public Builder setFacing(int cameraFacing) {
            if (cameraFacing != CAMERA_FACING_FRONT && cameraFacing != CAMERA_FACING_BACK)
                throw new IllegalArgumentException(new StringBuilder(32)
                        .append("Invalid camera facing \"")
                        .append(cameraFacing).append("\"!")
                        .toString());
            else {
                // Usually, opening camera device will take about 140ms.
                // To not block the main ui thread, initialize a camera device in another thread.
                facing = cameraFacing;
                holder.open(facing, new CameraHolder.OpenCameraCallback() {
                    @Override
                    public void hasCameraOpened() {
                        // Camera has been opened since it go into here.
                        android.util.Log.i(TAG, "Camera device has been initialized.");
                        holder.updatePreviewFormat();
                        holder.updateScreenOrientation(app);
                    }
                });
            }
            android.util.Log.d(TAG, "Initializing camera device.");
            return this;
        }

        public CameraSource build() {
            camera = new CameraSource();
            camera.facing = this.facing;
            camera.holder = this.holder;
            camera.size = this.size;
            return camera;
        }
    }

    public Size getPreviewSize() {
        return this.size;
    }

    public int getCameraFacing() {
        return this.facing;
    }

    public void start(SurfaceHolder holder) throws IOException {
        this.holder.setPreviewDisplay(holder);
        this.holder.startPreview();
    }

    public void stop() {
        this.holder.stopPreview();
    }

    public void release() {
        this.holder.releaseHolder();
    }
}

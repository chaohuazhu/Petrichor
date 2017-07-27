package work.petrichor.petrichor.modules;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.*;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import work.petrichor.seeta.FaceInfo;
import work.petrichor.seeta.ImageData;

/**
 * @author Mitscherlich.
 * @date 2017/7/26.
 **/

public class CameraInterface {

    private Camera mCamera;
    private Parameters mParams;
    private FaceDetectionTask mTask;
    private String modelPath = "";

    private boolean isPreview = false;

    private static CameraInterface instance;

    public static final int ON_SEETA_FACE_DETECTION_MESSAGE = 100;
    public static final int ON_GOOGLE_FACE_DETECTION_MESSAGE = 101;

    public static synchronized CameraInterface getInstance() {
        if (instance == null) instance = new CameraInterface();
        return instance;
    }

    private CameraInterface() {}

    public void openCamera(CameraOpenCallback callback) {
        mCamera = Camera.open(getFrontCameraID());
        callback.hasCameraOpened();
    }

    public void initParameters() {
        mParams = mCamera.getParameters();

        // 设置预览参数
        Size previewSize = getPreviewSize(mParams);
        mParams.setPreviewSize(previewSize.width / 3, previewSize.height / 3);
        mParams.setPreviewFormat(ImageFormat.YV12);

        // 设置拍照参数
        Size pictureSize = getPictureSize(mParams);
        mParams.setPictureSize(pictureSize.width, pictureSize.height);

        // 设置自动对焦模式
        String focusMode = getFocusMode(mParams);
        mParams.setFocusMode(focusMode);

        mCamera.setParameters(mParams);
        if (previewSize.width > previewSize.height)
            mCamera.setDisplayOrientation(90);
        else
            mCamera.setDisplayOrientation(0);
    }

    public void setPreviewDisplay(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFaceDetectionListener(Camera.FaceDetectionListener listener) {
        mCamera.setFaceDetectionListener(listener);
    }

    public void setFaceDetectionTask(String modelPath) {
        this.modelPath = modelPath;
        mTask = new FaceDetectionTask(modelPath);
    }

    public void setPreviewListener(final FaceDetectionListener listener) {
        mCamera.setPreviewCallback(new PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytes, Camera camera) {
                switch (mTask.getStatus()) {
                    case RUNNING:
                        return;
                    case PENDING:
                        mTask.cancel(false);
                }

                mTask = new FaceDetectionTask(modelPath);

                int width = mParams.getPreviewSize().width;
                int height = mParams.getPreviewSize().height;
                int channels = bytes.length / (width * height);

                if (bytes.length > 0) {
//                    long now = System.currentTimeMillis();
//                    byte[] data = filipFrontCameraPreviewData(bytes, width, height);
//                    long ms = System.currentTimeMillis() - now;
//                    android.util.Log.i("CameraInterface", String.format("filip front camera data in %dms", ms));
//                    ImageData frame = new ImageData(data, width, height, channels);
                    ImageData frame = new ImageData(bytes, width, height, channels);
                    try {
                        List<FaceInfo> faces = mTask.execute(frame).get();
                        if (faces == null) return;
                        else listener.onFaceDetection(faces);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void startPreview() {
        if (isPreview)
            stopPreview();

        mCamera.startPreview();
        isPreview = true;

        if (mCamera.getParameters().getMaxNumDetectedFaces() > 0)
            mCamera.startFaceDetection();
    }

    public void stopPreview() {
        try {
            if (mCamera != null) {
                if (mCamera.getParameters().getMaxNumDetectedFaces() > 0)
                    mCamera.stopFaceDetection();

                mCamera.setPreviewCallback(null);
                mCamera.setPreviewDisplay(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
                isPreview = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getFrontCameraID() {
//        return Camera.getNumberOfCameras() > 1 ? 1 : 0;
        return 0;
    }

    private Size getPreviewSize(Parameters params) {
        List<Size> sizes = params.getSupportedPreviewSizes();
        return sizes.get(0);
    }

    private String getFocusMode(Parameters params) {
        List<String> modes = params.getSupportedFocusModes();
        if (modes.contains("continuous-video"))
            return Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;
        else
            return Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
    }

    private Size getPictureSize(Parameters params) {
        List<Size> sizes = params.getSupportedPictureSizes();
        return sizes.get(0);
    }

//    private byte[] filipFrontCameraPreviewData(byte[] src, int cols, int rows) {
//        byte[] dst = new byte[src.length];
//
//        for (int i = 0; i < rows; ++i)
//            for (int j = 0; j < cols; ++j)
//                dst[(rows - i - 1) * cols + j] = src[cols * i + j];
//
//        return dst;
//    }

    public interface CameraOpenCallback {
        void hasCameraOpened();
    }

    public interface FaceDetectionListener {
        void onFaceDetection(List<FaceInfo> faces);
    }
}

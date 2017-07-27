package work.petrichor.petrichor.modules;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;

/**
 * @author Mitscherlich.
 * @date 2017/7/27.
 **/

public class GoogleFaceDetectionListener implements Camera.FaceDetectionListener {

    private Handler mHandler;

    public GoogleFaceDetectionListener(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
        Message message = mHandler.obtainMessage();
        message.what = CameraInterface.ON_GOOGLE_FACE_DETECTION_MESSAGE;
        message.obj = faces;
        message.sendToTarget();
    }
}

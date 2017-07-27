package work.petrichor.petrichor.modules;

import android.os.Handler;
import android.os.Message;

import java.util.List;

import work.petrichor.seeta.FaceInfo;

/**
 * @author Mitscherlich.
 * @date 2017/7/26.
 **/

public class SeetaFaceDetectionListener implements CameraInterface.FaceDetectionListener {

    private Handler mHandler;

    public SeetaFaceDetectionListener(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    public void onFaceDetection(List<FaceInfo> faces) {
        Message message = mHandler.obtainMessage();
        message.what = CameraInterface.ON_SEETA_FACE_DETECTION_MESSAGE;
        message.obj = faces;
        message.sendToTarget();
    }
}

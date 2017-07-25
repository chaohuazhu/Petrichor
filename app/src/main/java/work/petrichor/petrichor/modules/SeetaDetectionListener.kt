package work.petrichor.petrichor.modules

import android.os.Handler

import work.petrichor.facedetection.format.FaceInfo

/**
 * @author  Mitscherlich.
 * @date    2017/7/24.
 * **/

class SeetaDetectionListener(private var handler: Handler) : CameraInterface.FaceDetectionListener {

    override fun onFaceDetection(faces: Array<FaceInfo>) {
        // 将检测到的人脸数组返回
        val message = handler.obtainMessage()
        message.what = CameraInterface.ON_SEETA_FACE_DETECTION_MESSAGE
        message.obj = faces
        if (faces.isEmpty())
            android.util.Log.i("DetectionListener", "No face detected!")
        else
            android.util.Log.i("DetectionListener", "Detected ${faces.size} face.")
        message.sendToTarget()
    }
}
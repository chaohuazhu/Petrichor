package work.petrichor.petrichor.modules

import android.hardware.Camera
import android.os.Handler

/**
 * @author  Mitscherlich.
 * @date    2017/7/24.
 * **/
class GoogleFaceDetectionListener(private val handler: Handler) : Camera.FaceDetectionListener {

    override fun onFaceDetection(faces: Array<out Camera.Face>?, camera: Camera?) {
        val message = handler.obtainMessage()
        message.what = CameraInterface.ON_GOOGLE_FACE_DETECTION_MESSAGE
        message.obj = faces ?: emptyArray<Camera.Face>()
        message.sendToTarget()
    }
}
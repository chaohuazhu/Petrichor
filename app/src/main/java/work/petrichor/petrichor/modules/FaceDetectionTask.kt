package work.petrichor.petrichor.modules

import android.os.AsyncTask
import work.petrichor.facedetection.detection.Detector
import work.petrichor.facedetection.format.FaceInfo
import work.petrichor.facedetection.format.ImageData

/**
 * @author  Mitscherlich.
 * @date    2017/7/25.
 * **/
class FaceDetectionTask(private val modelPath: String) : AsyncTask<ImageData, Void, Array<FaceInfo>>() {

    override fun doInBackground(vararg frame: ImageData?): Array<FaceInfo> {
        return processFrame(frame[0])
    }

    private fun  processFrame(frame: ImageData?): Array<FaceInfo> {
        if (frame == null || frame.frameData.isEmpty()) return emptyArray()
        val detector = Detector(modelPath)
        val faces = detector.detect(frame) ?: emptyArray()
        return if (faces.isNotEmpty()) faces else emptyArray()
    }
}
package work.petrichor.petrichor.modules

import work.petrichor.facedetection.detection.Detector
import work.petrichor.facedetection.format.FaceInfo
import work.petrichor.facedetection.format.ImageData
import java.util.concurrent.LinkedBlockingDeque

/**
 * @author  Mitscherlich.
 * @date    2017/7/24.
 * **/
class FaceDetectionQueue(frameQueue: LinkedBlockingDeque<ImageData>, modelPath: String, listener: CameraInterface.FaceDetectionListener) : Thread() {

    private var mQueue: LinkedBlockingDeque<ImageData> = frameQueue
    private var mListener: CameraInterface.FaceDetectionListener = listener
    private var mModelPath: String = modelPath

    override fun run() {
        super.run()

        while (true) {
            val frame: ImageData = mQueue.take()
            processFrameData(frame)
        }
    }

    private fun processFrameData(frame: ImageData) {
        val detector = Detector(mModelPath)
        val faces: Array<FaceInfo> = detector.detect(frame) ?: emptyArray()
        mListener.onFaceDetection(faces)
    }

    init {
        start()
    }

}
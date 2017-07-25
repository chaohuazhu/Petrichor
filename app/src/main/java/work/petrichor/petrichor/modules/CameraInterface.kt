package work.petrichor.petrichor.modules

import android.graphics.ImageFormat
import android.hardware.Camera
import android.hardware.Camera.*
import android.os.AsyncTask
import android.view.SurfaceHolder
import work.petrichor.facedetection.format.FaceInfo
import work.petrichor.facedetection.format.ImageData
import java.util.concurrent.LinkedBlockingDeque

/**
 * @author  Mitscherlich.
 * @data    2017/7/24.
 */
object CameraInterface {

    val ON_SEETA_FACE_DETECTION_MESSAGE = 100
    val ON_GOOGLE_FACE_DETECTION_MESSAGE = 101

    private var mCamera: Camera? = null
    private var isPreview: Boolean = false
    private var modelPath: String = ""

    private lateinit var mTask: FaceDetectionTask

    private lateinit var mParams: Parameters
    private lateinit var faceDetectionQueue: FaceDetectionQueue

    private var frameQueue: LinkedBlockingDeque<ImageData> = LinkedBlockingDeque()
    fun openCamera(callback: CameraOpenCallback) {
        mCamera = Camera.open(getFrontCamera())
        callback.hasCameraOpened()
    }

    fun initParameters() {
        mParams = mCamera!!.parameters

        val pictureSize = getPictureSize(mParams)
        mParams.setPictureSize(pictureSize.width, pictureSize.height)

        val previewSize = getPreviewSize(mParams)
        mParams.setPreviewSize(previewSize.width, previewSize.height)
        mParams.previewFormat = ImageFormat.YV12

        mParams.focusMode = getFocusMode(mParams)

        mCamera!!.parameters = mParams
        mCamera!!.setDisplayOrientation(90)
    }

    /**
     * 设置预览视图句柄
     * @param holder 从 CameraView 获得的绘图句柄
     * **/
    fun setPreviewDisplay(holder: SurfaceHolder) {
        mCamera!!.setPreviewDisplay(holder)
    }

    /**
     * 设置 Seetaface 人脸检测监听对象
     * @param modelPath
     * @param listener
     * **/
    fun setDetectionListener(modelPath: String, listener: FaceDetectionListener) {
        faceDetectionQueue = FaceDetectionQueue(frameQueue, modelPath, listener)
//        this.modelPath = modelPath
//        mTask = FaceDetectionTask(modelPath)
        mCamera!!.setPreviewCallback({ data, _ ->
            if (data == null || data.isEmpty()) return@setPreviewCallback
            val frame = ImageData(data, mParams.previewSize.width, mParams.previewSize.height, ImageData.FRAME_FORMAT_YV12)
            frameQueue.put(frame)
//            if (mTask.status == AsyncTask.Status.RUNNING) return@setPreviewCallback
//            if (mTask.status == AsyncTask.Status.PENDING) mTask.cancel(false)
//            mTask = FaceDetectionTask(CameraInterface.modelPath)
//            val faces = mTask.execute(ImageData(data, mParams.previewSize.width, mParams.previewSize.height, ImageData.FRAME_FORMAT_YV12)).get()
//            listener.onFaceDetection(faces)
        })
    }

    /**
     * 设置 Google 人脸检测监听对象
     * @param listener
     * **/
    fun setDetectionListener(listener: Camera.FaceDetectionListener) {
        mCamera!!.setFaceDetectionListener(listener)
    }

    /**
     * 开始预览
     * **/
    fun startPreview() {
        if (isPreview)
            mCamera!!.stopPreview()

        mCamera!!.startPreview()
        isPreview = true

        // 先判断设备是否支持人脸检测
        if (mParams.maxNumDetectedFaces > 0)
            mCamera!!.startFaceDetection()
    }

    /**
     * 停止预览
     * **/
    fun stopPreview() {
        if (mCamera != null) {
            if (mParams.maxNumDetectedFaces > 0) {
                mCamera!!.stopFaceDetection()
                mCamera!!.setFaceDetectionListener(null)
            }

            mCamera!!.setPreviewCallback(null)
            mCamera!!.setPreviewDisplay(null)
            mCamera!!.stopPreview()
            mCamera!!.release()
            mCamera = null
            isPreview = false
        }
    }

    /**
     * 获取中间支持的图像 Size
     * @param params 相机参数
     * @return 中值拍照 Size
     * **/
    private fun getPictureSize(params: Parameters): Size {
        val pictureSize = params.supportedPictureSizes
        return pictureSize[0]
    }

    /**
     * 获取中间支持的预览 Size
     * @param params 相机参数
     * @return 中值预览 Size
     * **/
    private fun getPreviewSize(params: Parameters): Size {
        val previewSize = params.supportedPreviewSizes
        return previewSize[0]
    }

    /**
     * 获取自动对焦模式
     * @param params 相机参数
     * @return 预览性质
     * **/
    private fun getFocusMode(params: Parameters): String {
        val focusModes = params.supportedFocusModes

        return if (focusModes.contains("continuous-video"))
            Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
        else
            Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
    }

    private fun getFrontCamera(): Int = if (Camera.getNumberOfCameras() > 1) 1 else 0

    interface CameraOpenCallback {
        fun hasCameraOpened()
    }

    interface FaceDetectionListener {
        fun onFaceDetection(faces: Array<FaceInfo>)
    }
}
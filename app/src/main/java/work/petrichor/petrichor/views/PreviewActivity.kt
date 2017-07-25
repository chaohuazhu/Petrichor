package work.petrichor.petrichor.views

import android.app.Activity
import android.hardware.Camera
import android.os.Bundle
import android.os.Handler
import android.view.SurfaceHolder
import android.view.View
import android.widget.Toast
import work.petrichor.facedetection.format.FaceInfo

import work.petrichor.petrichor.R
import work.petrichor.petrichor.modules.CameraInterface
import work.petrichor.petrichor.modules.GoogleFaceDetectionListener
import work.petrichor.petrichor.modules.SeetaDetectionListener
import work.petrichor.petrichor.modules.utils.AssetUtils
import work.petrichor.petrichor.views.items.CameraView
import work.petrichor.petrichor.views.items.DrawableView

class PreviewActivity : Activity(), CameraInterface.CameraOpenCallback {

    private lateinit var mCameraView: CameraView
    private lateinit var mDrawableView: DrawableView
    private lateinit var mHolder: SurfaceHolder

    private val mHandler: Handler = Handler(Handler.Callback { message ->
        if (message.what == CameraInterface.ON_SEETA_FACE_DETECTION_MESSAGE) {
            val faces = message.obj as Array<FaceInfo>
            mDrawableView.setFaces(faces, faces.size)
        } else if (message.what == CameraInterface.ON_GOOGLE_FACE_DETECTION_MESSAGE) {
            val faces = message.obj as Array<Camera.Face>
            mDrawableView.setFaces(faces, faces.size)
        }; false
    })

//    private val mHandler: Handler = Handler(Handler.Callback { message ->
//        if (message.what == CameraInterface.ON_GOOGLE_FACE_DETECTION_MESSAGE) {
//            val faces = message.obj as Array<Camera.Face>
//            mDrawableView.setFaces(faces, faces.size)
//        }; false
//    })

    override fun hasCameraOpened() {
        CameraInterface.initParameters()
        CameraInterface.setPreviewDisplay(mHolder)
        CameraInterface.setDetectionListener(GoogleFaceDetectionListener(mHandler))
        CameraInterface.setDetectionListener(getExternalFilesDir(null).path + "/seeta_fd_frontal_v1.0.bin", SeetaDetectionListener(mHandler))
        CameraInterface.startPreview()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        if (!AssetUtils.isAssetFileExist(applicationContext, "seeta_fd_frontal_v1.0.bin"))
            Toast.makeText(applicationContext, "未找到模型，请检查权限", Toast.LENGTH_SHORT).show()
        else initView()
    }

    /**
     * 离开视图时退出预览
     * **/
    override fun onPause() {
        super.onPause()
        finish()
    }

    fun initView() {
        mCameraView = findViewById(R.id.sfvPreview)
        mDrawableView = findViewById(R.id.sfvDrawable)
        mHolder = mCameraView.getSurfaceHolder()

        Thread(Runnable {
            CameraInterface.openCamera(this@PreviewActivity)
        }).start()
    }

    /**
     * 关闭窗口，停止预览
     * **/
    fun View.doStopPreview() = finish()
}

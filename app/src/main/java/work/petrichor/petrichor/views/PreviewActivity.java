package work.petrichor.petrichor.views;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.View;

import java.util.List;

import work.petrichor.petrichor.R;
import work.petrichor.petrichor.modules.CameraInterface;
import work.petrichor.petrichor.modules.GoogleFaceDetectionListener;
import work.petrichor.petrichor.modules.SeetaFaceDetectionListener;
import work.petrichor.petrichor.views.items.CameraView;
import work.petrichor.petrichor.views.items.DrawView;
import work.petrichor.seeta.FaceInfo;

public class PreviewActivity extends Activity implements CameraInterface.CameraOpenCallback {

    private DrawView mDrawView;
    private SurfaceHolder mHolder;

    private Handler mSeetaHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {

            if (message.what == CameraInterface.ON_SEETA_FACE_DETECTION_MESSAGE) {
                List<FaceInfo> faces = (List<FaceInfo>) message.obj;
                mDrawView.setFaces(faces, faces.size());
            }
            return false;
        }
    });

    private Handler mGoogleHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == CameraInterface.ON_GOOGLE_FACE_DETECTION_MESSAGE) {
                Camera.Face[] faces = (Camera.Face[]) message.obj;
                mDrawView.setFaces(faces, faces.length);
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void initView() {
        CameraView mCameraView = findViewById(R.id.sfvPreview);
        mDrawView = findViewById(R.id.viewDraw);
        mHolder = mCameraView.getHolder();

        new Thread(new Runnable() {
            @Override
            public void run() {
                CameraInterface.getInstance().openCamera(PreviewActivity.this);
            }
        }).start();
    }

    @Override
    public void hasCameraOpened() {
        // 触发此回调，说明相机已打开
        CameraInterface.getInstance().initParameters();
        CameraInterface.getInstance().setPreviewDisplay(mHolder);
        CameraInterface.getInstance().setFaceDetectionTask(getExternalFilesDir(null).getPath() + "/seeta_fd_frontal_v1.0.bin");
        CameraInterface.getInstance().setPreviewListener(new SeetaFaceDetectionListener(mSeetaHandler));
        CameraInterface.getInstance().setFaceDetectionListener(new GoogleFaceDetectionListener(mGoogleHandler));
        CameraInterface.getInstance().startPreview();
    }

    public void doSopPreview(View view) {
        finish();
    }
}

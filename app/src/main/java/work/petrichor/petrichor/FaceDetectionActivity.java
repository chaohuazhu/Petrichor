package work.petrichor.petrichor;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import work.petrichor.petrichor.ui.camera.CameraSourcePreview;
import work.petrichor.petrichor.ui.camera.GraphicOverlay;
import work.petrichor.vision.CameraSource;

public class FaceDetectionActivity extends Activity {

    private static final String TAG = "FaceDetection";

    private CameraSource mCameraSource = null;

    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;

    private static final int RC_HANDLE_CAMERA_PREM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);

        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.faceOverlay);

        // Check for the camera permission before accessing the camera. If the permission is not
        // granted yet, require permission.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int rc = checkSelfPermission(Manifest.permission.CAMERA);
            if (rc == PackageManager.PERMISSION_GRANTED)
                createCameraSource();
            else
                requestCameraPermission();

            Log.i(TAG, String.format("View size is %d * %d", mPreview.getWidth(), mPreview.getHeight()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null)
            mCameraSource.release();
    }


    /**
     * Callback for the result from requesting permissions. This method is invoked for every call on
     * {@link #requestPermissions(String[], int)}.
     * Note: It's possible that the permission request interaction with the user is interrupted. In
     * this case you will receive empty permissions and results arrays which should be treated as
     * cancellation.
     *
     * @param requestCode   The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions   The requested permissions. Never null.
     * @param grantResults  The grant results for the corresponding permissions which is either
     *                      {@link PackageManager#PERMISSION_GRANTED} or
     *                      {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     * **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PREM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // permission granted, then create the camera source
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) { finish(); }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Detection Demo")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    public void reverseCamera(View view) {
        if (mCameraSource != null) {
            mCameraSource.stop();
        }
    }

    public void stopPreview(View view) {
        finish();
    }

    private void createCameraSource() {
        mCameraSource = new CameraSource.Builder(this)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setPreviewSize(640, 360)
                .build();
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission.");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
                requestPermissions(new String[] {
                        Manifest.permission.CAMERA
                }, RC_HANDLE_CAMERA_PREM);
    }

    private void startCameraSource() {
        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }
}

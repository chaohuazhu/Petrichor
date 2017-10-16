package work.petrichor.petrichor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class JumpActivity extends AppCompatActivity {

    private static final String TAG = "JumpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jump);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 判断返回的 `data` 是否包含数据
        if (null == data || null == data.getExtras()) return;

        String imageBase = null;

        try {
            imageBase = data.getExtras().getString("image_base");
        } catch (Exception e) {
            Log.e(TAG, "Intent result receive error!\n" + e);
        }

        if (null == imageBase || imageBase.isEmpty())
            return;

        Intent intent = getIntent();
        intent.putExtra("image_base", imageBase);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void startPreview(View view) {
        startActivityForResult(new Intent(this, FaceDetectionActivity.class), MainActivity.USER_SIGN_IN_ACTION);
    }
}

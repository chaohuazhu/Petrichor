package work.petrichor.petrichor.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

import work.petrichor.petrichor.R;
import work.petrichor.petrichor.modules.utils.AssetUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            AssetUtils.copy(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doPreview(View view) {
        startActivity(new Intent(this, PreviewActivity.class));
    }
}

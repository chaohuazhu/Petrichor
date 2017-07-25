package work.petrichor.petrichor.views

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import work.petrichor.petrichor.R

import work.petrichor.petrichor.modules.utils.AssetUtils

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Thread(Runnable { if (!AssetUtils.copy(applicationContext))
            Toast.makeText(applicationContext, "无法拷贝模型，请检查权限！", Toast.LENGTH_LONG).show()
        }).start()
    }

    fun View.doStartPreview() = startActivity(Intent(this@MainActivity, PreviewActivity::class.java))
}

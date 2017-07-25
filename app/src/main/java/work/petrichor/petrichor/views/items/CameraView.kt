package work.petrichor.petrichor.views.items

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

import work.petrichor.petrichor.modules.CameraInterface

/**
 * @author  Mitscherlich.
 * @data    2017/7/24.
 */
class CameraView : SurfaceView, SurfaceHolder.Callback {

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        CameraInterface.stopPreview()
    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
    }

    private lateinit var mHolder: SurfaceHolder

    fun getSurfaceHolder() : SurfaceHolder = mHolder

    constructor(context: Context?) : super(context) { init() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { init() }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init() }

    private fun init() {
        mHolder = holder
        mHolder.addCallback(this)
    }
}
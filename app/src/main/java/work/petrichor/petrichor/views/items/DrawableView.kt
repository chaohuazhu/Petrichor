package work.petrichor.petrichor.views.items

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.hardware.Camera
import android.util.AttributeSet
import android.view.View

import work.petrichor.facedetection.format.FaceInfo

/**
 * @author  Mitscherlich.
 * @date    2017/7/24.
 * **/
class DrawableView : View {

    private lateinit var mPaintRed: Paint
    private lateinit var mPaintBlue: Paint
    private var mSeetaFaces: Array<FaceInfo> = emptyArray()
    private var mGoogleFaces: Array<Camera.Face> = emptyArray()
    private var numSeetaFace = 0
    private var numGoogleFace = 0
    private var mMatrix: Matrix = Matrix()

    private var mRectF: RectF = RectF()

    constructor(context: Context?) : super(context) {
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        // 初始化 Seetaface 检测画笔
        mPaintRed = Paint()
        mPaintRed.color = Color.RED
        mPaintRed.style = Paint.Style.STROKE
        mPaintRed.strokeWidth = 5f

        // 初始化 Google 检测画笔
        mPaintBlue = Paint()
        mPaintBlue.color = Color.BLUE
        mPaintBlue.textSize = 45f
        mPaintBlue.style = Paint.Style.STROKE
        mPaintBlue.strokeWidth = 5f
    }

    fun setFaces(faces: Array<FaceInfo>, numFace: Int) {
        mSeetaFaces = faces
        this.numSeetaFace = numFace
        invalidate() // 刷新视图
    }

    fun setFaces(faces: Array<Camera.Face>, numFace: Int) {
        mGoogleFaces = faces
        this.numGoogleFace = numFace
        invalidate() // 刷新视图
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas!!.save()

        // 绘制 seetaface 检测结果
        if (mSeetaFaces.isNotEmpty()) {
            mSeetaFaces.forEach { face ->
                mRectF.set(prepareRect(face))
                canvas.drawRect(mRectF, mPaintRed)
            }
        }

        if (mGoogleFaces.isNotEmpty()) {
            prepareMatrix(mMatrix, true, 90, width, height)
            mMatrix.postRotate(0f)
            canvas.rotate(-0f)

            // 绘制 Google 检测结果
            mGoogleFaces.forEach { face ->
                mRectF.set(face.rect)
                mMatrix.mapRect(mRectF)

                canvas.drawRect(mRectF, mPaintBlue)
                canvas.drawText("Google", mRectF.left + 20f, mRectF.top - 40f, mPaintBlue)
            }
        }

        canvas.restore()
    }

    /**
     * 将外部库提供的 FaceInfo 转换为 android.graphics.Rect
     * @param face
     * @return
     * **/
    private fun prepareRect(face: FaceInfo): Rect = Rect(face.x, face.y, face.width, face.height)

    /**
     * 转换与预览视图匹配的绘图矩阵
     * @param matrix
     * @param isMirror
     * @param displayOrientation
     * @param viewWidth
     * @param viewHeight
     * **/
    private fun prepareMatrix(matrix: Matrix, isMirror: Boolean, displayOrientation: Int, viewWidth: Int, viewHeight: Int) {
        matrix.setScale(if (isMirror) -1f else 1f, 1f)
        matrix.postRotate(displayOrientation.toFloat())
        matrix.postScale(viewWidth / 2000f, viewHeight / 2000f)
        matrix.postTranslate(viewWidth / 2f, viewHeight / 2f)
    }
}
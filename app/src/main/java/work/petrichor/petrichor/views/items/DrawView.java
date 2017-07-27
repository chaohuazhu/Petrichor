package work.petrichor.petrichor.views.items;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import work.petrichor.petrichor.modules.CameraInterface;
import work.petrichor.seeta.FaceInfo;

/**
 * @author Mitscherlich.
 * @date 2017/7/26.
 **/

public class DrawView extends View {

	private Paint mPaintRed;
	private List<FaceInfo> mSeetaFaces;
	private int seetaFaceNum = 0;

	private Paint mPaintBlue;
	private Camera.Face[] mGoogleFaces;
	private int googleFaceNum = 0;

	private RectF mRectF;
	private Matrix mMatrix;

	public void setFaces(List<FaceInfo> faces, int numFace) {
		mSeetaFaces = faces;
		this.seetaFaceNum = numFace;
		invalidate();
	}

	public void setFaces(Camera.Face[] faces, int numFace) {
		mGoogleFaces = faces;
		this.googleFaceNum = numFace;
		invalidate();
	}

	public DrawView(Context context) {
		super(context);
		init();
	}

	public DrawView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		mRectF = new RectF();
		mMatrix = new Matrix();

		mPaintRed = new Paint();
		mPaintRed.setColor(Color.RED);
		mPaintRed.setStyle(Paint.Style.STROKE);
		mPaintRed.setStrokeWidth(5f);

		mPaintBlue = new Paint();
		mPaintBlue.setColor(Color.BLUE);
		mPaintBlue.setStyle(Paint.Style.STROKE);
		mPaintBlue.setStrokeWidth(5f);

		mSeetaFaces = new ArrayList<>();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.save();

		if (!mSeetaFaces.isEmpty()) {
			// 绘制人脸矩形
			for (FaceInfo face : mSeetaFaces) {
				mRectF.set(prepareFaceRect(face, getWidth(), getHeight()));
                android.util.Log.i("DrawView", String.format("face size is (x: %d, y: %d, width: %d, height: %d)", face.x, face.y, face.width, face.height));
                android.util.Log.i("DrawView", String.format("view size is (width: %d, height: %d)", getWidth(), getHeight()));
				android.util.Log.i("DrawView", "RectF: { x: " + mRectF.left + ", y: " + mRectF.top + ", right: " + mRectF.right + ", bottom: " + mRectF.bottom + " }");
                canvas.drawRect(mRectF, mPaintRed);
			}
		}

        prepareGoogleMatrix(mMatrix, true, 90, getWidth(), getHeight());
		if (mGoogleFaces != null && mGoogleFaces.length > 0)
            // 绘制人脸矩形
            for (Camera.Face face : mGoogleFaces) {
		        android.util.Log.i("DrawView", "Google detected a face.");
                mRectF.set(face.rect);
                mMatrix.mapRect(mRectF);
                canvas.drawRect(mRectF, mPaintBlue);
            }

		canvas.restore();
	}

	private RectF prepareFaceRect(FaceInfo face, int viewWidth, int viewHeight) {
	    float left = face.x * viewWidth / 360f;
	    float top = face.y * viewHeight / 640f;
	    int width = face.width * viewWidth / 360;
	    int height = face.height * viewHeight / 640;
		return new RectF(left, top, left + width, top + height);
	}

	private void prepareGoogleMatrix(Matrix matrix, boolean isMirror, int displayOrientation, int viewWidth, int viewHeight) {
		matrix.setScale(isMirror ? -1f : 1f, 1f);
		matrix.postRotate(displayOrientation);
		matrix.postScale(viewWidth / 2000f, viewHeight / 2000f);
		matrix.postTranslate(viewWidth / 2f, viewHeight / 2f);
	}
}

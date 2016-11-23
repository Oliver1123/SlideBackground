package oliver.com.slidebackground;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by zolotar on 23/11/16.
 */

public class SlideBackgroundView extends View {
    private static final int DEFAULT_UPDATE_DELAY = 1000 / 30;
    private static final String TAG = SlideBackgroundView.class.getSimpleName();

    private static final int DEFAULT_ANIMATION_SPEED = 5;
    private static final int MSG_INVALIDATE = 0;

    private int mRedrawDelay;
    private int[] mImagesId;

    private Bitmap mLeftImage;
    private Bitmap mRightImage;

    private int mFirstDisplayedImageIndex = -1;


    private int mElapsedTime;
    private int mAnimationSpeed;

    private boolean mAnimationStarted;
    private final Handler mInvalidateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mElapsedTime += mAnimationSpeed;
            invalidate();
            sendEmptyMessageDelayed(MSG_INVALIDATE, mRedrawDelay);
        }
    };
    private int mImageWidth, mImageHeight;
    private Paint mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private Matrix mLeftImageMatrix, mRightImageMatrix;
    private float mScaleX;
    private float mScaleY;
    private int mViewWidth;
    private int mViewHeight;


    public SlideBackgroundView(Context context) {
        super(context);
        init();
    }

    public SlideBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SlideBackgroundView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mViewWidth = getWidth();
        mViewHeight = getHeight();
    }

    private void init() {
        mRedrawDelay = DEFAULT_UPDATE_DELAY;
        mAnimationSpeed = DEFAULT_ANIMATION_SPEED;
        mLeftImageMatrix = new Matrix();
        mRightImageMatrix = new Matrix();
    }


    public void startAnimation(boolean restart) {
        Log.d(TAG, "startAnimation: ");
        mAnimationStarted = true;
        if (restart) mElapsedTime = 0;
        mInvalidateHandler.removeMessages(MSG_INVALIDATE);
        mInvalidateHandler.sendEmptyMessage(MSG_INVALIDATE);
    }

    public void pauseAnimation() {
        Log.d(TAG, "pauseAnimation: ");
        mAnimationStarted = false;
        mInvalidateHandler.removeMessages(MSG_INVALIDATE);
    }

    public boolean isAnimationStarted() {
        return mAnimationStarted;
    }

    public void setAnimatedImages(@DrawableRes int... imageID) {
        mImagesId = imageID;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        updateImageIndex();
        recalcMatrix();
        canvas.drawBitmap(mLeftImage, mLeftImageMatrix, mBitmapPaint);
        canvas.drawBitmap(mRightImage, mRightImageMatrix, mBitmapPaint);
        Log.d(TAG, "onDraw: time: " + mElapsedTime);
    }

    private void recalcMatrix() {

        mScaleY = mViewHeight / (float)mImageHeight;
        mScaleX = mViewWidth / (float)mImageWidth;

        Log.d(TAG, "recalcMatrix: scale x: " + mScaleX + " y: " + mScaleY);
        mLeftImageMatrix.reset();
        mLeftImageMatrix.setScale(mScaleX, mScaleY);
        mLeftImageMatrix.postTranslate(-mElapsedTime % mViewWidth, 0);

        //
        mRightImageMatrix.reset();
        mRightImageMatrix.postScale(mScaleX, mScaleY);
        mRightImageMatrix.postTranslate(mViewWidth - mElapsedTime % mViewWidth, 0);
    }


    private void updateImageIndex() {
        int tmpIndex = (mElapsedTime / mViewWidth);
        tmpIndex = tmpIndex % mImagesId.length;
        if (tmpIndex != mFirstDisplayedImageIndex) {
            mFirstDisplayedImageIndex = tmpIndex;
            updateDisplayedImages();
        }
    }

    private void updateDisplayedImages() {
        Log.d(TAG, "updateDisplayedImages: mFirstDisplayedImageIndex : " + mFirstDisplayedImageIndex);

        if (mLeftImage != null) {
            mLeftImage.recycle();
        }

        if (mRightImage != null) { // use the right image
            mLeftImage = mRightImage;
        } else {
            mLeftImage = BitmapFactory.decodeResource(getResources(), mImagesId[mFirstDisplayedImageIndex]);
        }

        mRightImage = BitmapFactory.decodeResource(getResources(), mImagesId[(mFirstDisplayedImageIndex + 1) % mImagesId.length]);
        mLeftImage = getCroppedBitmap(mLeftImage, mViewWidth/ (float)mViewHeight);
        mRightImage = getCroppedBitmap(mRightImage, mViewWidth/ (float)mViewHeight);
        mImageWidth = mLeftImage.getWidth();
        mImageHeight = mLeftImage.getHeight();
        System.gc();
    }

    /**
     * crop given bitmap with given dstAspectRatio scaleType center crop
     * @param frame
     * @param dstAspectRatio
     * @return
     */
    private static Bitmap getCroppedBitmap(Bitmap frame, float dstAspectRatio) {
        int frameW = frame.getWidth();
        int frameH = frame.getHeight();
        int outputW;
        int outputH;
        float frameAspectRatio = frameW / (float) frameH;
        if (frameAspectRatio == dstAspectRatio) return frame;


        if (isPortrait(frameAspectRatio)) {
            Log.d(TAG, "getCroppedBitmap: frame isPortrait");
            if (frameAspectRatio > dstAspectRatio) {
                outputH = frameH;
                outputW = (int)(frameH * dstAspectRatio);
            } else {
                outputW = frameW;
                outputH = (int)(frameW / dstAspectRatio);
            }
        } else {
            Log.d(TAG, "getCroppedBitmap: frame is Landscape");
            outputH = frameH;
            outputW = (int)(frameH * dstAspectRatio);
        }
        int startX = frameW / 2 - outputW / 2;
        int startY = frameH / 2 - outputH / 2;
        Bitmap result = Bitmap.createBitmap(frame, startX, startY, outputW, outputH);
        Log.d(TAG, "getCroppedBitmap: outputSizes w: " + outputW + " h: " + outputH);

        frame.recycle();
        return result;
    }

    private static boolean isPortrait(float aspectRatio) {
        return aspectRatio <= 1;
    }
}

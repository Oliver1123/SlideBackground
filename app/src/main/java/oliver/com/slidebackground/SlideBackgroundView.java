package oliver.com.slidebackground;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
    private static final int DEFAULT_UPDATE_DELAY = 1000 / 24;
    private static final String TAG = SlideBackgroundView.class.getSimpleName();

    private static final int DEFAULT_ANIMATION_SPEED = 2;
    private static final int MSG_INVALIDATE = 0;

    private int mRedrawDelay;
    private int[] mImageId;

    private Bitmap mLeftImage;
    private Bitmap mRightImage;

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

    private void init() {
        mRedrawDelay = DEFAULT_UPDATE_DELAY;
        mAnimationSpeed = DEFAULT_ANIMATION_SPEED;
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
        mImageId = imageID;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d(TAG, "onDraw: time: " + mElapsedTime);
    }
}

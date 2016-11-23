package oliver.com.slidebackground;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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

    private int mUpdateDelay;
    private int[] mImageId;

    private Bitmap mLeftImage;
    private Bitmap mRightImage;

    private boolean mAnimationStarted;


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
        mUpdateDelay = DEFAULT_UPDATE_DELAY;
    }


    public void startAnimation() {
        mAnimationStarted = true;
    }

    public void pauseAnimation() {
        mAnimationStarted = false;
    }

    public void setAnimatedImages(@DrawableRes int... imageID) {
        mImageId = imageID;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d(TAG, "onDraw: ");
        if (mAnimationStarted)
            postInvalidateDelayed(mUpdateDelay);
    }
}

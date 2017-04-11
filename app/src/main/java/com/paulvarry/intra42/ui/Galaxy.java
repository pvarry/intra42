package com.paulvarry.intra42.ui;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Scroller;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.ProjectDataIntra;

import java.util.List;

public class Galaxy extends View {

    private float weightPath;
    private int backgroundColor;
    private int colorPath;
    private int colorProjectUnavailable;
    private int colorProjectAvailable;
    private int colorProjectValidated;
    private int colorProjectFinish;
    private List<ProjectDataIntra> data;
    private GestureDetector mGestureDetector;
    private Scroller mScroller;
    private ValueAnimator mScrollAnimator;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 0.5f;

    private Paint mPaintBackground;
    private Paint mPaintPath;
    private Paint mPaintProjectUnavailable;
    private Paint mPaintProjectAvailable;
    private Paint mPaintProjectValidated;
    private Paint mPaintProjectFinish;
    private Paint mPaintInternshipCircle;
    private int posY = 0;
    private int posX = 0;

    public Galaxy(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Galaxy,
                0, 0);

        try {
            backgroundColor = attributes.getColor(R.styleable.Galaxy_backgroundColor, 0);
            colorPath = attributes.getColor(R.styleable.Galaxy_colorPath, 0);
            colorProjectUnavailable = attributes.getColor(R.styleable.Galaxy_colorProjectUnavailable, 0);
            colorProjectAvailable = attributes.getColor(R.styleable.Galaxy_colorProjectAvailable, 0);
            colorProjectValidated = attributes.getColor(R.styleable.Galaxy_colorProjectValidated, 0);
            colorProjectFinish = attributes.getColor(R.styleable.Galaxy_colorProjectFinish, 0);
            weightPath = attributes.getDimension(R.styleable.Galaxy_weightPath, 1.f);

        } finally {
            attributes.recycle();
        }

        init();
    }

    private void init() {
        mPaintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBackground.setColor(backgroundColor);
        mPaintBackground.setStyle(Paint.Style.FILL);

        mPaintPath = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintPath.setAntiAlias(true);
        mPaintPath.setColor(colorPath);
        mPaintPath.setStrokeWidth(weightPath);

        mPaintProjectUnavailable = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProjectUnavailable.setAntiAlias(true);
        mPaintProjectUnavailable.setColor(colorProjectUnavailable);

        mPaintProjectAvailable = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProjectAvailable.setAntiAlias(true);
        mPaintProjectAvailable.setColor(colorProjectAvailable);
        mPaintProjectAvailable.setStyle(Paint.Style.FILL);

        mPaintProjectValidated = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProjectValidated.setAntiAlias(true);
        mPaintProjectValidated.setColor(colorProjectValidated);

        mPaintProjectFinish = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProjectFinish.setAntiAlias(true);
        mPaintProjectFinish.setColor(colorProjectFinish);

        mPaintInternshipCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintInternshipCircle.setAntiAlias(true);
        mPaintInternshipCircle.setColor(colorProjectAvailable);
        mPaintInternshipCircle.setStyle(Paint.Style.STROKE);

        // Create a gesture detector to handle onTouch messages
        mGestureDetector = new GestureDetector(this.getContext(), new GestureListener());

        // The scroller doesn't have any built-in animation functions--it just supplies
        // values when we ask it to. So we have to have a way to call it every frame
        // until the fling ends. This code (ab)uses a ValueAnimator object to generate
        // a callback on every animation frame. We don't use the animated value at all.
        mScrollAnimator = ValueAnimator.ofFloat(0, 1);
        mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                tickScrollAnimation();
            }
        });

        mScroller = new Scroller(getContext(), null, true);

        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    private void tickScrollAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.computeScrollOffset();
//            posX = mScroller.getCurrX();
            posY = mScroller.getCurrY();
        } else {
            mScrollAnimator.cancel();
            onScrollFinished();
        }
    }

    /**
     * Called when the user finishes a scroll action.
     */
    private void onScrollFinished() {
        decelerate();
    }

    /**
     * Disable hardware acceleration (releases memory)
     */
    public void decelerate() {
        setLayerToSW(this);
    }

    private void setLayerToSW(View v) {
        if (!v.isInEditMode()) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public void setData(List<ProjectDataIntra> data) {
        this.data = data;
        invalidate();
        requestLayout();
    }

    /**
     * Enable hardware acceleration (consumes memory)
     */
    public void accelerate() {
        setLayerToHW(this);
    }

    private void setLayerToHW(View v) {
        if (!v.isInEditMode()) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Let the GestureDetector interpret this event
        boolean resultGesture = mGestureDetector.onTouchEvent(event);
        boolean resultScale = mScaleDetector.onTouchEvent(event);

        // If the GestureDetector doesn't want this event, do some custom processing.
        // This code just tries to detect when the user is done scrolling by looking
        // for ACTION_UP events.
        if (!resultGesture && !resultScale) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // User is done scrolling, it's now safe to do things like autocenter
                stopScrolling();
                resultGesture = true;
            }
        }

        return resultGesture;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Rect c = canvas.getClipBounds();

        canvas.drawRect(c, mPaintBackground);

        if (data == null)
            return;

        mPaintPath.setStrokeWidth(weightPath * mScaleFactor);
        mPaintInternshipCircle.setStrokeWidth(10 * mScaleFactor);

        canvas.drawCircle(
                getDrawPosX(3000),
                getDrawPosY(3000),
                1000 * mScaleFactor,
                mPaintInternshipCircle);

        for (ProjectDataIntra projectData : data) {
            if (projectData.by != null)
                for (ProjectDataIntra.By by : projectData.by) {

                    float startX = getDrawPosX(by.points.get(0).get(0));
                    float startY = getDrawPosY(by.points.get(0).get(1));
                    float stopX = getDrawPosX(by.points.get(1).get(0));
                    float stopY = getDrawPosY(by.points.get(1).get(1));

                    canvas.drawLine(startX, startY, stopX, stopY, mPaintPath);
                }
        }

        for (ProjectDataIntra projectData : data) {
            canvas.drawCircle(
                    getDrawPosX(projectData.x),
                    getDrawPosY(projectData.y),
                    50 * mScaleFactor,
                    mPaintProjectAvailable);
        }

        tickScrollAnimation();
        if (!mScroller.isFinished()) {
            postInvalidate();
        }
    }

    public void setBackgroundColor(int color) {
        backgroundColor = color;
        invalidate();
        requestLayout();
    }

    float getDrawPosX(float pos) {
        return posX + (pos - 3000) * mScaleFactor;
    }

    float getDrawPosY(float pos) {
        return posY + (pos - 3000) * mScaleFactor;
    }

    /**
     * Force a stop to all pie motion. Called when the user taps during a fling.
     */
    private void stopScrolling() {
        mScroller.forceFinished(true);

        onScrollFinished();
    }

    private boolean isAnimationRunning() {
        return !mScroller.isFinished();
    }

    /**
     * Extends {@link GestureDetector.SimpleOnGestureListener} to provide custom gesture
     * processing.
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            posX -= distanceX;
            posY -= distanceY;
            invalidate();
            requestLayout();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mScroller.fling(posX, posY, (int) velocityX, (int) velocityY, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

            // Start the animator and tell it to animate for the expected duration of the fling.
            mScrollAnimator.setDuration(mScroller.getDuration());
            mScrollAnimator.start();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            // The user is interacting with the pie, so we want to turn on acceleration
            // so that the interaction is smooth.
            accelerate();
            if (isAnimationRunning()) {
                stopScrolling();
            }
            return true;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.001f, Math.min(mScaleFactor, 5.0f));

            invalidate();
            return true;
        }
    }
}
package com.paulvarry.intra42.ui;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
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

    ProjectDataIntra projectDataFirstInternship = null;
    ProjectDataIntra projectDataFinalInternship = null;
    private float weightPath;
    private int backgroundColor;
    private int colorProjectUnavailable;
    private int colorProjectAvailable;
    private int colorProjectValidated;
    private int textColor;
    private int colorProjectInProgress;
    private int colorProjectFailed;
    private List<ProjectDataIntra> data;
    private GestureDetector mGestureDetector;
    private Scroller mScroller;
    private ValueAnimator mScrollAnimator;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 0.5f;
    private Paint mPaintBackground;
    private Paint mPaintPath;
    private Paint mPaintProject;
    private Paint mPaintText;
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
            colorProjectUnavailable = attributes.getColor(R.styleable.Galaxy_colorProjectUnavailable, 0);
            colorProjectAvailable = attributes.getColor(R.styleable.Galaxy_colorProjectAvailable, 0);
            colorProjectValidated = attributes.getColor(R.styleable.Galaxy_colorProjectValidated, 0);
            colorProjectFailed = attributes.getColor(R.styleable.Galaxy_colorProjectFailed, 0);
            colorProjectInProgress = attributes.getColor(R.styleable.Galaxy_colorProjectInProgress, 0);
            textColor = attributes.getColor(R.styleable.Galaxy_textColor, 0);
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
        mPaintPath.setColor(colorProjectUnavailable);
        mPaintPath.setStrokeWidth(weightPath);
        mPaintPath.setStyle(Paint.Style.STROKE);

        mPaintProject = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProject.setAntiAlias(true);
        mPaintProject.setColor(colorProjectUnavailable);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(textColor);
        mPaintText.setFakeBoldText(true);


        // Create a Scroller to handle the fling gesture.
        mScroller = new Scroller(getContext(), null, true);

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

        // Create a gesture detector to handle onTouch messages
        mGestureDetector = new GestureDetector(this.getContext(), new GestureListener());

        // Turn off long press--this control doesn't use it, and if long press is enabled,
        // you can't scroll for a bit, pause, then scroll some more (the pause is interpreted
        // as a long press, apparently)
        mGestureDetector.setIsLongpressEnabled(false);

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

        for (ProjectDataIntra projectData : data) {

            if (projectData.kind == ProjectDataIntra.Kind.first_internship)
                projectDataFirstInternship = projectData;
            else if (projectData.kind == ProjectDataIntra.Kind.second_internship)
                projectDataFinalInternship = projectData;
        }

        if (projectDataFirstInternship != null) {
            projectDataFirstInternship.x = 3680;
            projectDataFirstInternship.y = 3750;
        }
        if (projectDataFinalInternship != null) {
            projectDataFinalInternship.x = 4600;
            projectDataFinalInternship.y = 4600;
        }

        onScrollFinished();
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

        mPaintProject.setStrokeWidth(weightPath * mScaleFactor);
        mPaintPath.setStrokeWidth(weightPath * mScaleFactor);
        mPaintText.setTextSize(25 * mScaleFactor);

        // draw projects path
        for (ProjectDataIntra projectData : data) {
            if (projectData.by != null)
                for (ProjectDataIntra.By by : projectData.by) {

                    float startX = getDrawPosX(by.points.get(0).get(0));
                    float startY = getDrawPosY(by.points.get(0).get(1));
                    float stopX = getDrawPosX(by.points.get(1).get(0));
                    float stopY = getDrawPosY(by.points.get(1).get(1));

                    canvas.drawLine(startX, startY, stopX, stopY, getColorPath(projectData));
                }
        }

        canvas.drawCircle(
                getDrawPosX(3000),
                getDrawPosY(3000),
                1000 * mScaleFactor,
                getColorPath(projectDataFirstInternship));
        canvas.drawCircle(
                getDrawPosX(3000),
                getDrawPosY(3000),
                2250 * mScaleFactor,
                getColorPath(projectDataFinalInternship));

        // draw projects
        for (ProjectDataIntra projectData : data) {

            switch (projectData.kind) {
                case project:
                    drawProject(canvas, projectData, 60);
                    break;
                case big_project:
                    drawProject(canvas, projectData, 75);
                    break;
                case part_time:
                    drawProject(canvas, projectData, 150);
                    break;
                case first_internship:
                    drawProject(canvas, projectData, 100);
                    break;
                case second_internship:
                    drawProject(canvas, projectData, 100);
                    break;
                case exam:
                    drawProject(canvas, projectData, 75);
                    break;
                case piscine:
                    drawPiscine(canvas, projectData);
                    break;
                case rush:
                    drawRush(canvas, projectData);
                    break;
            }
        }

        if (mScrollAnimator.isRunning()) {
            tickScrollAnimation();
            postInvalidate();
        }
    }

    public void setBackgroundColor(int color) {
        backgroundColor = color;
        invalidate();
        requestLayout();
    }

    /**
     * Force a stop to all pie motion. Called when the user taps during a fling.
     */
    private void stopScrolling() {
        mScroller.forceFinished(true);

        onScrollFinished();
    }

    private float getDrawPosX(float pos) {
        return posX + (pos - 3000) * mScaleFactor;
    }

    private float getDrawPosY(float pos) {
        return posY + (pos - 3000) * mScaleFactor;
    }

    private float getDrawPosX(ProjectDataIntra projectData) {
        return posX + (projectData.x - 3000) * mScaleFactor;
    }

    private float getDrawPosY(ProjectDataIntra projectData) {
        return posY + (projectData.y - 3000) * mScaleFactor;
    }

    private Paint getColorProject(ProjectDataIntra projectData) {
        mPaintProject.setColor(getColor(projectData));

        return mPaintProject;
    }

    private void drawProject(Canvas canvas, ProjectDataIntra projectData, int size) {
        canvas.drawCircle(
                getDrawPosX(projectData.x),
                getDrawPosY(projectData.y),
                size * mScaleFactor,
                getColorProject(projectData));

        drawProjectTitle(canvas, projectData);
    }

    private void drawProjectTitle(Canvas canvas, ProjectDataIntra projectData) {
        float textWidth = mPaintText.measureText(projectData.name);
        canvas.drawText(
                projectData.name,
                getDrawPosX(projectData) - textWidth / 2,
                getDrawPosY(projectData) + mPaintText.getTextSize() / 2,
                mPaintText);
    }

    private void drawPiscine(Canvas canvas, ProjectDataIntra projectData) {

        float x = getDrawPosX(projectData.x);
        float y = getDrawPosY(projectData.y);

        float width = 250 * mScaleFactor;
        float height = 60 * mScaleFactor;

        float left = x - width / 2;
        float top = y + height / 2;
        float right = x + width / 2;
        float bottom = y - height / 2;

        canvas.drawRect(left, top, right, bottom, getColorProject(projectData));
        drawProjectTitle(canvas, projectData);
    }

    private void drawRush(Canvas canvas, ProjectDataIntra projectData) {

        float x = getDrawPosX(projectData.x);
        float y = getDrawPosY(projectData.y);

        float width = 180 * mScaleFactor;
        float height = 60 * mScaleFactor;

        float left = x - width / 2;
        float top = y + height / 2;
        float right = x + width / 2;
        float bottom = y - height / 2;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            canvas.drawRoundRect(left, top, right, bottom, 100, 100, getColorProject(projectData));
        else
            canvas.drawRect(left, top, right, bottom, getColorProject(projectData));

        drawProjectTitle(canvas, projectData);
    }

    private Paint getColorPath(ProjectDataIntra projectData) {
        mPaintPath.setColor(getColor(projectData));
        return mPaintPath;
    }

    private int getColor(ProjectDataIntra projectData) {

        if (projectData != null)
            switch (projectData.state) {
                case DONE:
                    return colorProjectValidated;

                case AVAILABLE:
                    return colorProjectAvailable;

                case IN_PROGRESS:
                    return colorProjectInProgress;

                case UNAVAILABLE:
                    return colorProjectUnavailable;

            }
        return colorProjectUnavailable;
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

            postInvalidate();
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
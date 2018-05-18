package com.paulvarry.intra42.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Scroller;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.ProjectDataIntra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Galaxy extends View {

    static int GRAPH_MAP_LIMIT_MIN = -2000;
    static int GRAPH_MAP_LIMIT_MAX = 2000;
    static int GRAPH_MAP_MIN = -3000;
    static int GRAPH_MAP_MAX = 2000;
    static int TEXT_HEIGHT = 25;
    private ProjectDataIntra projectDataFirstInternship = null;
    private ProjectDataIntra projectDataFinalInternship = null;
    private float weightPath;
    private int backgroundColor;
    private int colorProjectUnavailable;
    private int colorProjectAvailable;
    private int colorProjectValidated;
    private int colorProjectInProgress;
    private int colorProjectFailed;
    private int colorProjectTextUnavailable;
    private int colorProjectTextAvailable;
    private int colorProjectTextValidated;
    private int colorProjectTextInProgress;
    private int colorProjectTextFailed;
    /**
     * Data for current Galaxy.
     */
    @Nullable
    private List<ProjectDataIntra> data;
    private GestureDetector mGestureDetector;
    private Scroller mScroller;
    private ValueAnimator mScrollAnimator;
    private ScaleGestureDetector mScaleDetector;
    private String state;

    /**
     * Current scale factor.
     */
    private float mScaleFactor = 1f;
    private Paint mPaintBackground;
    private Paint mPaintPath;
    private Paint mPaintProject;
    private Paint mPaintText;
    private float posY;
    private float posX;

    /**
     * semiWidth is the height of the view divided by 2.
     */
    private float semiHeight;

    /**
     * semiWidth is the width of the view divided by 2.
     */
    private float semiWidth;

    /**
     * Compute title of each project once when data is added.
     * This split the title in multi line to display inside of the circle.
     */
    private SparseArray<List<String>> projectTitleComputed;

    /**
     * Save cache for project position. This is computed once per call of onDraw();
     */
    private SparseArray<DrawPos> drawPosComputed;

    /**
     * Listener for project clicks.
     */
    private OnProjectClickListener onClickListener;

    public Galaxy(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Galaxy,
                0, 0);

        try {
            int defaultTextColor = Color.parseColor("#3F51B5");
            backgroundColor = attributes.getColor(R.styleable.Galaxy_backgroundColor, 0);
            colorProjectUnavailable = attributes.getColor(R.styleable.Galaxy_colorProjectUnavailable, 0);
            colorProjectAvailable = attributes.getColor(R.styleable.Galaxy_colorProjectAvailable, 0);
            colorProjectValidated = attributes.getColor(R.styleable.Galaxy_colorProjectValidated, 0);
            colorProjectFailed = attributes.getColor(R.styleable.Galaxy_colorProjectFailed, 0);
            colorProjectInProgress = attributes.getColor(R.styleable.Galaxy_colorProjectInProgress, 0);
            colorProjectTextUnavailable = attributes.getColor(R.styleable.Galaxy_colorProjectTextUnavailable, defaultTextColor);
            colorProjectTextAvailable = attributes.getColor(R.styleable.Galaxy_colorProjectTextAvailable, defaultTextColor);
            colorProjectTextValidated = attributes.getColor(R.styleable.Galaxy_colorProjectTextValidated, defaultTextColor);
            colorProjectTextFailed = attributes.getColor(R.styleable.Galaxy_colorProjectTextFailed, defaultTextColor);
            colorProjectTextInProgress = attributes.getColor(R.styleable.Galaxy_colorProjectTextInProgress, defaultTextColor);
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
        mPaintText.setFakeBoldText(true);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(TEXT_HEIGHT * mScaleFactor);


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

        onUpdateData();
    }

    private void tickScrollAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.computeScrollOffset();
            posX = mScroller.getCurrX();
            posY = mScroller.getCurrY();
        } else {
            mScrollAnimator.cancel();
            onScrollFinished();
        }
    }

    private void onUpdateData() {
        if (data != null) {
            mPaintText.setTextSize(TEXT_HEIGHT * mScaleFactor);
            projectTitleComputed = new SparseArray<>(data.size());
            drawPosComputed = new SparseArray<>();
            for (ProjectDataIntra p : data) {
                projectTitleComputed.put(p.id, TextCalculator.split(p, mPaintText, mScaleFactor));
                drawPosComputed.put(p.id, new DrawPos());
            }
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

    public void setOnProjectClickListener(OnProjectClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setData(@Nullable List<ProjectDataIntra> data) {
        state = null;
        this.data = data;

        if (data != null) {
            for (ProjectDataIntra projectData : data) {

                if (projectData.kind == ProjectDataIntra.Kind.FIRST_INTERNSHIP)
                    projectDataFirstInternship = projectData;
                else if (projectData.kind == ProjectDataIntra.Kind.SECOND_INTERNSHIP)
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

            Collections.sort(data, new Comparator<ProjectDataIntra>() {
                @Override
                public int compare(ProjectDataIntra o1, ProjectDataIntra o2) {
                    if (o1.state == null || o2.state == null)
                        return 0;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        return Integer.compare(o1.state.getLayerIndex(), o2.state.getLayerIndex());
                    } else {
                        if (o1.state.getLayerIndex() > o2.state.getLayerIndex())
                            return 1;
                        else if (o1.state.getLayerIndex() == o2.state.getLayerIndex())
                            return 0;
                        else
                            return -1;
                    }
                }
            });
        }

        onUpdateData();
        onScrollFinished();
        invalidate();
    }

    public void setState(String state) {
        data = null;
        this.state = state;

        onScrollFinished();
        invalidate();
    }

    /**
     * Enable hardware acceleration (consumes memory)
     */
    public void accelerate() {
        setLayerToHW(this);
    }

    private void setLayerToHW(View v) {
  /*      if (!v.isInEditMode()) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }*/
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

    /**
     * Force a stop to all motion. Called when the user taps during a fling.
     */
    private void stopScrolling() {
        mScroller.forceFinished(true);

        onScrollFinished();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        semiHeight = h / 2;
        semiWidth = w / 2;

        posX = 0;
        posY = 0;

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPaint(mPaintBackground);

        if (data == null || data.isEmpty()) {
            data = null;
            if (state == null)
                state = getContext().getString(R.string.galaxy_not_found);

            mPaintText.setColor(colorProjectTextAvailable);
            mPaintText.setTextSize(50 * mScaleFactor);

            int width = canvas.getWidth();
            int height = canvas.getHeight();
            canvas.drawText(state, width / 2, height * 0.8f, mPaintText);
            return;
        }

        mPaintPath.setStrokeWidth(weightPath * mScaleFactor);
        mPaintText.setTextSize(TEXT_HEIGHT * mScaleFactor);

        // draw projects path
        float startX;
        float startY;
        float stopX;
        float stopY;
        for (ProjectDataIntra projectData : data) {
            if (projectData.by != null)
                for (ProjectDataIntra.By by : projectData.by) {

                    startX = getDrawPosX(by.points.get(0).get(0));
                    startY = getDrawPosY(by.points.get(0).get(1));
                    stopX = getDrawPosX(by.points.get(1).get(0));
                    stopY = getDrawPosY(by.points.get(1).get(1));

                    canvas.drawLine(startX, startY, stopX, stopY, getColorPath(projectData));
                }
        }

        if (projectDataFirstInternship != null)
            canvas.drawCircle(
                    getDrawPosX(3000),
                    getDrawPosY(3000),
                    1000 * mScaleFactor,
                    getColorPath(projectDataFirstInternship));
        if (projectDataFinalInternship != null)
            canvas.drawCircle(
                    getDrawPosX(3000),
                    getDrawPosY(3000),
                    2250 * mScaleFactor,
                    getColorPath(projectDataFinalInternship));

        // draw projects
        for (ProjectDataIntra projectData : data) {

            switch (projectData.kind) {
                case PISCINE:
                    drawPiscine(canvas, projectData);
                    break;
                case RUSH:
                    drawRush(canvas, projectData);
                    break;
                default:
                    drawProject(canvas, projectData);
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

    private Paint getPaintProject(ProjectDataIntra projectData) {
        mPaintProject.setColor(getColor(projectData));

        return mPaintProject;
    }

    private Paint getPaintProjectText(ProjectDataIntra projectData) {
        mPaintText.setColor(getColorText(projectData));

        return mPaintText;
    }

    private int getColor(ProjectDataIntra projectData) {

        if (projectData != null && projectData.state != null)
            switch (projectData.state) {
                case DONE:
                    return colorProjectValidated;
                case AVAILABLE:
                    return colorProjectAvailable;
                case IN_PROGRESS:
                    return colorProjectInProgress;
                case UNAVAILABLE:
                    return colorProjectUnavailable;
                case FAIL:
                    return colorProjectFailed;
            }
        return colorProjectUnavailable;
    }

    private int getColorText(ProjectDataIntra projectData) {

        if (projectData != null && projectData.state != null)
            switch (projectData.state) {
                case DONE:
                    return colorProjectTextValidated;
                case AVAILABLE:
                    return colorProjectTextAvailable;
                case IN_PROGRESS:
                    return colorProjectTextInProgress;
                case UNAVAILABLE:
                    return colorProjectTextUnavailable;
                case FAIL:
                    return colorProjectTextFailed;
            }
        return colorProjectTextUnavailable;
    }

    private void drawProject(Canvas canvas, ProjectDataIntra projectData) {

        canvas.drawCircle(
                getDrawPosX(projectData, true),
                getDrawPosY(projectData, true),
                projectData.kind.getRadius(mScaleFactor),
                getPaintProject(projectData));

        drawProjectTitle(canvas, projectData);
    }

    private void drawProjectTitle(Canvas canvas, ProjectDataIntra projectData) {
        Paint paintText = getPaintProjectText(projectData);
        List<String> textToDraw = projectTitleComputed.get(projectData.id);

        float textHeight = paintText.getTextSize();
        float posYStartDraw = getDrawPosY(projectData) - (textHeight * (textToDraw.size() - 1)) / 2;
        float heightTextDraw;

        for (int i = 0; i < textToDraw.size(); i++) {
            heightTextDraw = posYStartDraw + textHeight * i - (paintText.descent() + paintText.ascent()) / 2;
            canvas.drawText(
                    textToDraw.get(i),
                    getDrawPosX(projectData),
                    heightTextDraw,
                    paintText);
        }
    }

    private void drawPiscine(Canvas canvas, ProjectDataIntra projectData) {

        float x = getDrawPosX(projectData, true);
        float y = getDrawPosY(projectData, true);

        float width = projectData.kind.getWidth(mScaleFactor);
        float height = projectData.kind.getHeight(mScaleFactor);

        float left = x - width / 2;
        float top = y + height / 2;
        float right = x + width / 2;
        float bottom = y - height / 2;

        canvas.drawRect(left, top, right, bottom, getPaintProject(projectData));
        drawProjectTitle(canvas, projectData);
    }

    private void drawRush(Canvas canvas, ProjectDataIntra projectData) {

        float x = getDrawPosX(projectData, true);
        float y = getDrawPosY(projectData, true);

        float width = projectData.kind.getWidth(mScaleFactor);
        float height = projectData.kind.getHeight(mScaleFactor);

        float left = x - width / 2;
        float top = y + height / 2;
        float right = x + width / 2;
        float bottom = y - height / 2;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            canvas.drawRoundRect(left, top, right, bottom, 100, 100, getPaintProject(projectData));
        else
            canvas.drawRect(left, top, right, bottom, getPaintProject(projectData));

        drawProjectTitle(canvas, projectData);
    }

    private Paint getColorPath(ProjectDataIntra projectData) {
        mPaintPath.setColor(getColor(projectData));
        return mPaintPath;
    }

    private boolean isAnimationRunning() {
        return !mScroller.isFinished();
    }

    boolean ptInsideCircle(float x, float y, ProjectDataIntra projectData) {

        float projectCenterX = getDrawPosX(projectData);
        float projectCenterY = getDrawPosY(projectData);

        double distanceBetween = Math.pow(x - projectCenterX, 2) + Math.pow(y - projectCenterY, 2);
        double radius = Math.pow(projectData.kind.getRadius(mScaleFactor), 2);

        return distanceBetween <= radius;
    }

    private float getDrawPosX(ProjectDataIntra projectData) {
        return drawPosComputed.get(projectData.id).x;
    }

    private float getDrawPosY(ProjectDataIntra projectData) {
        return drawPosComputed.get(projectData.id).y;
    }

    private float getDrawPosX(ProjectDataIntra projectData, boolean forceReCompute) {
        if (forceReCompute)
            drawPosComputed.get(projectData.id).x = getDrawPosX(projectData.x);
        return drawPosComputed.get(projectData.id).x;
    }

    private float getDrawPosX(float pos) {
        return ((pos - 3000) + posX) * mScaleFactor + semiWidth;
    }

    private float getDrawPosY(ProjectDataIntra projectData, boolean forceReCompute) {
        if (forceReCompute)
            drawPosComputed.get(projectData.id).y = getDrawPosY(projectData.y);
        return drawPosComputed.get(projectData.id).y;
    }

    private float getDrawPosY(float pos) {
        return ((pos - 3000) + posY) * mScaleFactor + semiHeight;
    }

    boolean ptInsideRectPiscine(float clickX, float clickY, ProjectDataIntra projectData) {

        float x = getDrawPosX(projectData);
        float y = getDrawPosY(projectData);

        float semiWidth = projectData.kind.getWidth(mScaleFactor) / 2;
        float semiHeight = projectData.kind.getHeight(mScaleFactor) / 2;

        return clickX >= x - semiWidth &&
                clickX <= x + semiWidth / 2 &&
                clickY >= y - semiHeight / 2 &&
                clickY <= y + semiHeight / 2;
    }

    boolean ptInsideRectRush(float clickX, float clickY, ProjectDataIntra projectData) {

        float x = getDrawPosX(projectData);
        float y = getDrawPosY(projectData);

        float semiWidth = projectData.kind.getWidth(mScaleFactor) / 2;
        float semiHeight = projectData.kind.getHeight(mScaleFactor) / 2;

        return clickX >= x - semiWidth &&
                clickX <= x + semiWidth / 2 &&
                clickY >= y - semiHeight / 2 &&
                clickY <= y + semiHeight / 2;
    }

    /* ********** interfaces and classes ********** */

    public interface OnProjectClickListener {
        void onClick(ProjectDataIntra projectData);
    }

    private static class TextCalculator {

        static List<String> split(ProjectDataIntra projectData, Paint paintText, float scale) {
            float oldTextSize = paintText.getTextSize();
            paintText.setTextSize(oldTextSize * 1.05f); // make text just a little bit bigger to avoid text glued to the border

            float projectWidth = projectData.kind.getWidth(scale); // size of the preferable draw space
            float textWidth = paintText.measureText(projectData.name); // size of the entire text

            List<String> textToDraw = new ArrayList<>();

            if (projectWidth != -1 && projectWidth < textWidth) {
                int numberCut = Math.round(textWidth / projectWidth) + 1;
                String tmpText = projectData.name;
                int posToCut = tmpText.length() / numberCut;

                int i = 0;
                while (true) {
                    posToCut = TextCalculator.splitAt(tmpText, posToCut);
                    if (posToCut == -1) {
                        textToDraw.add(tmpText);
                        break;
                    }

                    if (tmpText.charAt(posToCut) == ' ') {
                        textToDraw.add(tmpText.substring(0, posToCut));
                        tmpText = tmpText.substring(posToCut + 1);
                    } else {
                        textToDraw.add(tmpText.substring(0, posToCut + 1));
                        tmpText = tmpText.substring(posToCut + 1);
                    }
                    tmpText = tmpText.trim();
                    i++;
                    posToCut = tmpText.length() / (numberCut - i);
                }

            } else
                textToDraw.add(projectData.name);
            paintText.setTextSize(oldTextSize);
            return textToDraw;
        }

        private static int splitAt(String stringToSplit, int posSplit) {

            if (posSplit < 0 || stringToSplit == null || stringToSplit.length() <= posSplit)
                return -1;

            if (isSplittablePos(stringToSplit, posSplit))
                return posSplit;

            int stringLength = stringToSplit.length();
            int searchShift = 0;

            boolean pursueBefore = true;
            boolean pursueAfter = true;
            while (pursueBefore || pursueAfter) {

                if (pursueBefore && posSplit - searchShift >= 0) {
                    if (isSplittablePos(stringToSplit, posSplit - searchShift))
                        return posSplit - searchShift;
                } else
                    pursueBefore = false;
                if (pursueAfter && posSplit + searchShift < stringLength) {
                    if (isSplittablePos(stringToSplit, posSplit + searchShift))
                        return posSplit + searchShift;
                } else
                    pursueAfter = false;

                searchShift++;
            }

            return -1;
        }

        private static boolean isSplittablePos(String str, int index) {
            if (index <= 1 || index >= str.length() - 2) // a single char can't be split apart.
                return false;
            char c = str.charAt(index);
            if (c == ' ' || c == '-' || c == '_') // verify if it is a splittable char
                return (index > 2 && index < str.length() - 3) || str.length() >= 8;
            return false;
        }

    }

    /**
     * Extends {@link GestureDetector.SimpleOnGestureListener} to provide custom gesture
     * processing.
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            float tempPosX = posX - distanceX * (1 / mScaleFactor);
            float tempPosY = posY - distanceY * (1 / mScaleFactor);

            if (tempPosX < GRAPH_MAP_LIMIT_MIN)
                tempPosX = GRAPH_MAP_LIMIT_MIN;
            else if (tempPosX > GRAPH_MAP_LIMIT_MAX)
                tempPosX = GRAPH_MAP_LIMIT_MAX;

            if (tempPosY < GRAPH_MAP_LIMIT_MIN)
                tempPosY = GRAPH_MAP_LIMIT_MIN;
            else if (tempPosY > GRAPH_MAP_LIMIT_MAX)
                tempPosY = GRAPH_MAP_LIMIT_MAX;

            posX = tempPosX;
            posY = tempPosY;

            postInvalidate();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mScroller.fling(
                    (int) posX,
                    (int) posY,
                    (int) velocityX,
                    (int) velocityY,
                    GRAPH_MAP_LIMIT_MIN,
                    GRAPH_MAP_LIMIT_MAX,
                    GRAPH_MAP_LIMIT_MIN,
                    GRAPH_MAP_LIMIT_MAX);

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

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mScaleFactor *= 1.5f;
            postInvalidate();
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (data == null)
                return false;

            float x = e.getX();
            float y = e.getY();

            boolean clicked = false;

            for (ProjectDataIntra p : data) {
                if (p.kind == ProjectDataIntra.Kind.PISCINE) {
                    if (ptInsideRectPiscine(x, y, p)) {
                        clicked = true;
                        if (onClickListener != null)
                            onClickListener.onClick(p);
                        break;
                    }
                } else if (p.kind == ProjectDataIntra.Kind.RUSH) {
                    if (ptInsideRectRush(x, y, p)) {
                        clicked = true;
                        if (onClickListener != null)
                            onClickListener.onClick(p);
                        break;
                    }
                } else if (ptInsideCircle(x, y, p)) {
                    clicked = true;
                    if (onClickListener != null)
                        onClickListener.onClick(p);
                    break;
                }
            }
            return clicked;
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

    private class DrawPos {
        float x;
        float y;
    }
}
package com.paulvarry.intra42.ui.galaxy

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.SparseArray
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Scroller
import com.paulvarry.intra42.R
import com.paulvarry.intra42.ui.galaxy.model.CircleToDraw
import com.paulvarry.intra42.ui.galaxy.model.ProjectDataIntra
import java.util.*
import kotlin.Comparator
import kotlin.math.pow
import kotlin.math.roundToInt

class Galaxy(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var weightPath: Float = 0f
    private var backgroundColor: Int = 0
    private var colorProjectUnavailable: Int = 0
    private var colorProjectAvailable: Int = 0
    private var colorProjectValidated: Int = 0
    private var colorProjectInProgress: Int = 0
    private var colorProjectFailed: Int = 0
    private var colorProjectTextUnavailable: Int = 0
    private var colorProjectTextAvailable: Int = 0
    private var colorProjectTextValidated: Int = 0
    private var colorProjectTextInProgress: Int = 0
    private var colorProjectTextFailed: Int = 0

    private val mGestureDetector: GestureDetector
    private val mScroller: Scroller
    private val mScrollAnimator: ValueAnimator
    private val mScaleDetector: ScaleGestureDetector

    /**
     * Data for current Galaxy.
     */
    private var data: List<ProjectDataIntra>? = null
    private var dataCircle: MutableList<CircleToDraw>? = null
    private var noDataMessage: String = context.getString(R.string.galaxy_not_found)

    /**
     * Current scale factor.
     */
    private var mScaleFactor = 1f
        set(value) {
            // Don't let the object get too small or too large.
            field = value.coerceIn(0.1f, 2.5f)
        }
    private val mPaintBackground: Paint
    private val mPaintPath: Paint
    private val mPaintProject: Paint
    private val mPaintText: Paint
    private val position = PointF(0f, 0f)

    /**
     * semiWidth is the height of the view divided by 2.
     */
    private var semiHeight: Float = 0.toFloat()

    /**
     * semiWidth is the width of the view divided by 2.
     */
    private var semiWidth: Float = 0.toFloat()

    /**
     * Compute title of each project once when data is added.
     * This split the title in multi line to display inside of the circle.
     */
    private var projectTitleComputed: SparseArray<List<String>> = SparseArray(0)

    /**
     * Save cache for project position. This is computed once per call of onDraw();
     */
    private var drawPosComputed: SparseArray<PointF> = SparseArray()

    /**
     * Listener for project clicks.
     */
    private var onClickListener: OnProjectClickListener? = null

    private val isAnimationRunning: Boolean
        get() = !mScroller.isFinished

    private var projectDataFirstInternship: ProjectDataIntra? = null
    private var projectDataFinalInternship: ProjectDataIntra? = null

    init {
        val attributes = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.Galaxy,
                0, 0)

        try {
            val defaultTextColor = Color.parseColor("#3F51B5")
            backgroundColor = attributes.getColor(R.styleable.Galaxy_colorProjectBackground, 0)
            colorProjectUnavailable = attributes.getColor(R.styleable.Galaxy_colorProjectUnavailable, 0)
            colorProjectAvailable = attributes.getColor(R.styleable.Galaxy_colorProjectAvailable, 0)
            colorProjectValidated = attributes.getColor(R.styleable.Galaxy_colorProjectValidated, 0)
            colorProjectFailed = attributes.getColor(R.styleable.Galaxy_colorProjectFailed, 0)
            colorProjectInProgress = attributes.getColor(R.styleable.Galaxy_colorProjectInProgress, 0)
            colorProjectTextUnavailable = attributes.getColor(R.styleable.Galaxy_colorProjectOnUnavailable, defaultTextColor)
            colorProjectTextAvailable = attributes.getColor(R.styleable.Galaxy_colorProjectOnAvailable, defaultTextColor)
            colorProjectTextValidated = attributes.getColor(R.styleable.Galaxy_colorProjectOnValidated, defaultTextColor)
            colorProjectTextFailed = attributes.getColor(R.styleable.Galaxy_colorProjectOnFailed, defaultTextColor)
            colorProjectTextInProgress = attributes.getColor(R.styleable.Galaxy_colorProjectOnInProgress, defaultTextColor)
            weightPath = attributes.getDimension(R.styleable.Galaxy_weightPath, 1f)
        } finally {
            attributes.recycle()
        }

        mPaintBackground = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintBackground.color = backgroundColor
        mPaintBackground.style = Paint.Style.FILL

        mPaintPath = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintPath.isAntiAlias = true
        mPaintPath.color = colorProjectUnavailable
        mPaintPath.strokeWidth = weightPath
        mPaintPath.style = Paint.Style.STROKE

        mPaintProject = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintProject.isAntiAlias = true
        mPaintProject.color = colorProjectUnavailable

        mPaintText = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintText.isAntiAlias = true
        mPaintText.isFakeBoldText = true
        mPaintText.textAlign = Paint.Align.CENTER
        mPaintText.textSize = TEXT_HEIGHT * mScaleFactor


        // Create a Scroller to handle the fling gesture.
        mScroller = Scroller(context, null, true)

        // The scroller doesn't have any built-in animation functions--it just supplies
        // values when we ask it to. So we have to have a way to call it every frame
        // until the fling ends. This code (ab)uses a ValueAnimator object to generate
        // a callback on every animation frame. We don't use the animated value at all.
        mScrollAnimator = ValueAnimator.ofInt(0, 1)
        mScrollAnimator.addUpdateListener { tickScrollAnimation() }

        // Create a gesture detector to handle onTouch messages
        mGestureDetector = GestureDetector(this.context, GestureListener())

        // Turn off long press--this control doesn't use it, and if long press is enabled,
        // you can't scroll for a bit, pause, then scroll some more (the pause is interpreted
        // as a long press, apparently)
        mGestureDetector.setIsLongpressEnabled(false)

        mScaleDetector = ScaleGestureDetector(context, ScaleListener())

        onUpdateData()
    }

    private fun tickScrollAnimation() {
        if (!mScroller.isFinished) {
            mScroller.computeScrollOffset()
            position.set(mScroller.currX.toFloat(), mScroller.currY.toFloat())
        } else {
            mScrollAnimator.cancel()
            onScrollFinished()
        }
    }

    private fun onUpdateData() {
        data?.let {
            mPaintText.textSize = TEXT_HEIGHT * mScaleFactor
            projectTitleComputed = SparseArray(it.size)
            drawPosComputed = SparseArray()
            it.forEach { projectData ->
                projectTitleComputed.put(projectData.id, TextCalculator.split(projectData, mPaintText, mScaleFactor))
                drawPosComputed.put(projectData.id, PointF())
            }
        }
    }

    /**
     * Called when the user finishes a scroll action.
     */
    private fun onScrollFinished() {
        decelerate()
    }

    /**
     * Disable hardware acceleration (releases memory)
     */
    private fun decelerate() {
        setLayerToSW(this)
    }

    private fun setLayerToSW(v: View) {
        if (!v.isInEditMode) {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
    }

    fun setOnProjectClickListener(onClickListener: OnProjectClickListener) {
        this.onClickListener = onClickListener
    }

    fun setData(data: List<ProjectDataIntra>?, cursusId: Int) {
        val c = Hack.computeData(data, cursusId)
        this.data = c?.first
        this.dataCircle = c?.second
        if (this.data.isNullOrEmpty())
            this.data = null

        if (data != null) {
            for (projectData in data) {

                if (projectData.kind == ProjectDataIntra.Kind.FIRST_INTERNSHIP)
                    projectDataFirstInternship = projectData
                else if (projectData.kind == ProjectDataIntra.Kind.SECOND_INTERNSHIP)
                    projectDataFinalInternship = projectData
            }

            projectDataFirstInternship?.let {
                it.x = 3680
                it.y = 3750
            }
            projectDataFinalInternship?.let {
                it.x = 4600
                it.y = 4600
            }

            try {
                Collections.sort(data, Comparator { o1, o2 ->
                    if (o1.state == null || o2.state == null)
                        return@Comparator 0
                    o1.state!!.layerIndex.compareTo(o2.state!!.layerIndex) //TODO
                })
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }

        }

        onUpdateData()
        onScrollFinished()
        invalidate()
    }

    fun setMessage(string: String) {
        data = null
        this.noDataMessage = string

        onScrollFinished()
        invalidate()
    }

    /**
     * Enable hardware acceleration (consumes memory)
     */
    fun accelerate() {
        setLayerToHW(/*this*/)
    }

    private fun setLayerToHW(/*v: View*/) {
//        if (!v.isInEditMode) {
//            setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Let the GestureDetector interpret this event
        var resultGesture = mGestureDetector.onTouchEvent(event)
        val resultScale = mScaleDetector.onTouchEvent(event)

        // If the GestureDetector doesn't want this event, do some custom processing.
        // This code just tries to detect when the user is done scrolling by looking
        // for ACTION_UP events.
        if (!resultGesture && !resultScale) {
            if (event.action == MotionEvent.ACTION_UP) {
                // User is done scrolling, it's now safe to do things like autocenter
                stopScrolling()
                resultGesture = true
            }
        }

        return resultGesture
    }

    /**
     * Force a stop to all motion. Called when the user taps during a fling.
     */
    private fun stopScrolling() {
        mScroller.forceFinished(true)

        onScrollFinished()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        semiHeight = h / 2f
        semiWidth = w / 2f

        position.set(0f, 0f)

        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawPaint(mPaintBackground)

        data?.let {
            onDrawData(canvas, it)
        } ?: run {
            onDrawEmpty(canvas)
        }
    }

    private fun onDrawData(canvas: Canvas, data: List<ProjectDataIntra>) {
        mPaintPath.strokeWidth = weightPath * mScaleFactor
        mPaintText.textSize = TEXT_HEIGHT * mScaleFactor

        onDrawDataPath(canvas, data)
        onDrawDataCircle(canvas)
        onDrawDataContent(canvas, data)

        if (mScrollAnimator.isRunning) {
            tickScrollAnimation()
            postInvalidate()
        }
    }

    private fun onDrawDataPath(canvas: Canvas, data: List<ProjectDataIntra>) {
        var startX: Float
        var startY: Float
        var stopX: Float
        var stopY: Float
        for (projectData in data) {
            projectData.by?.forEach { by ->
                by.points?.let { points ->
                    startX = getDrawPosX(points[0][0])
                    startY = getDrawPosY(points[0][1])
                    stopX = getDrawPosX(points[1][0])
                    stopY = getDrawPosY(points[1][1])

                    canvas.drawLine(startX, startY, stopX, stopY, getPaintPath(projectData.state))
                }
            }
        }
    }

    private fun onDrawDataCircle(canvas: Canvas) {
        projectDataFirstInternship?.let {
            canvas.drawCircle(
                    getDrawPosX(3000f),
                    getDrawPosY(3000f),
                    1000 * mScaleFactor,
                    getPaintPath(it.state))
        }
        projectDataFinalInternship?.let {
            canvas.drawCircle(
                    getDrawPosX(3000f),
                    getDrawPosY(3000f),
                    2250 * mScaleFactor,
                    getPaintPath(it.state))
        }

        dataCircle?.forEach {
            canvas.drawCircle(
                    getDrawPosX(3000f),
                    getDrawPosY(3000f),
                    it.radius * mScaleFactor,
                    getPaintPath(it.state ?: ProjectDataIntra.State.UNAVAILABLE))
        }
    }

    private fun onDrawDataContent(canvas: Canvas, data: List<ProjectDataIntra>) {
        for (projectData in data) {
            projectData.kind?.data?.let {
                when (it) {
                    is ProjectDataIntra.Kind.DrawType.Rectangle -> drawRectangle(canvas, projectData, it)
                    is ProjectDataIntra.Kind.DrawType.RoundRect -> drawRoundRect(canvas, projectData, it)
                    is ProjectDataIntra.Kind.DrawType.Circle -> drawCircle(canvas, projectData, it)
                }
            }
        }
    }

    private fun onDrawEmpty(canvas: Canvas) {
        mPaintText.color = colorProjectTextAvailable
        mPaintText.textSize = 50 * mScaleFactor
        canvas.drawText(noDataMessage, width / 2f, height * 0.8f, mPaintText)
    }

    override fun setBackgroundColor(color: Int) {
        backgroundColor = color
        invalidate()
        requestLayout()
    }

    private fun getPaintProject(projectState: ProjectDataIntra.State?): Paint {
        mPaintProject.color = getColorProject(projectState)

        return mPaintProject
    }

    private fun getPaintPath(projectState: ProjectDataIntra.State?): Paint {
        mPaintPath.color = getColorProject(projectState)
        return mPaintPath
    }

    private fun getPaintProjectText(projectData: ProjectDataIntra): Paint {
        mPaintText.color = getColorText(projectData.state)

        mPaintText.textSize = TEXT_HEIGHT * mScaleFactor
        if (projectData.kind == ProjectDataIntra.Kind.INNER_SATELLITE) {
            mPaintText.textSize = TEXT_HEIGHT * mScaleFactor * 0.5f
        }

        return mPaintText
    }

    private fun getColorProject(projectState: ProjectDataIntra.State?): Int {
        return when (projectState) {
            ProjectDataIntra.State.DONE -> colorProjectValidated
            ProjectDataIntra.State.AVAILABLE -> colorProjectAvailable
            ProjectDataIntra.State.IN_PROGRESS -> colorProjectInProgress
            ProjectDataIntra.State.UNAVAILABLE -> colorProjectUnavailable
            ProjectDataIntra.State.FAIL -> colorProjectFailed
            null -> colorProjectUnavailable
        }
    }

    private fun getColorText(projectState: ProjectDataIntra.State?): Int {
        return when (projectState) {
            ProjectDataIntra.State.DONE -> colorProjectTextValidated
            ProjectDataIntra.State.AVAILABLE -> colorProjectTextAvailable
            ProjectDataIntra.State.IN_PROGRESS -> colorProjectTextInProgress
            ProjectDataIntra.State.UNAVAILABLE -> colorProjectTextUnavailable
            ProjectDataIntra.State.FAIL -> colorProjectTextFailed
            null -> colorProjectTextUnavailable
        }
    }

    private fun drawProjectTitle(canvas: Canvas, projectData: ProjectDataIntra) {
        val paintText = getPaintProjectText(projectData)
        val textToDraw = projectTitleComputed.get(projectData.id)

        val textHeight = paintText.textSize
        val posYStartDraw = getDrawPosY(projectData) - textHeight * (textToDraw.size - 1) / 2
        var heightTextDraw: Float

        for (i in textToDraw.indices) {
            heightTextDraw = posYStartDraw + textHeight * i - (paintText.descent() + paintText.ascent()) / 2
            canvas.drawText(
                    textToDraw[i],
                    getDrawPosX(projectData),
                    heightTextDraw,
                    paintText)
        }
    }

    private fun drawCircle(canvas: Canvas, projectData: ProjectDataIntra, data: ProjectDataIntra.Kind.DrawType.Circle) {
        canvas.drawCircle(
                getDrawPosX(projectData, true),
                getDrawPosY(projectData, true),
                data.radius * mScaleFactor,
                getPaintProject(projectData.state))

        drawProjectTitle(canvas, projectData)
    }

    private fun drawRectangle(canvas: Canvas, projectData: ProjectDataIntra, data: ProjectDataIntra.Kind.DrawType.Rectangle) {

        val x = getDrawPosX(projectData, true)
        val y = getDrawPosY(projectData, true)

        val width = data.width * mScaleFactor
        val height = data.height * mScaleFactor

        val left = x - width / 2
        val top = y + height / 2
        val right = x + width / 2
        val bottom = y - height / 2

        canvas.drawRect(left, top, right, bottom, getPaintProject(projectData.state))
        drawProjectTitle(canvas, projectData)
    }

    private fun drawRoundRect(canvas: Canvas, projectData: ProjectDataIntra, data: ProjectDataIntra.Kind.DrawType.RoundRect) {

        val x = getDrawPosX(projectData, true)
        val y = getDrawPosY(projectData, true)

        val width = data.width * mScaleFactor
        val height = data.height * mScaleFactor

        val left = x - width / 2
        val top = y + height / 2
        val right = x + width / 2
        val bottom = y - height / 2

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            canvas.drawRoundRect(left, top, right, bottom, 100f, 100f, getPaintProject(projectData.state))
        else
            canvas.drawRect(left, top, right, bottom, getPaintProject(projectData.state))

        drawProjectTitle(canvas, projectData)
    }

    private fun getDrawPosX(projectData: ProjectDataIntra, forceReCompute: Boolean = false): Float {
        if (forceReCompute)
            drawPosComputed.get(projectData.id).x = getDrawPosX(projectData.x.toFloat())
        return drawPosComputed.get(projectData.id).x
    }

    private fun getDrawPosX(pos: Float): Float {
        return (pos - centerPoint.x + position.x) * mScaleFactor + semiWidth
    }

    private fun getDrawPosY(projectData: ProjectDataIntra, forceReCompute: Boolean = false): Float {
        if (forceReCompute)
            drawPosComputed.get(projectData.id).y = getDrawPosY(projectData.y.toFloat())
        return drawPosComputed.get(projectData.id).y
    }

    private fun getDrawPosY(pos: Float): Float {
        return (pos - centerPoint.y + position.y) * mScaleFactor + semiHeight
    }

    internal fun ptInsideCircle(x: Float, y: Float, projectData: ProjectDataIntra, data: ProjectDataIntra.Kind.DrawType.Circle): Boolean {

        val projectCenterX = getDrawPosX(projectData)
        val projectCenterY = getDrawPosY(projectData)

        val distanceBetween = (x - projectCenterX).pow(2f) + (y - projectCenterY).pow(2f)
        val radius = data.radius.times(mScaleFactor).pow(2f)

        return distanceBetween <= radius
    }

    internal fun ptInsideRectangle(clickX: Float, clickY: Float, projectData: ProjectDataIntra, data: ProjectDataIntra.Kind.DrawType.Rectangle): Boolean {

        val x = getDrawPosX(projectData)
        val y = getDrawPosY(projectData)

        val semiWidth = data.width * mScaleFactor / 2f
        val semiHeight = data.height * mScaleFactor / 2f

        return clickX >= x - semiWidth &&
                clickX <= x + semiWidth / 2 &&
                clickY >= y - semiHeight / 2 &&
                clickY <= y + semiHeight / 2
    }

    internal fun ptInsideRoundRect(clickX: Float, clickY: Float, projectData: ProjectDataIntra, data: ProjectDataIntra.Kind.DrawType.RoundRect): Boolean {

        val x = getDrawPosX(projectData)
        val y = getDrawPosY(projectData)

        val semiWidth = data.width * mScaleFactor / 2f
        val semiHeight = data.height * mScaleFactor / 2f

        return clickX >= x - semiWidth &&
                clickX <= x + semiWidth / 2 &&
                clickY >= y - semiHeight / 2 &&
                clickY <= y + semiHeight / 2
    }

    /* ********** interfaces and classes ********** */

    interface OnProjectClickListener {
        fun onClick(projectData: ProjectDataIntra)
    }

    /**
     * Extends [GestureDetector.SimpleOnGestureListener] to provide custom gesture
     * processing.
     */
    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {

            var tmpX = position.x - distanceX * (1 / mScaleFactor)
            var tmpY = position.y - distanceY * (1 / mScaleFactor)

            tmpX = tmpX.coerceIn(GRAPH_MAP_LIMIT_MIN.toFloat(), GRAPH_MAP_LIMIT_MAX.toFloat())
            tmpY = tmpY.coerceIn(GRAPH_MAP_LIMIT_MIN.toFloat(), GRAPH_MAP_LIMIT_MAX.toFloat())

            position.set(tmpX, tmpY)

            postInvalidate()
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            mScroller.fling(
                    position.x.roundToInt(),
                    position.y.roundToInt(),
                    velocityX.toInt(),
                    velocityY.toInt(),
                    GRAPH_MAP_LIMIT_MIN,
                    GRAPH_MAP_LIMIT_MAX,
                    GRAPH_MAP_LIMIT_MIN,
                    GRAPH_MAP_LIMIT_MAX)

            // Start the animator and tell it to animate for the expected duration of the fling.
            mScrollAnimator.duration = mScroller.duration.toLong()
            mScrollAnimator.start()
            return true
        }

        override fun onDown(e: MotionEvent): Boolean {
            // The user is interacting with the pie, so we want to turn on acceleration
            // so that the interaction is smooth.
            accelerate()
            if (isAnimationRunning) {
                stopScrolling()
            }
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            mScaleFactor *= 1.5f
            postInvalidate()
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            var clicked = false

            data?.forEach { projectData ->

                projectData.kind?.let {
                    val x = e.x
                    val y = e.y
                    when (it.data) {
                        is ProjectDataIntra.Kind.DrawType.Circle -> {
                            if (ptInsideCircle(x, y, projectData, it.data)) {
                                clicked = true
                                onClickListener?.onClick(projectData)
                            }
                        }
                        is ProjectDataIntra.Kind.DrawType.RoundRect -> {
                            if (ptInsideRoundRect(x, y, projectData, it.data)) {
                                clicked = true
                                onClickListener?.onClick(projectData)
                            }
                        }
                        is ProjectDataIntra.Kind.DrawType.Rectangle -> {
                            if (ptInsideRectangle(x, y, projectData, it.data)) {
                                clicked = true
                                onClickListener?.onClick(projectData)
                            }
                        }
                    }
                }
            }
            return clicked
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor

            invalidate()
            return true
        }
    }

    companion object {

        val centerPoint = Point(3000, 3000)
        val graphSize = Point(6000, 6000)

        var GRAPH_MAP_LIMIT_MIN = -2000
        var GRAPH_MAP_LIMIT_MAX = 2000
        var TEXT_HEIGHT = 25
    }
}
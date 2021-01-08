package com.example.paint.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

const val TOUCH_TOLERANCE: Int = 10
lateinit var bitmap: Bitmap
lateinit var bitmapCanvas: Canvas
lateinit var paintScreen: Paint
lateinit var paintLine: Paint
lateinit var pathMap: HashMap<Int, Path>
lateinit var previousPointMap: HashMap<Int, Point>

class PicassoView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    init {
        paintScreen = Paint()
        paintLine = Paint()
        paintLine.isAntiAlias = false
        paintLine.setColor(Color.BLACK)
        paintLine.style = Paint.Style.STROKE
        paintLine.strokeWidth = 10F
        paintLine.strokeCap = Paint.Cap.ROUND
        pathMap = HashMap()
        previousPointMap = HashMap()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmapCanvas = Canvas(bitmap)
        bitmap.eraseColor(Color.WHITE)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(bitmap, 0F, 0F, paintScreen)
        for (key in pathMap.keys) {
            canvas?.drawPath(pathMap.get(key)!!, paintLine)
            Log.d("keyyyyyyyyyyyyyyyyyy", key.toString())
        }
//        canvas?.drawCircle((width/2).toFloat(), (height/2).toFloat(), 78F, paintLine)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action = event?.actionMasked
        val actionIndex = event?.actionIndex
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_UP) {
            touchStarted(
                actionIndex!!.let { event.getX(it) },
                actionIndex!!.let { event.getY(it) },
                actionIndex!!.let {
                    event.getPointerId(it)
                }

            )
            Log.d("ID", event.getPointerId(actionIndex).toString())
            Log.d("PointerIndex", actionIndex.toString())
            Log.d("actionMasked", action.toString())
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(actionIndex?.let { event.getPointerId(it) }
            )
            Log.d("ID", actionIndex?.let { event.getPointerId(it) }.toString())
            Log.d("PointerIndex", actionIndex.toString())
            Log.d("actionMasked", action.toString())


        } else {
            touchMoved(event)
            Log.d("ID", actionIndex?.let { event.getPointerId(it) }.toString())
            Log.d("PointerIndex", actionIndex.toString())
            Log.d("actionMasked", action.toString())
        }
        invalidate()
        return true
    }

    private fun touchStarted(x: Float?, y: Float?, pID: Int?) {
        var path: Path = Path()
        var point: Point = Point()
        if (pathMap.containsKey(pID)) {
            path = pathMap.get(pID)!!
            point = previousPointMap.get(pID)!!
        } else {
            path = Path()
            pathMap.put(pID!!, path)
            point = Point()
            previousPointMap.put(pID, point)
        }
        path.moveTo(x!!.toFloat(), y!!.toFloat())
        point.x = x.toInt()
        point.y = y.toInt()
    }

    private fun touchMoved(event: MotionEvent?) {
        for (i in 0 until event!!.pointerCount) {
            val pointerId = event.getPointerId(i)
            val pointerIndex = event.findPointerIndex(pointerId)
            if (pathMap.containsKey(pointerId)) {
                val newX = event.getX(pointerIndex)
                val newY = event.getY(pointerIndex)

                val path: Path? = pathMap.get(pointerId)
                val point: Point? = previousPointMap.get(pointerId)

                val deltaX: Float = abs(newX - (point!!.x))
                val deltaY: Float = abs(newY - (point!!.y))
                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    path!!.quadTo(
                        point.x.toFloat(),
                        point.y.toFloat(),
                        (newX + point.x) / 2,
                        (newY + point.y) / 2
                    )
                    // path?.lineTo(newX.toFloat(), newY.toFloat())
                    point.x = newX.toInt()
                    point.y = newY.toInt()
                }
            }
        }
    }

    private fun touchEnded(pID: Int?) {
        val path: Path? = pathMap.get(pID)
        bitmapCanvas.drawPath(path!!, paintLine)
        path.reset()
    }

    fun clear() {
        pathMap.clear()
        previousPointMap.clear()
        bitmap.eraseColor(Color.WHITE)
        invalidate()
    }

    public fun setDrawingColor(color: Int) {
        paintLine.setColor(color)
    }
    public fun getDrawingColor(): Int {
        return paintLine.color
    }
    public fun setLineWidth(width:Int){
        paintLine.strokeWidth= width.toFloat()
    }
    public fun getLineWidth(): Float {
        return paintLine.strokeWidth
    }
}
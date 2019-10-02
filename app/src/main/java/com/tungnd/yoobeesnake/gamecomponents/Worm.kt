package com.tungnd.yoobeesnake.gamecomponents

import android.graphics.*
import android.util.Log
import com.tungnd.yoobeesnake.utils.Maths
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author Tung Nguyen (petaminds)
 * The worm always moves to the destination with the speed defined by movement vector
 */
class Worm : Collidable, Runnable {


    private var mPaint = Paint()
    private var mPath = Path()
    private var worm_pace = 0f
    private var mCurrentMoveVector = PointF()
    private var mTargetMoveVec = PointF()
    private var mSize = 300.0f
        set(value) {
            field = value
        }
    private var bodyPoints = ArrayList<PointF>()
    private val BODY_SEGMENTS = 50
    private val CURVE_SEGMENTS = 5
    private val head = RectF(0f, 0f, 40f, 40f)
    private val mHeadTracker = PointF()
    private var mAnimationThread: Thread? = null
    private val mAnimationFR = 15//5 frames /sec
    private var mHeadRadial = 0.0
    private var mMoveAmplitute = 0f

    var destination = PointF()
        set(value) {
            if (!field.equals(value)) {
                field.set(value)
                updateDirection()
            }
            Log.i(this.toString(), destination.toString())
        }

    init {
        mPath.cubicTo(mSize / 3, -mSize / 5, mSize * 2 / 3, mSize / 5, mSize, 0f)
        val pm = PathMeasure(mPath, false)

        for (f in 0..BODY_SEGMENTS) {
            val aBodyPoint = FloatArray(2)
            pm.getPosTan(pm.length * f / BODY_SEGMENTS, aBodyPoint, null)
            val aBodyPointF = PointF(aBodyPoint[0], aBodyPoint[1])
            bodyPoints.add(aBodyPointF)
        }

        worm_pace = pm.length / BODY_SEGMENTS
        mCurrentMoveVector.set(-worm_pace, 0f)
        mTargetMoveVec.set(mCurrentMoveVector)
        mMoveAmplitute = 2 * worm_pace

        mPath.reset()

        mPaint.apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 10f
            isAntiAlias = true
            textSize = 60f
        }

        mAnimationThread = Thread(this)
        mAnimationThread?.start()
    }

    override fun collide(o: Collidable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     *
     */
    private fun move() {
        mHeadTracker.offset(mCurrentMoveVector.x, mCurrentMoveVector.y)
        //move zigzag
        mHeadRadial += 2 * Math.PI / mAnimationFR

        val dxOrigin = 0f
        val dyOrigin = mMoveAmplitute * Math.sin(mHeadRadial)//*
        //(if (mCurrentMoveVector.y > 0)  1 else -1)
        val dTranspose = Maths.RotatedCoordinate(dxOrigin, dyOrigin.toFloat(), mCurrentMoveVector)


//        val diag =  Math.sqrt((mCurrentMoveVector.y * mCurrentMoveVector.y +
//                mCurrentMoveVector.x * mCurrentMoveVector.x).toDouble())
//        val sinOfCurrentMoveVec = mCurrentMoveVector.y / diag
//        val cosOfCurrentMoveVec = mCurrentMoveVector.x / diag
//
//        val dxTransposed = dxOrigin * cosOfCurrentMoveVec - dyOrigin*sinOfCurrentMoveVec
//        val dyTransposed = dxOrigin * sinOfCurrentMoveVec + dyOrigin * cosOfCurrentMoveVec

        //update body part
        for (i in (bodyPoints.size - 1) downTo 1) {
            bodyPoints[i].set(bodyPoints[i - 1])
        }

        bodyPoints[0].set(
            (mHeadTracker.x + dTranspose.x).toFloat(),
            (mHeadTracker.y + dTranspose.y).toFloat()
        )

    }

    fun translateTo(x: Float, y: Float) {
        val dx = x - bodyPoints[0].x
        val dy = y - bodyPoints[0].y

        for (i in 0..bodyPoints.size - 1) {
            bodyPoints[i].offset(dx, dy)
        }
        mHeadTracker.set(bodyPoints[0])
    }

    fun draw(c: Canvas?) {
        //draw the head
        head.set(
            bodyPoints[0].x - head.width() / 2, bodyPoints[0].y - head.height() / 2
            , bodyPoints[0].x + head.width() / 2, bodyPoints[0].y + head.height() / 2
        )
        //c?.save()
        c?.rotate(45f, bodyPoints[0].x, bodyPoints[0].y)
        mPaint.style = Paint.Style.FILL_AND_STROKE
        c?.drawRect(head, mPaint)
        c?.rotate(-45f, bodyPoints[0].x, bodyPoints[0].y)
        //draw the body
        //c?.restore()
        mPaint.style = Paint.Style.STROKE
        mPath.moveTo(bodyPoints[0].x, bodyPoints[0].y)
        for (point in bodyPoints) {
            mPath.lineTo(point.x, point.y)
        }
        c?.drawPath(mPath, mPaint)
        mPath.reset()
    }

    override fun run() {
        while (true) {
            move()
            Thread.sleep(1000L / mAnimationFR)
        }
    }

    private fun updateDirection() {
        val v = PointF(destination.x - bodyPoints[0].x, destination.y - bodyPoints[0].y)
        val diag = Math.sqrt((v.x * v.x + v.y * v.y).toDouble())
        mCurrentMoveVector.set(
            (worm_pace * v.x / diag).toFloat(),
            (worm_pace * v.y / diag).toFloat()
        )
//        if(!mCurrentMoveVector.equals(mTargetMoveVec)){
//            if(Maths.CosineSim(mCurrentMoveVector, mTargetMoveVec) < 0.95){
//
//            }
//        }
    }

}
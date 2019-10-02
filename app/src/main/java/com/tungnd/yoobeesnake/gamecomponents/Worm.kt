package com.tungnd.yoobeesnake.gamecomponents

import android.graphics.*
import android.util.Log
import com.tungnd.yoobeesnake.utils.Maths
import kotlin.collections.ArrayList

/**
 * @author Tung Nguyen (petaminds)
 * The worm always moves to the destination with the speed defined by movement vector
 */
class Worm : Collidable, Runnable {


    private var mPaint = Paint()
    private var mPath = Path()
    var state = SpriteState.ALIVE

    private var worm_pace = 0f
    private var timeToMove = 0L
    @Volatile
    private var mCurrentMoveVector = PointF()
    private var mTargetMoveVec = PointF()
    private var mSize = 300.0f
        set(value) {
            field = value
        }
    private var bodyPoints = ArrayList<PointF>()
    private val BODY_SEGMENTS = 50
    private val head = RectF(-15f, 15f, 15f, -15f)
    private var mHeadPoints: Array<PointF>? = null
    private var headFacingDegree = -45
    private val mHeadTracker = PointF()
    private var mAnimationThread: Thread? = null
    private val mAnimationFR = 26//5 frames /sec
    private var mHeadRadial = 0.0 //Moving up and down witn sinus
    private var mMoveAmplitude = 0f //head moving amplitude
    @Volatile
    private var isRotatingHead = false
    private var mRotateHeadRunnable = Runnable {
        run() {
            val dx = (mTargetMoveVec.x - mCurrentMoveVector.x) / mAnimationFR
            val dy = (mTargetMoveVec.y - mCurrentMoveVector.y) / mAnimationFR

            while (Maths.CosineSim(mCurrentMoveVector, mTargetMoveVec) < 0.95 && isRotatingHead) {
                val rVec = PointF()
                rVec.set(mCurrentMoveVector)
                rVec.offset(dx, dy)
                val sc = Maths.VecSinCos(rVec)

                mCurrentMoveVector.set(worm_pace * sc.x, worm_pace * sc.y)
                Log.d("rotate", mCurrentMoveVector.toString())
                Thread.sleep(1000L / mAnimationFR)
            }

            mCurrentMoveVector.set(mTargetMoveVec)
            isRotatingHead = false
        }
    }

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
        mMoveAmplitude = 2 * worm_pace

        mPath.reset()

        val r45deg = PointF(1f, -1f)
        val p1 = Maths.RotatedCoordinate(head.top, head.right, r45deg)
        val p2 = Maths.RotatedCoordinate(head.top, head.left, r45deg)
        val p3 = Maths.RotatedCoordinate(head.bottom, head.left, r45deg)
        val p4 = Maths.RotatedCoordinate(head.bottom, head.right, r45deg)
        mHeadPoints = arrayOf(p1, p2, p3, p4)

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
        val dyOrigin = mMoveAmplitude * Math.sin(mHeadRadial)
        val dTranspose = Maths.RotatedCoordinate(dxOrigin, dyOrigin.toFloat(), mCurrentMoveVector)

        //update body part
        for (i in (bodyPoints.size - 1) downTo 1) {
            bodyPoints[i].set(bodyPoints[i - 1])
        }

        //Location of head
        bodyPoints[0].set(
            mHeadTracker.x + dTranspose.x,
            mHeadTracker.y + dTranspose.y
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
        val p1 =
            mHeadPoints?.get(0)?.x?.let {
                mHeadPoints?.get(0)?.y?.let { it1 ->
                    Maths.RotatedCoordinate(
                        it,
                        it1, mCurrentMoveVector
                    )
                }
            }
        val p2 =
            mHeadPoints?.get(1)?.x?.let {
                mHeadPoints?.get(1)?.y?.let { it1 ->
                    Maths.RotatedCoordinate(
                        it,
                        it1, mCurrentMoveVector
                    )
                }
            }
        val p3 =
            mHeadPoints?.get(2)?.x?.let {
                mHeadPoints?.get(2)?.y?.let { it1 ->
                    Maths.RotatedCoordinate(
                        it,
                        it1, mCurrentMoveVector
                    )
                }
            }
        val p4 =
            mHeadPoints?.get(3)?.x?.let {
                mHeadPoints?.get(3)?.y?.let { it1 ->
                    Maths.RotatedCoordinate(
                        it,
                        it1, mCurrentMoveVector
                    )
                }
            }


        if (p1 != null && p2 != null && p3 != null && p4 != null) {
            mPath.moveTo(bodyPoints[0].x + p1.x, bodyPoints[0].y + p1.y)
            mPath.lineTo(bodyPoints[0].x + p2.x, bodyPoints[0].y + p2.y)
            mPath.lineTo(bodyPoints[0].x + p3.x, bodyPoints[0].y + p3.y)
            mPath.lineTo(bodyPoints[0].x + p4.x, bodyPoints[0].y + p4.y)
            mPath.close()
        }


        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.setColor(Color.BLACK)
        c?.drawPath(mPath, mPaint)


        //draw body

        mPaint.style = Paint.Style.STROKE
        mPaint.setColor(Color.BLACK)
        mPath.moveTo(bodyPoints[0].x, bodyPoints[0].y)
        for (point in bodyPoints) {
            mPath.lineTo(point.x, point.y)
        }
        c?.drawPath(mPath, mPaint)

        //draw nose
        mPaint.style = Paint.Style.FILL_AND_STROKE
        val nose = PointF(bodyPoints[0].x + p1!!.x, bodyPoints[0].y + p1.y)
        mPaint.setColor(Color.GREEN)

        c?.drawPoint(
            nose.x, nose.y,
            mPaint
        )



        mPath.reset()
    }

    override fun run() {
        while (state == SpriteState.ALIVE) {
            if (isTimeToMove()) {
                move()
            }
        }
    }

    private fun isTimeToMove(): Boolean {
        val now = System.currentTimeMillis()
        if (timeToMove < now) {
            timeToMove = now + 1000L / mAnimationFR
            return true
        }
        return false
    }

    private fun updateDirection() {
        isRotatingHead = false
        val v = PointF(destination.x - mHeadTracker.x, destination.y - mHeadTracker.y)
        val sc = Maths.VecSinCos(v)
        mTargetMoveVec.set(
            worm_pace * sc.x,
            worm_pace * sc.y
        )
        isRotatingHead = true

        Thread(mRotateHeadRunnable).start()
    }

}
package com.tungnd.yoobeesnake.gamecomponents

import android.graphics.*
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList


class Worm: Collidable {

    private var mPaint = Paint()
    private var mPath = Path()
    private var mMoveVec = Point(5, 5)
    private var mSize = 300.0f
        set(value) {field = value}
    private var bodyPoints = ArrayList<FloatArray>()
    private val BODY_SEGMENTS = 20
    private val head=  RectF(0f, 0f , 40f, 40f)

    init {
        mPath.cubicTo(mSize / 3, -mSize/5, mSize* 2 /3 ,mSize/5, mSize, 0f)
        val pm = PathMeasure(mPath, false)

        for (f in 0.. BODY_SEGMENTS ){
            val aBodyPoint = FloatArray(2)
            pm.getPosTan(pm.length * f / BODY_SEGMENTS , aBodyPoint,null)
            bodyPoints.add(aBodyPoint)
        }
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
    }

    override fun collide(o: Collidable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     *
     */
    private fun move(){

    }

    fun translateTo(x:Float, y:Float){
       val dx = x -  bodyPoints[0][0]
       val dy = y  - bodyPoints[0][1]
        for(i in 0.. bodyPoints.size -1 ){
            bodyPoints[i][0] += dx
            bodyPoints[i][1] += dy
        }
    }

    fun draw(c: Canvas?) {


        //draw the head
        head.set(bodyPoints[0][0] - head.width()/2, bodyPoints[0][1] - head.height()/2
            , bodyPoints[0][0] + head.width()/2, bodyPoints[0][1] + head.height()/2)
        //c?.save()
        c?.rotate(45f, bodyPoints[0][0], bodyPoints[0][1])
        mPaint.style = Paint.Style.FILL_AND_STROKE
        c?.drawRect(head, mPaint)
        c?.rotate(-45f, bodyPoints[0][0], bodyPoints[0][1])
        //draw the body
        //c?.restore()
        mPaint.style = Paint.Style.STROKE
        mPath.moveTo(bodyPoints[0][0], bodyPoints[0][1])
        for (point in bodyPoints)
        {
            mPath.lineTo(point[0], point[1])
        }
        c?.drawPath(mPath, mPaint)
        mPath.reset()
    }




}
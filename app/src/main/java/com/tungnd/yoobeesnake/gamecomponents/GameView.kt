package com.tungnd.yoobeesnake.gamecomponents

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.icu.lang.UCharacter.GraphemeClusterBreak.T


class GameView : SurfaceView, SurfaceHolder.Callback, GameLoop, Runnable {


    private var mPaint: Paint? = null
    private var mContext: Context? = null
    private var mHolder: SurfaceHolder? = null
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mThread: Thread? = null
    private var mCanvas: Canvas? = null
    private var mRunning: Boolean = false

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    init {
        mContext = context
        mHolder = holder
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint?.setColor(Color.RED)
        when {
            mHolder != null -> mHolder?.addCallback(this)
        }
        //alpha = 0f
        mThread = Thread(this)

    }

    override fun run() {
        while (mRunning) {
            if (mHolder?.surface?.isValid == true) {

                //Log.d("draw", "Yeah")

                mCanvas = mHolder?.lockCanvas()

                val cx = mWidth.toFloat() / 2
                val cy = mHeight.toFloat() / 2
                mCanvas?.drawColor(Color.BLUE)
                mPaint?.setColor(Color.YELLOW)
                mPaint?.let {
                    mCanvas?.drawCircle(cx, cy, 300f, it)

                }
                mHolder?.unlockCanvasAndPost(mCanvas)
            }


            Thread.sleep(60)

        }


    }


    override fun draw() {

    }

    override fun start() {
//        if(mThread == null){
//            mThread = Thread(this)
//        }
//        mThread?.start()
    }

    override fun pause() {
        mRunning = false
        try {
            // Stop the thread == rejoin the main thread.
            mThread?.join()
        } catch (e: InterruptedException) {
        }

    }

    override fun resume() {
        mRunning = true
        mThread = Thread(this)
        mThread?.start()
    }

    override fun update() {

    }

    override fun stop() {

    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        Log.d(this.toString(), "surface changed")
        //this.mHolder = holder
        this.mWidth = width
        this.mHeight = height
        Log.d("surface", width.toString() + " : " + height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        Log.d(this.toString(), "surface destroyed")
        //this.mHolder = holder
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        Log.d(this.toString(), "surface created")
        //this.mHolder = holder
    }

}
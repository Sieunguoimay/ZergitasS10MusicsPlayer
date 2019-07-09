package com.sieunguoimay.vuduydu.s10musicplayer.visual_effects

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View

class WaveformView(
    context: Context,
    attributes: AttributeSet
): View(context,attributes) {
    private var mBytes: ByteArray? = null
    private var mPoints: FloatArray? = null
    private var mRect = Rect()
    private var mForePaint = Paint()

    init{
        mBytes = null
        mForePaint.setStrokeWidth(1f)
        mForePaint.setAntiAlias(true)
        mForePaint.setColor(Color.rgb(255, 255, 255))
    }

    override fun onDraw(canvas: Canvas){

        super.onDraw(canvas)
        if (mBytes == null) {
            return
        }
        if (mPoints == null || mPoints?.size!!.compareTo(mBytes?.size!!* 4)<0) {
            mPoints = FloatArray(mBytes?.size!! * 4)
        }
        mRect.set(0, 0, width, height)
        for (i in 0 until mBytes?.size!!-1) {

            mPoints!![i * 4] = (mRect.width() * i).toFloat() / (mBytes?.size!!.minus(1)).toFloat()

            mPoints!![i * 4 + 1] = mRect.height().toFloat() / 2 + (mBytes!![i] + 128).toByte() * (mRect.height() / 4) / 128

            mPoints!![i * 4 + 2] = mRect.width().toFloat() * (i + 1) / (mBytes?.size!!.minus(1)).toFloat()

            mPoints!![i * 4 + 3] = mRect.height().toFloat() / 2 + (mBytes!![i + 1] + 128).toByte() * (mRect.height() / 4) / 128

        }

        canvas.drawLines(mPoints, mForePaint)

    }
    fun updateWaveform(waveform: ByteArray){
        mBytes = waveform
        invalidate()
    }
}
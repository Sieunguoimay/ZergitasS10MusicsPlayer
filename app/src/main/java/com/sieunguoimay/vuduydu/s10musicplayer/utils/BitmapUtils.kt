package com.sieunguoimay.vuduydu.s10musicplayer.utils

import android.graphics.Bitmap

inline fun Bitmap.scaleAndCropCenter(w: Int, h: Int): Bitmap {
    val width = this.width
    val height = this.height
    val b = if (width >= height)
        Bitmap.createBitmap(
                this,
                width / 2 - height / 2,
                0,
                height,
                height
        )
    else
        Bitmap.createBitmap(
                this,
                0,
                height / 2 - width / 2,
                width,
                width
        )
    return Bitmap.createScaledBitmap(b, w, h, true)
}
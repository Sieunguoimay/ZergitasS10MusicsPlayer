package com.sieunguoimay.vuduydu.musicplayer.models.data

import android.graphics.Bitmap

class Category(
    var id:Long,
    var title:String,
    var art: Bitmap?
) {
   var songList = ArrayList<Song>()
}
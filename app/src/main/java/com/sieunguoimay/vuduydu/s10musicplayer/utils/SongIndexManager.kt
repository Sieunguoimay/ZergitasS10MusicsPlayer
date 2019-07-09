package com.sieunguoimay.vuduydu.s10musicplayer.utils

import android.util.Log

const val TAG = "SONG_INDEX_MANAGER"
object SongIndexManager {
  private var songMap:LinkedHashMap<Long,Int>? = null
  private var otherListMap:LinkedHashMap<Int,Long>? = null
  private var goThroughMap:Boolean = false
  private var listSize:Int = 0

  fun create(songMap:LinkedHashMap<Long,Int>,otherListMap:LinkedHashMap<Int,Long>?){
    this.songMap = songMap
    this.otherListMap = otherListMap
  }
  fun reset(goThroughSongMap:Boolean, listSize:Int){
    this.goThroughMap = goThroughSongMap
    this.listSize = listSize
  }

  fun getIndex(id:Int):Int{
    if(goThroughMap&&songMap!=null&&otherListMap!=null){
      Log.d(TAG,"index and size "+id+" "+otherListMap!!.size)
      if(id<otherListMap!!.size){
        Log.d(TAG,"index babe "+id+" "+songMap!![otherListMap!![id]]!!)
        return songMap!![otherListMap!![id]]!!
      }else
        return id
    }
    return id
  }

  fun getNextIndex(index:Int):Int{
    var nextSongIndex = index+1
    if(nextSongIndex>=listSize)
      nextSongIndex -= listSize
    return nextSongIndex
  }
  fun getPrevIndex(index:Int):Int{
    var prevSongIndex = index-1
    if(prevSongIndex<=-1)
      prevSongIndex += listSize
    return prevSongIndex
  }
  fun getRandomIndex():Int{
    return 1
  }
}
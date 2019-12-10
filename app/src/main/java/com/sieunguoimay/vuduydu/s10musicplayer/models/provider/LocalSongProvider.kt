package com.sieunguoimay.vuduydu.s10musicplayer.models.provider

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Category
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Song
import android.R.id.edit
import android.content.SharedPreferences
import android.content.Context.MODE_PRIVATE
import android.R.attr.data



private const val TAG = "LOCAL_SONG_PROVIDER"
object LocalSongProvider{
    fun loadSong(context: Context
                 , songList:ArrayList<Song>, songMap:LinkedHashMap<Long, Int>
                 , albumList :ArrayList<Category>
                 , artistList :ArrayList<Category>
    ):Boolean{
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.DISPLAY_NAME,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.DURATION,
            MediaStore.Audio.AudioColumns.DATE_MODIFIED,
            MediaStore.Audio.AudioColumns.SIZE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ARTIST_ID
        )
        val cursor = context.contentResolver.query(uri,projection,null,null,null)

        val albumMap=LinkedHashMap<Long, Int>()
        val artistMap=LinkedHashMap<Long, Int>()

        if(cursor!=null){
            var count = 0
            while(cursor.moveToNext()){
                val song = Song(cursor.getLong(0),
                        cursor.getLong(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        null,
                        cursor.getLong(5),
                        cursor.getLong(6),
                        cursor.getLong(7)
                    )

                Log.d(TAG,"Duration "+song.duration)
                if(song.duration>100&&!song.path.endsWith("mp4")){
                    Log.d(TAG,"Added "+song.title)
                    songMap.put(cursor.getLong(0),count++)
                    songList.add(song)
                    processForCategory(cursor, song,albumList,albumMap,1,8)
                    processForCategory(cursor, song,artistList,artistMap,9,3)
                }
            }
            return true
        }
        return false
    }
    fun getSongInfoFromUri(context: Context,uri:Uri):Song?{
        val proj = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.DISPLAY_NAME,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.DURATION,
            MediaStore.Audio.AudioColumns.DATE_MODIFIED,
            MediaStore.Audio.AudioColumns.SIZE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ARTIST_ID
        )

        val cursor = context.contentResolver.query(uri,proj,null,null,null)

        if(cursor!=null){
            while(cursor.moveToNext()){
                val song = Song(cursor.getLong(0),
                    cursor.getLong(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    null,
                    cursor.getLong(5),
                    cursor.getLong(6),
                    cursor.getLong(7)
                )
                cursor.close()
                return song
            }
            cursor.close()
        }
        return null
    }
    fun getThumbail(context: Context, albumId: Long) : Bitmap {
        val artworkUri = Uri.parse("content://media/external/audio/albumart")
        val uri = ContentUris.withAppendedId(artworkUri, albumId)
        val input = context.contentResolver.openInputStream(uri)
        val thumb = BitmapFactory.decodeStream(input)
        return thumb
    }

    fun processForCategory(cursor: Cursor, song:Song, list:ArrayList<Category>, map:LinkedHashMap<Long, Int>, indexId:Int, indexTitle:Int){
        val id = cursor.getLong(indexId)
        val title= cursor.getString(indexTitle)
        if(map[id]==null){
            list.add(Category(id,title,null))
            map[id] = list.size-1
        }
        list[map[id]!!].songList.add(song)
    }
}
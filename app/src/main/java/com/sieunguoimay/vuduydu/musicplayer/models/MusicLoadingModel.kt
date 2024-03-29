package com.sieunguoimay.vuduydu.musicplayer.models

import android.content.Context
import com.sieunguoimay.vuduydu.musicplayer.models.data.Category
import com.sieunguoimay.vuduydu.musicplayer.models.data.Song
import com.sieunguoimay.vuduydu.musicplayer.models.tasks.LoadSongDataAsyncTask
import com.sieunguoimay.vuduydu.musicplayer.screens.HomeScreenActivity.MusicsLoadingContract

private const val TAG = "MUSIC_LOADING_MODEL"
class MusicLoadingModel(var context: Context):MusicsLoadingContract.Model{


    //MusicsLoadingContract.Model
    override fun getSong(songList:ArrayList<Song>, songMap:LinkedHashMap<Long,Int>,
                         albumList:ArrayList<Category>, artistList:ArrayList<Category>,
                         callback: LoadSongDataAsyncTask.SongDataCallback){

        LoadSongDataAsyncTask(callback).execute(LoadSongDataAsyncTask.Params(context,songList, songMap,albumList,artistList))
    }
}
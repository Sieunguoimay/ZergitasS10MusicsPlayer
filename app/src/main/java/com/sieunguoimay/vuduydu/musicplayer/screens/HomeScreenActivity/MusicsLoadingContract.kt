package com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity

import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Category
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Song
import com.sieunguoimay.vuduydu.s10musicplayer.models.tasks.LoadSongDataAsyncTask

//the model know nothing about the View
//as well as the presenter
//the presenter know them all
class MusicsLoadingContract {
    interface View{
        fun onLoadedSongList()
        fun showErrorMessage()
        fun onLoadedThumbails()
    }
    interface Presenter{
        fun loadSong(songList:ArrayList<Song>, songMap:LinkedHashMap<Long, Int>,
                     albumList:ArrayList<Category>, artistList:ArrayList<Category>)
    }
    interface Model{
        fun getSong(songList:ArrayList<Song>, songMap:LinkedHashMap<Long, Int>,
                    albumList:ArrayList<Category>,artistList:ArrayList<Category>,
                    callback: LoadSongDataAsyncTask.SongDataCallback)
    }
}
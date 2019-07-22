package com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity

import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Category
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Song
import com.sieunguoimay.vuduydu.s10musicplayer.models.tasks.LoadSongDataAsyncTask

class MusicsLoadingPresenter(
    var view:MusicsLoadingContract.View,
    var model:MusicsLoadingContract.Model
) :MusicsLoadingContract.Presenter, LoadSongDataAsyncTask.SongDataCallback{

    override fun loadSong(songList:ArrayList<Song>, songMap:LinkedHashMap<Long, Int>,
                          albumList:ArrayList<Category>, artistList:ArrayList<Category>) {
        //assume that songs are loaded
        model.getSong(songList,songMap,albumList,artistList,this)
    }

    override fun onThumbailsLoaded() {
        view.onLoadedThumbails()
    }

    override fun onSongsLoaded(songList:ArrayList<Song>) {
        if(songList.size==0)
            view.showErrorMessage()
        else
            view.onLoadedSongList()
    }

}
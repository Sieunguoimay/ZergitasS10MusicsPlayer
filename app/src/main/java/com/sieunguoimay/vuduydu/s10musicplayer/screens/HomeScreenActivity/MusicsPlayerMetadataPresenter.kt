package com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity

class MusicsPlayerMetadataPresenter(
    var view:MusicsPlayerMetadataContract.View,
    var model:MusicsPlayerMetadataContract.Model
):MusicsPlayerMetadataContract.Presenter {
    var currentState:Int = 0
    override fun changeShuffleState() {
        currentState++
        if(currentState>=4)currentState = 0

        model.saveInt(currentState)
        view.updateOnShuffleStateChange(currentState)
    }

    override fun getShuffleState() {
        currentState = model.getInt()
        view.updateOnShuffleStateChange(currentState)
    }
}
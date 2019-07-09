package com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity

class MusicsPlayerMetadataContract {
    interface View{
        fun updateOnShuffleStateChange(state:Int)
    }
    interface Presenter{
        fun changeShuffleState()
        fun getShuffleState()
    }
    interface Model{
        fun saveInt(value:Int)
        fun getInt():Int
    }
}
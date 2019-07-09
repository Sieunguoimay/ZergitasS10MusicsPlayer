package com.sieunguoimay.vuduydu.s10musicplayer.models

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity.MusicsPlayerMetadataContract
import com.sieunguoimay.vuduydu.s10musicplayer.utils.Constants.SHARED_PREFERENCE_NAME
import com.sieunguoimay.vuduydu.s10musicplayer.utils.Constants.SHUFFLE_STATE

class MetadataModel(var context:Context): MusicsPlayerMetadataContract.Model {
    var sharedPreferences:SharedPreferences? = null
    override fun saveInt(value: Int) {
        if(sharedPreferences ==null)
            sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME,MODE_PRIVATE)
        sharedPreferences!!.edit().putInt(SHUFFLE_STATE,value).apply()
    }

    override fun getInt(): Int {
        if(sharedPreferences ==null)
            sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME,MODE_PRIVATE)
        return sharedPreferences!!.getInt(SHUFFLE_STATE,0)
    }

}
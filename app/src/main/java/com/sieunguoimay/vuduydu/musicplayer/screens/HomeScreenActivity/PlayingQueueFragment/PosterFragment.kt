package com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity.PlayingQueueFragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.sieunguoimay.vuduydu.s10musicplayer.R
import com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity.HomeScreenActivity
import com.sieunguoimay.vuduydu.s10musicplayer.visual_effects.WaveformView

class PosterFragment : Fragment(){

    var ivPhoto: ImageView? = null
    var ivOverlay:ImageView? = null
    var wvPlayerScreen:WaveformView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return initView(inflater.inflate(R.layout.fragment_poster, container, false))
    }

    fun initView(view:View):View{
        ivPhoto = view.findViewById(R.id.iv_fragment_poster_photo)
        ivOverlay = view.findViewById(R.id.iv_fragment_poster_overlay)
        wvPlayerScreen = view.findViewById(R.id.wv_fragment_poster)
        return view
    }
}

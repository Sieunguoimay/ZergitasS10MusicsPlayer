package com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.sieunguoimay.vuduydu.s10musicplayer.R
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Song
import com.sieunguoimay.vuduydu.s10musicplayer.screens.adapters.PlaylistSongRecyclerViewAdapter

class ShowSongListFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return initView(inflater.inflate(R.layout.fragment_playing_queue, container, false))
    }

    private fun initView(view : View): View {

        val songList=arguments?.getParcelableArrayList<Song>("songList")
        val playlistIndex = arguments?.getInt("playlistIndex")
        val listType = arguments?.getString("listType")

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_playing_queue_fragment)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = PlaylistSongRecyclerViewAdapter((activity as HomeScreenActivity).playlistSongItemListener
            ,songList!!,playlistIndex!!, listType!!,(activity as HomeScreenActivity))
        setHasOptionsMenu(true)
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home->{
                (activity as HomeScreenActivity).onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
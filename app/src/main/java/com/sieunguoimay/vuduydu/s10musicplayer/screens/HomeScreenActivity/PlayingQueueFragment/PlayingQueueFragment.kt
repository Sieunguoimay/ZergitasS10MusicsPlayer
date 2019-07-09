package com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity.PlayingQueueFragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.sieunguoimay.vuduydu.s10musicplayer.R
import com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity.HomeScreenActivity
import com.sieunguoimay.vuduydu.s10musicplayer.screens.adapters.SimpleItemTouchHelperCallback
import kotlinx.android.synthetic.main.app_bar_home_screen.*

class PlayingQueueFragment: Fragment(), HomeScreenActivity.ServiceSongQueueCallback {


    var recyclerView:RecyclerView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return initView(inflater.inflate(R.layout.fragment_playing_queue, container, false))
    }

    private fun initView(view : View): View {
        val a = (activity as HomeScreenActivity).queueAdapter
        recyclerView = view.findViewById(R.id.rv_playing_queue_fragment)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.itemAnimator = DefaultItemAnimator()
        recyclerView?.adapter = a

        if(a!=null) ItemTouchHelper(SimpleItemTouchHelperCallback(a)).attachToRecyclerView(recyclerView)

        setHasOptionsMenu(true)
        (activity as HomeScreenActivity).supportActionBar?.setTitle(R.string.action_bar_title_playing_queue)
        return view
    }
    override fun onStop(){
        (activity as HomeScreenActivity).queueFragmentOpened = false
        super.onStop()
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

    override fun notifyQueueChanged() {
        recyclerView?.adapter!!.notifyDataSetChanged()
    }
}
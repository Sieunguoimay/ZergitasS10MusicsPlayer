package com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sieunguoimay.vuduydu.s10musicplayer.R
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Song
import com.sieunguoimay.vuduydu.s10musicplayer.screens.adapters.SongRecyclerViewAdapter
import com.sieunguoimay.vuduydu.s10musicplayer.utils.Utils
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment() {
    private var sampleList:ArrayList<Song>? = null
    private var displayList = ArrayList<Song>()
    private var recyclerView:RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return initView(inflater.inflate(R.layout.fragment_search, container, false))
    }

    private fun initView(view : View): View {

        val searchBox = view.findViewById<TextView>(R.id.tv_search_box)
        recyclerView = view.findViewById(R.id.rv_search_fragment)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.adapter = SongRecyclerViewAdapter(activity as HomeScreenActivity,displayList,(activity as HomeScreenActivity))
        setHasOptionsMenu(true)
        sampleList = (activity as HomeScreenActivity).songList
        displayList.addAll(sampleList!!)

        searchBox.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                filterOutTheList(searchBox.text.toString().toLowerCase())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        (activity as HomeScreenActivity).supportActionBar?.setTitle(R.string.title_search_fragment)
        searchBox.requestFocus()
        Utils.showKeyBoard(activity as HomeScreenActivity)
        return view
    }

    override fun onStop() {
        Utils.hideKeyBoard(activity!!)
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
    fun filterOutTheList(key:String){
        displayList.clear()
        if(key.length == 0){
            displayList.addAll(sampleList!!)
        }else{
            for(song in sampleList!!){
                if(song.title.toLowerCase(Locale.getDefault()).contains(key)){
                    displayList.add(song)
                }
            }
        }
        recyclerView!!.adapter!!.notifyDataSetChanged()
    }
}
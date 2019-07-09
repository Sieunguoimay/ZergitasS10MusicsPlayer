package com.sieunguoimay.vuduydu.s10musicplayer.screens.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sieunguoimay.vuduydu.s10musicplayer.R
import com.sieunguoimay.vuduydu.s10musicplayer.utils.Utils
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Song


class PlaylistSongRecyclerViewAdapter(
    var listener:StandardSongViewHolder.ItemClickListener<Pair<String,Pair<Int, Int>>>,
    var songList:ArrayList<Song>,
    var playlistIndex:Int,
    var listType:String,
    var moreOptionListener: StandardSongViewHolder.ItemMoreOptionClickListener
): RecyclerView.Adapter<StandardSongViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): StandardSongViewHolder {
        return StandardSongViewHolder(
            LayoutInflater.from(p0.context).inflate(
                R.layout.standard_row,
                p0,
                false
            ),moreOptionListener
        )
    }
    override fun getItemCount(): Int {
        return songList.size
    }
    override fun onBindViewHolder(p0: StandardSongViewHolder, p1: Int) {
        var song = songList[p1]
        p0.bind(song,p1)
        p0.itemView.setOnClickListener {
            listener.onItemClick(Pair(listType,Pair(p1,playlistIndex)))
        }
    }
}
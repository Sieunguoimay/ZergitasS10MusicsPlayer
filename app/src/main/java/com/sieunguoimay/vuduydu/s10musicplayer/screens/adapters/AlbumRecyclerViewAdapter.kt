package com.sieunguoimay.vuduydu.s10musicplayer.screens.adapters

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sieunguoimay.vuduydu.s10musicplayer.R
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Category
import com.sieunguoimay.vuduydu.s10musicplayer.utils.Utils

class AlbumRecyclerViewAdapter (
    var listener:AlbumListener,
    var albumList:ArrayList<Category>,
    var listType:String,
    var context: Context
): RecyclerView.Adapter<AlbumRecyclerViewAdapter.AlbumViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AlbumViewHolder {
        return AlbumViewHolder(
            LayoutInflater.from(p0.context).inflate(
                R.layout.playlist_row,
                p0, false
            )
        )
    }
    override fun getItemCount(): Int {
        return albumList.size
    }
    override fun onBindViewHolder(p0: AlbumViewHolder, p1: Int) {
        val album = albumList[p1]

        p0.tv_title.text = album.title
        p0.tv_sub.text = album.songList.size.toString()+" song(s)"

        if(album.art!=null)
            p0.iv_poster.setImageBitmap(album.art)
        else
            p0.iv_poster.setImageResource(R.drawable.ic_song)

        p0.cv_card.setOnClickListener {
            listener.onItemClick(p1,albumList,listType)
        }
        p0.cv_option.setOnClickListener{
            listener.onPlaylistOptionClick(p1,it,listType)
        }
        Utils.animateRecyclerView(context,p0.itemView)
    }

    class AlbumViewHolder(view: View): RecyclerView.ViewHolder(view){
        var tv_title: TextView
        var tv_sub: TextView
        var cv_option: CardView
        var iv_poster: ImageView
        var cv_card: CardView
        init{
            tv_title = view.findViewById(R.id.tv_playlist_title)
            tv_sub= view.findViewById(R.id.tv_playlist_sub)
            cv_option = view.findViewById(R.id.cv_playlist_options)
            iv_poster = view.findViewById(R.id.iv_playlist_poster)
            cv_card = view.findViewById(R.id.cv_playlist_card)
        }
    }
    interface AlbumListener{
        fun onItemClick(item:Int, songList:ArrayList<Category>, listType:String)
        fun onPlaylistOptionClick(item:Int,view:View,listType:String)
    }
}
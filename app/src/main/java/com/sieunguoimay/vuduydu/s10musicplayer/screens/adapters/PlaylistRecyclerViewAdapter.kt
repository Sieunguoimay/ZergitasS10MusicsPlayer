package com.sieunguoimay.vuduydu.s10musicplayer.screens.adapters

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.sieunguoimay.vuduydu.s10musicplayer.R
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Playlist
import com.sieunguoimay.vuduydu.s10musicplayer.utils.ListTypes
import com.sieunguoimay.vuduydu.s10musicplayer.utils.Utils
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Song


class PlaylistRecyclerViewAdapter (
    var listener:PlaylistListener,
    var playlistList:ArrayList<Playlist>
    ,var context: Context

): RecyclerView.Adapter<PlaylistRecyclerViewAdapter.PlaylistViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PlaylistViewHolder {
        return PlaylistViewHolder(
            LayoutInflater.from(p0.context).inflate(
                R.layout.playlist_row,
                p0, false
            )
        )
    }
    override fun getItemCount(): Int {
        return playlistList.size
    }
    override fun onBindViewHolder(p0: PlaylistViewHolder, p1: Int) {
        val playlist = playlistList[p1]

        p0.tv_title.text = playlist.title
        p0.tv_sub.text = playlist.songList.size.toString()+" song(s)"

        p0.cv_card.setOnClickListener {
            listener.onItemClick(p1, null,"")
        }
        p0.cv_option.setOnClickListener{
            listener.onPlaylistOptionClick(p1,it)
        }
        if(p1 == 0&&playlist.songList[0].thumb!=null)
            p0.iv_poster.setImageBitmap(playlist.songList[0].thumb)

        Utils.animateRecyclerView(context,p0.itemView)
    }

    class PlaylistViewHolder(view: View): RecyclerView.ViewHolder(view){
        var tv_title: TextView
        var tv_sub: TextView
        var cv_option: CardView
        var iv_poster:ImageView
        var cv_card: CardView

        init{
            tv_title = view.findViewById(R.id.tv_playlist_title)
            tv_sub= view.findViewById(R.id.tv_playlist_sub)
            cv_option = view.findViewById(R.id.cv_playlist_options)
            iv_poster = view.findViewById(R.id.iv_playlist_poster)
            cv_card = view.findViewById(R.id.cv_playlist_card)
        }
    }

    interface PlaylistListener{
        fun onItemClick(item:Int,songList:ArrayList<Song>?, listType:String)
        fun onPlaylistOptionClick(item:Int,view:View)
    }
}
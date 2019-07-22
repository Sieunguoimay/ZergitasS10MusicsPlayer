package com.sieunguoimay.vuduydu.s10musicplayer.screens.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sieunguoimay.vuduydu.s10musicplayer.R
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Song
import com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity.HomeScreenActivity
import com.sieunguoimay.vuduydu.s10musicplayer.utils.Utils

class SongSelectRecyclerViewAdapter(
    var listener:SongSelectListener,
    var favouriteList:ArrayList<Song>
    ,var context: Context

): RecyclerView.Adapter<SongSelectRecyclerViewAdapter.SongSelectViewHolder>() {
    var selectAll:Boolean = false
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SongSelectViewHolder {
        return SongSelectViewHolder(
            LayoutInflater.from(p0.context).inflate(
                R.layout.song_select_row,
                p0, false
            ))
    }
    override fun getItemCount(): Int {
        return favouriteList.size
    }
    override fun onBindViewHolder(p0: SongSelectViewHolder, p1: Int) {
        val song = favouriteList[p1]

        p0.title.text = song.title
        p0.sub.text = song.artist

        p0.iv_more.setImageResource(
            if(HomeScreenActivity.darkModeEnabled){R.drawable.ic_more_white}else{R.drawable.ic_more}
        )



        if(song.thumb!=null)
            p0.thumbail.setImageBitmap(song.thumb)
        else
            p0.thumbail.setImageResource(R.drawable.ic_songs)

        if(song.selected){
            p0.background.setBackgroundColor(ContextCompat.getColor(context,R.color.colorTransparentLight))
            p0.selectState.setImageResource(R.drawable.ic_tick)
        }else{
            p0.background.setBackgroundColor(ContextCompat.getColor(context,R.color.colorAbsoluteTransparency))

            if(HomeScreenActivity.darkModeEnabled)
                p0.selectState.setImageResource(R.drawable.ic_tick_no_dark)
            else
                p0.selectState.setImageResource(R.drawable.ic_tick_no)
        }

        p0.cv_select.setOnClickListener{
            listener.onItemClick(p0,p1,song)
            notifyItemChanged(p1)
        }

        p0.itemView.setOnClickListener {
            listener.onItemClick(p0,p1,song)
            notifyItemChanged(p1)
        }
    }

    class SongSelectViewHolder(
        view: View
    ): RecyclerView.ViewHolder(view){
        var title: TextView
        var sub: TextView
        var thumbail: ImageView
        var selectState: ImageView
        var background:CardView
        var cv_select: CardView
        var cv_more:CardView
        var iv_more:ImageView
        init{
            title = view.findViewById(R.id.tv_song_select_title)
            sub= view.findViewById(R.id.tv_song_select_sub_text)
            thumbail = view.findViewById(R.id.iv_song_select_icon)
            selectState = view.findViewById(R.id.iv_song_select_state)
            background = view.findViewById(R.id.cv_song_select_backgound)
            cv_select = view.findViewById(R.id.cv_song_select_state)
            cv_more = view.findViewById(R.id.cv_song_select_options)
            iv_more = view.findViewById(R.id.iv_song_select_options)

            cv_more.visibility =View.GONE
        }
    }

    interface SongSelectListener{
        fun onItemClick(holder:SongSelectViewHolder,index:Int, song:Song)
    }
}
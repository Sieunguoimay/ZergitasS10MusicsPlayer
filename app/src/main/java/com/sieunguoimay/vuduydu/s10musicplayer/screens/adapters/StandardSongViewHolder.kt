package com.sieunguoimay.vuduydu.s10musicplayer.screens.adapters

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.sieunguoimay.vuduydu.s10musicplayer.R
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Song
import kotlinx.android.synthetic.main.standard_row.*

class StandardSongViewHolder(
    view: View,
    var moreOptionListener: ItemMoreOptionClickListener
):RecyclerView.ViewHolder(view){
    var title: TextView
    var sub: TextView
    var thumbail: ImageView
    var moreOption:CardView
    init{
        title = view.findViewById(R.id.tv_standard_row_title)
        sub= view.findViewById(R.id.tv_standard_row_sub_text)
        thumbail = view.findViewById(R.id.iv_standard_row_icon)
        moreOption = view.findViewById(R.id.cv_standard_row_options)
    }

    fun bind(song: Song, index:Int){
        title.text = song.title
        sub.text = song.artist
        if(song.thumb!=null)
            thumbail.setImageBitmap(song.thumb)
        else
            thumbail.setImageResource(R.drawable.ic_library_music_24dp)
        moreOption.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                moreOptionListener.onItemMoreOptionClick(song,itemView,index)
            }
                //if the service has not been started yet, we push song to the queue on main activity
                //and that is when the player lick the play button for the first time. we will send that queue to the
                //service. and when the user open the queue. they will see their songs over there
                //in case the service has already started. we simply send the song to the queue inside that service
        })
    }
    interface ItemMoreOptionClickListener{
        fun onItemMoreOptionClick(song:Song,view:View, index:Int)
    }

    interface ItemClickListener<T>{
        fun onItemClick(item:T)
    }
}
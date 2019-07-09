package com.sieunguoimay.vuduydu.s10musicplayer.screens.adapters

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.sieunguoimay.vuduydu.s10musicplayer.R
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Song
import com.sieunguoimay.vuduydu.s10musicplayer.utils.Utils
import java.util.*
import kotlin.collections.ArrayList

class PlayingQueueAdapter (
    var listener:QueueListener,
    var favouriteList:ArrayList<Song>
    ,var context: Context

): RecyclerView.Adapter<PlayingQueueAdapter.QueueViewHolder>() ,
    SimpleItemTouchHelperCallback.ItemTouchHelperAdapter
{
    override fun onItemMove(fromPos: Int, toPos: Int) {
        for(i in fromPos..toPos){
            if(fromPos<toPos){
                Collections.swap(favouriteList,i,i+1)
            }else {
                Collections.swap(favouriteList, i, i - 1)
            }

        }
        notifyItemMoved(fromPos,toPos)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): QueueViewHolder {
        return QueueViewHolder(
            LayoutInflater.from(p0.context).inflate(
                R.layout.queue_row,
                p0,
                false
            )
        )
    }
    override fun getItemCount(): Int {
        return favouriteList.size
    }
    override fun onBindViewHolder(p0: QueueViewHolder, p1: Int) {
        val song = favouriteList[p1]
        p0.title.text = song.title
        p0.sub.text = song.artist
        if(song.thumb!=null)
            p0.thumbail.setImageBitmap(song.thumb)
        p0.itemView.setOnClickListener {
            listener.onItemClick(p1)
        }

        p0.dragButton.setOnDragListener { v, event ->
            listener.onDrag(v,event)
            true
        }

        Utils.animateRecyclerView(context,p0.itemView)
    }

    class QueueViewHolder(view: View):RecyclerView.ViewHolder(view){
        var title: TextView
        var sub: TextView
        var thumbail: ImageView
        var dragButton: CardView

        init{
            title = view.findViewById(R.id.tv_queue_row_title)
            sub= view.findViewById(R.id.tv_queue_row_sub_text)
            thumbail = view.findViewById(R.id.iv_queue_row_icon)
            dragButton = view.findViewById(R.id.cv_queue_row_drag)
        }
    }
    interface QueueListener{
        fun onDrag(view:View?, event:DragEvent?)
        fun onItemClick(item:Int)
    }
}
package com.sieunguoimay.vuduydu.s10musicplayer.screens.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.sieunguoimay.vuduydu.s10musicplayer.R
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Song
import com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity.HomeScreenActivity
import com.sieunguoimay.vuduydu.s10musicplayer.services.MusicPlayerService
import com.sieunguoimay.vuduydu.s10musicplayer.utils.Utils
import java.util.*
import kotlin.collections.ArrayList

class PlayingQueueAdapter (
    var listener:QueueListener,
    var queueList:ArrayList<Song>,
    var context: Context,
    var currentSongIndex:Int,
    var fakeAdapter:Boolean = false

): RecyclerView.Adapter<PlayingQueueAdapter.QueueViewHolder>() ,
    SimpleItemTouchHelperCallback.ItemTouchHelperAdapter
{
    var itemTouchHelper = ItemTouchHelper(SimpleItemTouchHelperCallback(this))

    override fun onItemMove(fromPos: Int, toPos: Int) {
        if(toPos<= currentSongIndex){
            return
        }
        if(fromPos<toPos){
            var i = fromPos
            while(i<toPos) {
                Collections.swap(queueList, i, i + 1)
                i++
            }
            if(currentSongIndex>fromPos && currentSongIndex<=toPos){
                currentSongIndex--
                listener.onCurrentSongIndexUpdated(currentSongIndex)
            }
        }else {
            var i = fromPos
            while(i>toPos) {
                Collections.swap(queueList, i, i - 1)
                i--
            }
            if(currentSongIndex>=toPos&&currentSongIndex<fromPos){
                currentSongIndex++
                listener.onCurrentSongIndexUpdated(currentSongIndex)
            }

        }
        if(MusicPlayerService.serviceBound){
            if(currentSongIndex==fromPos){
                currentSongIndex = toPos
                listener.onCurrentSongIndexUpdated(currentSongIndex)
            }

        }
        HomeScreenActivity.queueSetChanged = true
        notifyItemMoved(fromPos, toPos)
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
        return queueList.size
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(p0: QueueViewHolder, p1: Int) {
        val song = queueList[p0.adapterPosition]
        p0.title.text = song.title
        p0.sub.text = song.artist
        if(song.thumb!=null)
            p0.thumbail.setImageBitmap(song.thumb)
        else
            p0.thumbail.setImageResource(R.drawable.ic_songs)


        p0.ivMoreButton.setImageResource(
            if(HomeScreenActivity.darkModeEnabled){R.drawable.ic_more_white}else{R.drawable.ic_more}
        )

        if(currentSongIndex==p0.adapterPosition){
            p0.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.colorTransparentLight))
        }else{
            p0.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.colorAbsoluteTransparency))
        }

        if(p0.adapterPosition>currentSongIndex&&!fakeAdapter){

            p0.dragButton.visibility = View.VISIBLE
            p0.moreButton.visibility = View.VISIBLE
            p0.dragButton.setOnTouchListener { v, event ->
                if(event.actionMasked==MotionEvent.ACTION_DOWN){
                    itemTouchHelper.startDrag(p0)
                }
                false
            }

            p0.moreButton.setOnClickListener{
                listener.onMoreButtonClick(it,p0.adapterPosition)
            }

        }else{
            p0.moreButton.visibility = View.INVISIBLE
            p0.dragButton.visibility = View.INVISIBLE
        }





        p0.itemView.setOnClickListener {
            //here we come the other list
            if(p0.adapterPosition>currentSongIndex+1)
                notifyItemRangeChanged(currentSongIndex,p0.adapterPosition-currentSongIndex)
            if(p0.adapterPosition<currentSongIndex)
                notifyItemRangeChanged(p0.adapterPosition,currentSongIndex-p0.adapterPosition)

            listener.onItemClick(p0.adapterPosition,fakeAdapter)
            currentSongIndex = p0.adapterPosition
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
        var moreButton:CardView
        var ivMoreButton:ImageView
        init{
            title = view.findViewById(R.id.tv_queue_row_title)
            sub= view.findViewById(R.id.tv_queue_row_sub_text)
            thumbail = view.findViewById(R.id.iv_queue_row_icon)
            dragButton = view.findViewById(R.id.cv_queue_row_drag)
            moreButton = view.findViewById(R.id.cv_queue_row_options)
            ivMoreButton = view.findViewById(R.id.iv_queue_row_options)
        }
    }
    interface QueueListener{
        fun onDrag(view:View?, event:DragEvent?)
        fun onItemClick(item:Int,fakeAdapter: Boolean)
        fun onMoreButtonClick(view:View,index:Int)

        fun onCurrentSongIndexUpdated(currentSongIndex: Int)
    }
}



//package com.sieunguoimay.vuduydu.s10musicplayer.screens.adapters
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.support.v4.content.ContextCompat
//import android.support.v7.widget.CardView
//import android.support.v7.widget.RecyclerView
//import android.support.v7.widget.helper.ItemTouchHelper
//import android.view.*
//import android.widget.ImageView
//import android.widget.TextView
//import com.sieunguoimay.vuduydu.s10musicplayer.R
//import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Song
//import com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity.HomeScreenActivity
//import com.sieunguoimay.vuduydu.s10musicplayer.services.MusicPlayerService
//import com.sieunguoimay.vuduydu.s10musicplayer.utils.Utils
//import java.util.*
//import kotlin.collections.ArrayList
//
//class PlayingQueueAdapter (
//    var listener:QueueListener,
//    var queueList:ArrayList<Song>,
//    var service:MusicPlayerService?,
//    var context: Context,
//    var currentSongIndex:Int
//
//): RecyclerView.Adapter<PlayingQueueAdapter.QueueViewHolder>() ,
//    SimpleItemTouchHelperCallback.ItemTouchHelperAdapter
//{
//    var itemTouchHelper = ItemTouchHelper(SimpleItemTouchHelperCallback(this))
//
//    override fun onItemMove(fromPos: Int, toPos: Int) {
//        if(toPos<=service?.currentSongIndex!!){
//            return
//        }
//        if(fromPos<toPos){
//            var i = fromPos
//            while(i<toPos) {
//                Collections.swap(queueList, i, i + 1)
//                i++
//            }
//            if(MusicPlayerService.serviceBound){
//                if(service?.currentSongIndex!!>fromPos&&service?.currentSongIndex!!<=toPos){
//                    service?.currentSongIndex = service?.currentSongIndex!! - 1
//                }
//            }
//        }else {
//            var i = fromPos
//            while(i>toPos) {
//                Collections.swap(queueList, i, i - 1)
//                i--
//            }
//            if(MusicPlayerService.serviceBound){
//                if(service?.currentSongIndex!!>=toPos&&service?.currentSongIndex!!<fromPos){
//                    service?.currentSongIndex = service?.currentSongIndex!! + 1
//                }
//            }
//
//        }
//        if(MusicPlayerService.serviceBound){
//            if(service?.currentSongIndex==fromPos){
//                service?.currentSongIndex = toPos
//            }
//
//        }
//        HomeScreenActivity.queueSetChanged = true
//        notifyItemMoved(fromPos, toPos)
//    }
//
//    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): QueueViewHolder {
//        return QueueViewHolder(
//            LayoutInflater.from(p0.context).inflate(
//                R.layout.queue_row,
//                p0,
//                false
//            )
//        )
//    }
//    override fun getItemCount(): Int {
//        return queueList.size
//    }
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onBindViewHolder(p0: QueueViewHolder, p1: Int) {
//        val song = queueList[p1]
//        p0.title.text = song.title
//        p0.sub.text = song.artist
//
//        if(song.thumb!=null)
//            p0.thumbail.setImageBitmap(song.thumb)
//        else
//            p0.thumbail.setImageResource(R.drawable.ic_library_music_24dp)
//
//
//
//
//
//        if(service?.currentSongIndex==p1){
//            p0.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.colorTransparentLight))
//        }else{
//            p0.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.colorAbsoluteTransparency))
//        }
//
//        if(p1>service?.currentSongIndex!!){
//
//            p0.dragButton.visibility = View.VISIBLE
//            p0.moreButton.visibility = View.VISIBLE
//            p0.dragButton.setOnTouchListener { v, event ->
//                if(event.actionMasked==MotionEvent.ACTION_DOWN){
//                    itemTouchHelper.startDrag(p0)
//                }
//                false
//            }
//
//            p0.moreButton.setOnClickListener{
//                listener.onMoreButtonClick(it,p1)
//            }
//
//        }else{
//            p0.moreButton.visibility = View.INVISIBLE
//            p0.dragButton.visibility = View.INVISIBLE
//        }
//
//
//
//
//
//        p0.itemView.setOnClickListener {
//            listener.onItemClick(p1)
//        }
//        p0.dragButton.setOnDragListener { v, event ->
//            listener.onDrag(v,event)
//            true
//        }
//        Utils.animateRecyclerView(context,p0.itemView)
//    }
//
//    class QueueViewHolder(view: View):RecyclerView.ViewHolder(view){
//        var title: TextView
//        var sub: TextView
//        var thumbail: ImageView
//        var dragButton: CardView
//        var moreButton:CardView
//        init{
//            title = view.findViewById(R.id.tv_queue_row_title)
//            sub= view.findViewById(R.id.tv_queue_row_sub_text)
//            thumbail = view.findViewById(R.id.iv_queue_row_icon)
//            dragButton = view.findViewById(R.id.cv_queue_row_drag)
//            moreButton = view.findViewById(R.id.cv_queue_row_options)
//        }
//    }
//    interface QueueListener{
//        fun onDrag(view:View?, event:DragEvent?)
//        fun onItemClick(item:Int)
//        fun onMoreButtonClick(view:View,index:Int)
//
//        fun onCurrentSongIndexUpdated(currentSongIndex: Int)
//    }
//}
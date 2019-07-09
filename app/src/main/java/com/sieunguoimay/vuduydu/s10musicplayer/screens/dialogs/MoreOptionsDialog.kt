package com.sieunguoimay.vuduydu.s10musicplayer.screens.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.support.v7.widget.CardView
import android.text.Layout
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.PopupWindow
import com.sieunguoimay.vuduydu.s10musicplayer.R
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Playlist
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Song
import com.sieunguoimay.vuduydu.s10musicplayer.utils.ListTypes
import com.sieunguoimay.vuduydu.s10musicplayer.utils.Utils

private const val TAG = "MORE_OPTIONS_DIALOG"
object MoreOptionsDialog {

    fun showDialog(context: Context,view: View){
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
//        dialog.setButton(1,"Add to next", clicklistener )
//        dialog.setButton(2,"Push to queue", clicklistener )
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setTitle("Hello Dialog")
        val wmlp = dialog.window!!.attributes
        wmlp.gravity = Gravity.TOP;Gravity.START
        wmlp.x = view.x.toInt()
        wmlp.y = view.y.toInt()
        dialog.show()
    }
    val clicklistener = object:DialogInterface.OnClickListener{
        override fun onClick(dialog: DialogInterface?, which: Int) {
            when(which){
                1->{
                    Log.d(TAG,"Button 1 on dialog clicked")
                }
            }
        }
    }


    fun showPopupWindow1(context:Context, view:View, song:Song, callback:MoreOptionActionCallback, isBottom:Boolean){
        val inflater  = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.more_option_popup_window,null, false)
        val pw = PopupWindow(v,Utils.DPToPX(100,context), Utils.DPToPX(80,context),true)
//        pw.showAtLocation(view,Gravity.BOTTOM or Gravity.END,view.x.toInt(),view.y.toInt())

        if(isBottom)
            pw.showAsDropDown(view,0,0,Gravity.BOTTOM or Gravity.END)
        else
            pw.showAsDropDown(view,0,0,Gravity.TOP or Gravity.END)

        val addToNext = v.findViewById<CardView>(R.id.cv_more_option_add_to_next)
        val pushToQueue = v.findViewById<CardView>(R.id.cv_more_option_push_to_queue)
        addToNext.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                Log.d(TAG,"Button 1 on dialog clicked")
                callback.onAddToNext(song)
                pw.dismiss()
            }
        })
        pushToQueue.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                Log.d(TAG,"Button 1 on dialog clicked")
                callback.onAddToNext(song)
                pw.dismiss()
            }
        })
    }


    fun showPopupWindow2(context:Context, view:View, index:Int,type:String, callback:MoreOptionActionCallback2, isBottom:Boolean){
        val inflater  = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.more_option_popup_window_2,null, false)
        val height = if(type == ListTypes.LIST_TYPE_PLAYLIST_SONGS){160}else{120}
        val pw = PopupWindow(v,Utils.DPToPX(100,context), Utils.DPToPX(height,context),true)
//        pw.showAtLocation(view,Gravity.BOTTOM or Gravity.END,view.x.toInt(),view.y.toInt())

        if(isBottom)
            pw.showAsDropDown(view,0,0,Gravity.BOTTOM or Gravity.END)
        else
            pw.showAsDropDown(view,0,0,Gravity.TOP or Gravity.END)

        val addToNext = v.findViewById<CardView>(R.id.cv_more_option_2_add_to_next)
        val pushToQueue = v.findViewById<CardView>(R.id.cv_more_option_2_push_to_queue)
        val playAll = v.findViewById<CardView>(R.id.cv_more_option_2_play_all)

        playAll.setOnClickListener {
            callback.onPlayAll(index,type)
        }
        addToNext.setOnClickListener{
            Log.d(TAG,"Button 1 on dialog clicked")
            callback.onAddToNext2(index,type)
            pw.dismiss()
        }
        pushToQueue.setOnClickListener{
            Log.d(TAG,"Button 1 on dialog clicked")
            callback.onPushToQueue2(index,type)
            pw.dismiss()
        }

        val delete = v.findViewById<CardView>(R.id.cv_more_option_2_delete)
        if(type == ListTypes.LIST_TYPE_PLAYLIST_SONGS){
            delete.setOnClickListener{
                callback.onDelete2(index,type)
                pw.dismiss()
            }

        }else{
            delete.visibility = View.GONE
        }
    }
    interface MoreOptionActionCallback{
        fun onAddToNext(song:Song)
        fun onPushToQueue(song:Song)
    }
    interface MoreOptionActionCallback2{
        fun onAddToNext2(index:Int, listType:String)
        fun onPushToQueue2(index:Int, listType:String)
        fun onDelete2(index:Int, listType:String)
        fun onPlayAll(index:Int, listType:String)
    }
}
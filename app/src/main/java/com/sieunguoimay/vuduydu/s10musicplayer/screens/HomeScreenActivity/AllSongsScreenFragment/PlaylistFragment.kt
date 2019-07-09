package com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity.AllSongsScreenFragment

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import com.sieunguoimay.vuduydu.s10musicplayer.R
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Song
import com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity.HomeScreenActivity
import com.sieunguoimay.vuduydu.s10musicplayer.screens.SongSelectActivity

import kotlinx.android.synthetic.main.enter_name_dialog.*

private const val TAG = "PLAYLIST_FRAGMENT"

class PlaylistFragment: Fragment() {
    private val REQUEST_SONG_SELECT_CODE = 122

    private var newPlaylistTitle:String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return initView(inflater.inflate(R.layout.fragment_playlist, container, false))
    }

    private fun initView(view :View):View{
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_playlist_fragment)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        val gridLayoutManager= GridLayoutManager(activity,2)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = (activity as HomeScreenActivity).playlistAdapter

        setHasOptionsMenu(true)
        return view
    }
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.playlist_menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.playlist_menu_create->{
                Log.d(TAG, "Create playlist button pressed")
//                val homeScreenActivity = (activity as HomeScreenActivity)
//                homeScreenActivity.databasePresenter.createPlaylist("Favourite playlist",homeScreenActivity.favouriteList)
                createDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }






    private fun createDialog(){
        var builder = AlertDialog.Builder(activity)
        builder.setTitle("Enter playlist title")
        builder.setView(R.layout.enter_name_dialog)
        builder.setPositiveButton("OK", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val title = (dialog as AlertDialog).findViewById<EditText>(R.id.et_enter_name_dialog)
                Log.d(TAG, "Dialog returned "+title.text)
                newPlaylistTitle = title.text.toString()
                if(newPlaylistTitle==""){
                    Toast.makeText(activity,"Empty name",Toast.LENGTH_SHORT).show()
                    return
                }
                //enter the select song fragment from here...
                startSongSelectingActivityForResult()
            }
        })
        builder.setNegativeButton("Cancel", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.cancel()
            }
        })
        builder.show()
    }

    private fun startSongSelectingActivityForResult(){
        val intent = Intent(activity, SongSelectActivity::class.java)
        intent.putParcelableArrayListExtra("songList",(activity as HomeScreenActivity).songList)
        startActivityForResult(intent,REQUEST_SONG_SELECT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_SONG_SELECT_CODE&&resultCode== Activity.RESULT_OK){
            val selectedSongs = data?.getParcelableArrayListExtra<Song>("selectedSongs")
            Log.d(TAG, "Selected "+selectedSongs?.size+" songs")
            (activity as HomeScreenActivity).databasePresenter.createPlaylist(newPlaylistTitle!!,selectedSongs!!)
        }
    }
}
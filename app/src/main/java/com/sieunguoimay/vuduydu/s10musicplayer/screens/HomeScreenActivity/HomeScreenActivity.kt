package com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.view.GravityCompat
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.sieunguoimay.vuduydu.s10musicplayer.R
import kotlinx.android.synthetic.main.activity_home_screen.*
import kotlinx.android.synthetic.main.app_bar_home_screen.*
import android.view.*
import android.support.design.widget.BottomSheetBehavior
import com.sieunguoimay.vuduydu.s10musicplayer.services.MusicPlayerService
import kotlinx.android.synthetic.main.player_bar_home_screen.*
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.Toast
import com.sieunguoimay.vuduydu.s10musicplayer.models.DatabaseModel
import com.sieunguoimay.vuduydu.s10musicplayer.models.MetadataModel
import com.sieunguoimay.vuduydu.s10musicplayer.models.MusicLoadingModel
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Category
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Playlist
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Song
import com.sieunguoimay.vuduydu.s10musicplayer.notifications.MusicPlayerNotification
import com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity.AllSongsScreenFragment.AllSongsFragment
import com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity.PlayingQueueFragment.PlayingQueueFragment
import com.sieunguoimay.vuduydu.s10musicplayer.screens.adapters.*
import com.sieunguoimay.vuduydu.s10musicplayer.screens.dialogs.ColorChooser
import com.sieunguoimay.vuduydu.s10musicplayer.screens.dialogs.MoreOptionsDialog
import com.sieunguoimay.vuduydu.s10musicplayer.screens.dialogs.TimePickerDialogFragment
import com.sieunguoimay.vuduydu.s10musicplayer.utils.ListTypes
import com.sieunguoimay.vuduydu.s10musicplayer.utils.SongIndexManager
import com.sieunguoimay.vuduydu.s10musicplayer.utils.Utils
import com.sieunguoimay.vuduydu.s10musicplayer.visual_effects.WaveformVisualizer


private const val TAG = "HOME_SCREEN_ACTIVITY"
private const val REQUEST_PERMISSION_CODE = 3

class HomeScreenActivity : AppCompatActivity()
    , NavigationView.OnNavigationItemSelectedListener
    , MusicsLoadingContract.View
    , MusicPlayerService.UpdateViewCallback
    , StandardSongViewHolder.ItemClickListener<Pair<Int,Song>>
    , SeekBar.OnSeekBarChangeListener
    , DatabaseContract.View
    , MusicsPlayerMetadataContract.View
    , StandardSongViewHolder.ItemMoreOptionClickListener
    , MoreOptionsDialog.MoreOptionActionCallback
    , MoreOptionsDialog.MoreOptionActionCallback2
    , TimePickerDialogFragment.MyTimePickerListener
    , ColorChooser.ColorChooserListner
{



    val PERMISSIONS = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.MODIFY_AUDIO_SETTINGS
    )

    private var fragmentCount:Int = 0
    private var presenter: MusicsLoadingContract.Presenter
    var databasePresenter:DatabasePresenter
    private var musicsPlayerMetadataPresenter:MusicsPlayerMetadataPresenter
    private var service:MusicPlayerService? =null



    //this songAdapter is not used in this activity, but in other fragment.
    //we put it here so that it will be created once. not twice...
    //and the listviews that are on other fragments want to display songs , just set their adapter to this
    //and booom!! it is there. :))))
    lateinit var adapter: SongRecyclerViewAdapter
    var songList = ArrayList<Song>()//the one and only songlist
    var songMap = LinkedHashMap<Long,Int>()//the one and only songlist


//    val albumSongList = ArrayList<ArrayList<Song>>()
    val albumList = ArrayList<Category>()
    lateinit var albumAdapter:AlbumRecyclerViewAdapter

    val artistList = ArrayList<Category>()
    lateinit var artistAdapter:AlbumRecyclerViewAdapter


    lateinit var favouriteAdapter: FavouriteRecyclerViewAdapter
    var favouriteList = ArrayList<Song>()
    var favouriteMap = LinkedHashMap<Int ,Long>()

    lateinit var playlistAdapter: PlaylistRecyclerViewAdapter
    var playlistList = ArrayList<Playlist>()
    var playlistSongMapList = ArrayList<LinkedHashMap<Int, Long>>()


    private lateinit var bottomSheet:BottomSheetBehavior<FrameLayout>

    private var aCopyOfCurrentSongIndexToCarryWithinThisClass:Int = 0

    //now we want to provide something for debugging purpose
    //we want to bind to the service on resume and unbind it on stop
    /// which is parallel with the same stuff in onCreate and onDestroy
    private var stopped:Boolean = false

    inner class ListToAddToQueue{
        var listType:String = ListTypes.LIST_TYPE_ALL_SONGS
        var index:Int = 0
        var queueSetChanged:Boolean = false
    }
    private var listToAddToQueue = ListToAddToQueue()
    var queueFragmentOpened:Boolean = false


    var queueAdapter:PlayingQueueAdapter? = null



    init{
        presenter = MusicsLoadingPresenter(this, MusicLoadingModel(this))
        databasePresenter = DatabasePresenter(DatabaseModel(this))
        databasePresenter.view = this
        musicsPlayerMetadataPresenter = MusicsPlayerMetadataPresenter(this, MetadataModel(this))
    }

    //system events

    //here we initialize everything once and for all
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        initView()
        //why do we call this function in onResume?
        //because the activity may had not stopped??? -> that means we should make sure that
        //we unbind it to the service only when we are destroyed. sothat the next time this activity is triggered tot start
        //it will encounter the onCreate event. and that is where we should call this initServiceIfExist()
        initServiceIfExist()

        checkPermissions()
    }

    override fun onResume() {
        super.onResume()
        if(stopped)
            initServiceIfExist()

    }
    override fun onStop() {
        //checking for binding already inside this function
        stopped = true
        super.onStop()
    }


    override fun onDestroy() {
        unbindServiceIfExist()
        super.onDestroy()
    }

    private fun onPermissionGranted(){
        //after the view is ready we run this. be cause the presenter will call this view for displaying something
        //which require the content view to be not null
        //if(!songListGivenByService) {
        presenter.loadSong(songList, songMap, albumList, artistList)
        musicsPlayerMetadataPresenter.getShuffleState()

        adapter = SongRecyclerViewAdapter(this, songList,this)
        albumAdapter = AlbumRecyclerViewAdapter(albumItemListener,albumList,ListTypes.LIST_TYPE_ALBUM_SONGS,this)
        artistAdapter = AlbumRecyclerViewAdapter(albumItemListener,artistList,ListTypes.LIST_TYPE_ARTIST_SONGS,this)

        addHomeScreenFragment()
   // }
    }










    //user interact events
    private val clickListener = object:View.OnClickListener {
        override fun onClick(v: View?) {
            if(MusicPlayerService.serviceBound){
                if(v?.id==cv_player_bar_play.id||v?.id==bt_state.id) {
                    if(service?.musicPlayer!!.isPlaying)
                        service?.pause()
                    else {
                        playMusicInService(service?.currentSongIndex!!)
                    }
                    Log.d(TAG, "button clicked")
                }else {
                    when(v?.id) {
                        bt_next.id -> {
                            nextSongInService()
                            Log.d(TAG, "button clicked")
                        }
                        bt_prev.id -> {
                            prevSongInService()
                            Log.d(TAG, "button clicked")
                        }
                        cv_love.id->{
                            databasePresenter.likeOrUnlike(service?.getCurrentSong()!!)
                        }
                    }
                }
            }
            when(v?.id) {
                iv_hide_player_screen.id->{
                    bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                bottom_sheet_peek.id->{
                    bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                }

                cv_player_screen_playing_queue.id->{
                    openPlayingQueueFragment()
                }
                cv_player_bar_playing_queue.id->{
                    openPlayingQueueFragment()
                }
                cv_shuffle_state.id->{
                    musicsPlayerMetadataPresenter.changeShuffleState()
                }
                bt_equalizer.id->{
                    Toast.makeText(baseContext,"Equalizer available soon", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    //events that occur during application running

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_equalizer-> {

            }
            R.id.nav_sleep_timer-> {
                openTimePicker()
            }
            R.id.nav_skin_theme-> {
                openColorChooser()
            }
            R.id.nav_settings-> {

            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true

    }


    //more optionssssss
    override fun onItemMoreOptionClick(song: Song,view:View, index:Int) {
        MoreOptionsDialog.showPopupWindow1(this@HomeScreenActivity,view,song, this,false)
    }
    override fun onAddToNext(song: Song) {
        if(MusicPlayerService.serviceBound){
            service?.songList!!.add(service?.currentSongIndex!!+1,song)
            if(serviceSongQueueCallback!=null)
                serviceSongQueueCallback?.notifyQueueChanged()
            listToAddToQueue.queueSetChanged = true
        }
    }
    override fun onPushToQueue(song:Song){
        if(MusicPlayerService.serviceBound){
            service?.songList!!.add(song)
            if(serviceSongQueueCallback!=null)
                serviceSongQueueCallback?.notifyQueueChanged()
            listToAddToQueue.queueSetChanged = true
        }
    }

    override fun onAddToNext2(index:Int, listType:String){
        addOrPush(index,listType,false)
    }
    override fun onPushToQueue2(index:Int, listType:String){
        addOrPush(index,listType,true)
    }
    override fun onDelete2(index:Int, listType:String){
        when(listType){
            ListTypes.LIST_TYPE_PLAYLIST_SONGS->{
                Toast.makeText(this,"Deleted playlist "+playlistList[index].title,Toast.LENGTH_SHORT).show()
                databasePresenter.deletePlaylist(playlistList[index])
                playlistList.removeAt(index)
                playlistAdapter.notifyItemRemoved(index)
                listToAddToQueue.queueSetChanged = true
            }
        }
    }
    override fun onPlayAll(index:Int, listType:String){
        listToAddToQueue.listType = listType
        playMusicInService(0)
    }


    private fun addOrPush(index:Int,listType:String,type:Boolean){
        when(listType){
            ListTypes.LIST_TYPE_PLAYLIST_SONGS->{
                addToNextInQueue(playlistList[index].songList,type)
            }
            ListTypes.LIST_TYPE_ALBUM_SONGS->{
                addToNextInQueue(albumList[index].songList,type)
            }
            ListTypes.LIST_TYPE_ARTIST_SONGS->{
                addToNextInQueue(artistList[index].songList,type)
            }
        }

    }
    private fun addToNextInQueue(list:ArrayList<Song>,end:Boolean = false){
        if(MusicPlayerService.serviceBound){
            if(end) {
                service?.songList!!.addAll(list)
            }
            else {
                service?.songList!!.addAll(service?.currentSongIndex!! + 1, list)
            }
            if(serviceSongQueueCallback!=null)
                serviceSongQueueCallback?.notifyQueueChanged()
            Toast.makeText(this,"Added "+list.size+" songs",Toast.LENGTH_SHORT).show()
            listToAddToQueue.queueSetChanged = true
        }
    }


    interface ServiceSongQueueCallback{
        fun notifyQueueChanged()
    }
    var serviceSongQueueCallback:ServiceSongQueueCallback? = null





    //end of seek bar listener//////////////////////////////////////////////////////////
    //click from songList
    override fun onItemClick(item: Pair<Int,Song>) {
        //here is the trigger point of the songIndexManager
        playAllSong(item.first)
        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }
    fun playAllSong(fromIndex:Int){
        listToAddToQueue.listType = ListTypes.LIST_TYPE_ALL_SONGS
        playMusicInService(fromIndex)
    }
    //click from favourite list
    val favouriteItemListener = object:StandardSongViewHolder.ItemClickListener<Pair<Int,Song>>{
        override fun onItemClick(item: Pair<Int, Song>) {
            //here we come the other list
            listToAddToQueue.listType = ListTypes.LIST_TYPE_FAVOURITE_SONGS
            playMusicInService(item.first)
            bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
    fun onPlayAllButtonClickedInFavouriteSongFragment(){
        //here we play the favourite list from 0
        listToAddToQueue.listType = ListTypes.LIST_TYPE_FAVOURITE_SONGS
        playMusicInService(0)
    }

    private val queueItemListener = object: PlayingQueueAdapter.QueueListener {
        override fun onItemClick(item: Int) {
            //here we come the other list
            playMusicInService(item)
        }
        override fun onDrag(view:View?, event:DragEvent?){

        }
    }
    val playlistSongItemListener = object:StandardSongViewHolder.ItemClickListener<Pair<String,Pair<Int, Int>>>{
        override fun onItemClick(item: Pair<String,Pair<Int,Int>>) {
            //here we come the other list
            listToAddToQueue.listType = item.first//ListTypes.LIST_TYPE_PLAYLIST_SONGS
            listToAddToQueue.index = item.second.second
            playMusicInService(item.second.first)
            bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }



    private val playlistItemListener = object:PlaylistRecyclerViewAdapter.PlaylistListener{
        override fun onItemClick(item: Int,songList:ArrayList<Song>?, listType:String) {
            //open the playlist
            Log.d(TAG, "Open the playlist")
            openShowSongListFragment(item,playlistList[item].title,playlistList[item].songList,ListTypes.LIST_TYPE_PLAYLIST_SONGS)
        }
        override fun onPlaylistOptionClick(item: Int,view:View) {
            //do something with the playlist such as delete it
            Log.d(TAG, "Do something with the playlist")
            MoreOptionsDialog.showPopupWindow2(this@HomeScreenActivity,view,item,ListTypes.LIST_TYPE_PLAYLIST_SONGS,this@HomeScreenActivity,false)
        }
    }


    private val albumItemListener = object: AlbumRecyclerViewAdapter.AlbumListener {
        override fun onItemClick(item: Int,list:ArrayList<Category>, listType:String) {
            //open the playlist
            Log.d(TAG, "Open the album or artist")
            openShowSongListFragment(item,list[item].title,list[item].songList,listType)
        }
        override fun onPlaylistOptionClick(item: Int,view:View,listType:String) {
            //do something with the playlist such as delete it
            Log.d(TAG, "Do something with the album or artist")
            MoreOptionsDialog.showPopupWindow2(this@HomeScreenActivity,view,item,listType,this@HomeScreenActivity,false)
        }
    }


    private fun openShowSongListFragment(index:Int,title:String, list:ArrayList<Song>,listType:String){
        val bundle = Bundle()
        bundle.putParcelableArrayList("songList",list)
        bundle.putInt("playlistIndex",index)
        bundle.putString("listType",listType)

        val showSongListFragment = ShowSongListFragment()
        showSongListFragment.arguments = bundle
        replaceFragment(showSongListFragment)

        supportActionBar?.setTitle(title)
    }


    private fun openPlayingQueueFragment(){
        Log.d(TAG,"Touch playing queue")
        if(!queueFragmentOpened) {
            val a = PlayingQueueFragment()
            replaceFragment(a)
            serviceSongQueueCallback = a
            queueFragmentOpened = true
        }
        closeButtonSheet()
    }


    fun doSomethingOnServiceBound(){
        queueAdapter = PlayingQueueAdapter(queueItemListener, service?.songList!!,this)
        service?.waveformVisualizer?.waveformView = wv_player_screen
    }





    //events that occur during application at loading only

    //on loaded song list from MusicLoadingModel content resolver
    //we notify the data set adapter and we trigger the favourite song loading from SQLite to start
    override fun onLoadedSongList() {
        Log.d(TAG,"Loaded all songs with artists "+artistList.size)
        adapter.notifyDataSetChanged()
        databasePresenter.getFavouriteSongs(favouriteList, songList, songMap,favouriteMap)
        databasePresenter.getAllPlaylists(playlistList,playlistSongMapList,songList, songMap)
        SongIndexManager.create(songMap,favouriteMap)
        SongIndexManager.reset(false,songList.size)
    }
    override fun showErrorMessage() {
        Log.d(TAG,"Something went wrong with loading music")
    }
    override fun onLoadedThumbails() {
        Log.d(TAG,"Thumbail loaded")
        adapter.notifyDataSetChanged()
    }



    //override from service to update views
    override fun updateViewOnNewSong(state:Boolean,song: Song) {
        super.updateViewOnNewSong(state,song)

        tv_player_peek_title.text = song.title
        tv_player_screen_title.text = song.title
        tv_player_screen_artist.text = song.artist
        if(song.thumb!=null){
            iv_player_bar_thumb.setImageBitmap(song.thumb)
            iv_player_screen_photo.setImageBitmap(song.thumb)
            Log.d(TAG,"has thumbnail")
//            iv_drawer_thumbnail.setImageBitmap(song.thumb)
        }else{
            iv_player_bar_thumb.setImageResource(R.drawable.ic_library_music_24dp)
            iv_player_screen_photo.setImageResource(R.drawable.ic_library_music_24dp)
        }
        iv_love.setImageResource(if(song.liked){R.drawable.ic_favorite_24dp}else{R.drawable.ic_favorite_border_24dp})
    }

    override fun updateViewOnStateChange(state: Boolean) {
        iv_state.setImageResource(if(state){R.drawable.ic_pause}else{R.drawable.ic_play})
        iv_player_bar_state.setImageResource(if(state){R.drawable.ic_pause}else{R.drawable.ic_play})
    }



    override fun updateOnLikeFromNotification(song: Song) {
        if(song.liked)
            updateOnLike(song)
        else
            updateOnUnlike(song)
    }


    //favourite song implementation
    override fun updateOnLike(song:Song) {
        iv_love.setImageResource(R.drawable.ic_favorite_24dp)


        favouriteList.add(song)
        favouriteMap.put(favouriteList.size-1,song.id)
        favouriteAdapter.notifyItemInserted(favouriteList.size-1)

        //update the notification from here
        if(MusicPlayerService.serviceBound){
            service?.updateLikeOnNotification()
        }
    }

    override fun updateOnUnlike(song:Song) {
        iv_love.setImageResource(R.drawable.ic_favorite_border_24dp)


        for((index, fs) in favouriteList.withIndex()){
            if(fs.id == song.id){
                favouriteList.removeAt(index)
                favouriteMap.remove(index)
                favouriteAdapter.notifyItemRemoved(index)
                break
            }
        }
        //update the notification from here
        if(MusicPlayerService.serviceBound){
            service?.updateLikeOnNotification()
        }
    }

    override fun updateOnFavouritSongsLoaded(count: Int) {
        Log.d(TAG, "Loaded "+count+" favourite songs")
        favouriteAdapter.notifyDataSetChanged()
    }


    override fun updateOnPlaylistsLoaded(count: Int) {
        Log.d(TAG, "Loaded "+count+" playlists")
        playlistAdapter.notifyDataSetChanged()
    }

    override fun showMessageOnPlaylistCreated(playlistId: Long) {
        Log.d(TAG, "Created playlist "+playlistId)
        databasePresenter.getAllPlaylists(playlistList,playlistSongMapList,songList, songMap)
    }


    override fun updateOnShuffleStateChange(state: Int) {
//        cv_shuffle_state.setImageRe
        MusicPlayerService.shuffleState = state
        iv_shuffle_state.setImageResource(when(state){
            0->{R.drawable.loop}
            1->{R.drawable.ic_repeat_one_24dp}
            else->{R.drawable.ic_shuffle_24dp}
//            else->{R.drawable.ic_keyboard_tab_24dp}
        })
    }


    fun nextSongInService(){
        service?.nextSong()
        startServiceIfNotStartedYet()
    }

    fun prevSongInService(){
        service?.prevSong()
        startServiceIfNotStartedYet()
    }

    //call this function on:
    //1. first time clicked on the song
    //2. click on play button on pause????
    //don't worry this function can only be called when the recyclerView was initialized. i.e songList was loaded
    fun playMusicInService(songIndex:Int){
        //how do we suppose to carry the songIndex to the boundEvent
        aCopyOfCurrentSongIndexToCarryWithinThisClass = songIndex

        Log.d(TAG,"playing music babe")

        startAndBindServiceIfNotRunning()

        if(MusicPlayerService.serviceRunning){
            //do we need to change the queue and which list you want to put to queue??
            putSongListToQueue()
            service?.play(songIndex)
            //here we can check for if the notification has been dismissed or not
            startServiceIfNotStartedYet()
        }

    }

    private fun startAndBindServiceIfNotRunning(){
        if(!MusicPlayerService.serviceRunning){
            val serviceIntent = Intent(this,MusicPlayerService::class.java)
            startService(serviceIntent)
            bindService(serviceIntent,serviceConnection,BIND_AUTO_CREATE)
        }
    }
    private fun startServiceIfNotStartedYet(){
        //here we can check for if the notification has been dismissed or not
        if(!MusicPlayerService.serviceStarted) {
            service?.initServiceObject(baseContext,wv_player_screen)
            startService(Intent(this, MusicPlayerService::class.java))
        }

    }






    //if the service is existing.. we don't need to load the songList from model again
    //just take a copy from the service's songList
    //and if the onServiceConnected arrived from here, dont initialize it again any more
    private fun initServiceIfExist(){
        //first instance is created here
        if(MusicPlayerService.serviceRunning){
            //bind to that service
            bindService(Intent(this,MusicPlayerService::class.java),serviceConnection,BIND_AUTO_CREATE)

        }
    }





    private val serviceConnection = object:ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG,"Service disconnected")
        }

        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            Log.d(TAG,"Start passing data from activity to service")

            //now we ready to communicate and use the public functions of Service
            //at this point service must be null, always
            if(service == null){
                service = (binder as MusicPlayerService.MusicPlayerBinder).getServiceInstance()
                service?.callback = this@HomeScreenActivity
            }

            MusicPlayerService.serviceBound = true


            if(!MusicPlayerService.serviceRunning){
                MusicPlayerService.serviceRunning = true
                playMusicOnBeginRunning()
            }else{
                aCopyOfCurrentSongIndexToCarryWithinThisClass=service?.currentSongIndex!!
                updateViewOnNewSong(service?.musicPlayer!!.isPlaying, service?.getCurrentSong()!!)
            }


            if(intent.action== MusicPlayerNotification.INTENT_ACTION_FROM_NOTIFICATION){
                MusicPlayerService.openActivityFromNotification = true
                Log.d(TAG,"Open activity from notification")
            }

            doSomethingOnServiceBound()
        }
    }


    private fun playMusicOnBeginRunning(){
        //come from playMusicInService()
        //means we have to play music for the first time
        service?.initServiceObject(baseContext,wv_player_screen)
        //which list you want to put to queue
        putSongListToQueue()
        service?.play(aCopyOfCurrentSongIndexToCarryWithinThisClass)
    }

    fun putSongListToQueue(){
        //if new song list is the previous one?
        //if not then we add the list to the queue and save the pointer of that list
        if(service?.queuePointer!=listToAddToQueue.listType||service?.queuePointer2!=listToAddToQueue.index||listToAddToQueue.queueSetChanged){
            service?.queuePointer = listToAddToQueue.listType
            service?.queuePointer2 = listToAddToQueue.index
            service?.initSongQueue(getListInQueue())
        }
    }




    private fun getListInQueue():ArrayList<Song>{
        return when(listToAddToQueue.listType){
            ListTypes.LIST_TYPE_ALL_SONGS->{
                songList
            }
            ListTypes.LIST_TYPE_FAVOURITE_SONGS->{
                favouriteList
            }
            ListTypes.LIST_TYPE_PLAYLIST_SONGS->{
                playlistList[listToAddToQueue.index].songList
            }
            ListTypes.LIST_TYPE_ALBUM_SONGS->{
                albumList[listToAddToQueue.index].songList
            }
            ListTypes.LIST_TYPE_ARTIST_SONGS->{
                artistList[listToAddToQueue.index].songList
            }else->{
                songList
            }
        }
    }


    //we unbind the service on Stop
    private fun unbindServiceIfExist(){
        //if any song has been played since this activity started
        //that mean we had bound to the music player service
        //now we have to check if the singleton of that service say it was started
        if(MusicPlayerService.serviceRunning&&MusicPlayerService.serviceBound){
            Log.d(TAG,"Trying to disconnect with the service")

            if(!MusicPlayerService.openActivityFromNotification){
                unbindService(serviceConnection)
                MusicPlayerService.serviceBound = false
                if(!MusicPlayerService.serviceBound)
                    Log.d(TAG,"Unbound babe.......")
                //null so that next time init you will create new service instance again
                service = null

            }else{
                MusicPlayerService.openActivityFromNotification = false
            }
        }

    }





    var visualizerOk:Boolean = false

    private fun checkPermissions(){
        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
            !=PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(this, PERMISSIONS,REQUEST_PERMISSION_CODE)
            }else{
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE)
            }
        }else{
            onPermissionGranted()
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val p = Utils.checkPermission(baseContext,PERMISSIONS)
            if(p!=null){
                requestPermissions(p, REQUEST_PERMISSION_CODE)
            }else{
                visualizerOk = true
            }
        }else
            visualizerOk = true

//            startVisualizer()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            REQUEST_PERMISSION_CODE->{
                if(grantResults.size>0&&grantResults[0]==PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED){
                        Log.d(TAG,"Permission of reading storage granted")

                        onPermissionGranted()
                    }
                }else{
                    Log.d(TAG,"Permission not granted")
                    finish()
                }
            }
        }

    }


    private fun addHomeScreenFragment(){
        val fragment = AllSongsFragment()//HomeScreenFragment()ll_home_screen_fragment
        //fragment.fragmentChangeListener = this
        supportFragmentManager.beginTransaction().add(R.id.ll_home_screen_fragment,fragment,"tag_home_screen_fragment").commit()
        fragmentCount = 1
    }
    private val bottomSheetCallback = object:BottomSheetBehavior.BottomSheetCallback(){
        override fun onSlide(p0: View, p1: Float) {
            Log.d(TAG,"BottomSheet: sliding up "+p1)
            ll_home_screen_fragment.visibility = View.VISIBLE
            ll_player_screen.visibility = View.VISIBLE

            abl_action_bar.y = -p1*abl_action_bar.height.toFloat()

            if(p1==0.0f) abl_action_bar.y = 0.0f

            bottom_sheet_peek.alpha = 1.0f-p1
            ll_player_screen_header.alpha = p1

        }

        override fun onStateChanged(p0: View, p1: Int) {
            when(p1){
                BottomSheetBehavior.STATE_HIDDEN->
                    Log.d(TAG,"BottomSheet: state hidden")
                BottomSheetBehavior.STATE_COLLAPSED->{
                    Log.d(TAG,"BottomSheet: state collapsed")
                    ll_player_screen.visibility = View.GONE
                }
                BottomSheetBehavior.STATE_EXPANDED->{
                    Log.d(TAG,"BottomSheet: state expanded")
                    ll_home_screen_fragment.visibility = View.GONE
//                    ll_home_screen_fragment.y = abl_action_bar.height.toFloat()

                }
            }
        }
    }
    //common use for all fragment having recyclerview
    val recyclerViewScrollListener = object: RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
//            if(dy>0){
//                //scroll up
//                if(abl_action_bar.y>-abl_action_bar.height) {
//                    abl_action_bar.y = Math.max(abl_action_bar.y-dy.toFloat()*.5f,-abl_action_bar.height.toFloat())
//                    ll_home_screen_fragment.translationY = abl_action_bar.y
//                }else{
//                    abl_action_bar.y = -abl_action_bar.height.toFloat()
//                    ll_home_screen_fragment.y = 0.0f
//                }
//            }else{
//                //scroll down
//                if(abl_action_bar.y<0){
//                    abl_action_bar.y =Math.min(0.0f,abl_action_bar.y-dy.toFloat()*.5f)
//                    ll_home_screen_fragment.y = ll_home_screen_fragment.y-dy.toFloat()*.5f
//                }else{
//                    abl_action_bar.y = 0.0f
//                    ll_home_screen_fragment.y = abl_action_bar.height.toFloat()
//                }
//            }
        }

    }

    //this is seekbar listener//////////////////////////////////////////////////////////
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if(MusicPlayerService.serviceBound&&fromUser)
            service?.setProgress(progress.toFloat()/sb_player_screen_seek_bar.max.toFloat())
    }
    override fun updateProgressBar(progress: Float, maxTime:Int) {
        val p = progress*sb_player_screen_seek_bar.max

        pb_player_screen_progress_bar.progress = p.toInt()
        sb_player_screen_seek_bar.progress = p.toInt()

        val currentTime = (progress*maxTime.toFloat()).toInt()
        var m = currentTime/60000
        var s = (currentTime/1000)%60
        var time:String = ""+if(m<10){"0"+m}else{m}+":"+if(s<10){"0"+s}else{s}
        tv_time.text = time

        m = maxTime/60000
        s = (maxTime/1000)%60
        time = ""+if(m<10){"0"+m}else{m}+":"+if(s<10){"0"+s}else{s}
        tv_duration.text = time
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    //from fragments other than HomeScreenFragment, the call to this funtion is triggered
    //that means want to goback
    override fun onBackPressed(){

        if(bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED){
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            return
        }
        //these are fragments other than the HomeScreenFragment
        if(!popBackUptoHomeScreenFragment())
            super.onBackPressed()

    }
    //from fragment usage
    //from the HomeScreenFragment on the home OptionsItem is selected this function is called
    fun openDrawer(){
        if (drawer_layout.isDrawerOpen(GravityCompat.START))
            drawer_layout.closeDrawer(GravityCompat.START)
        else
            drawer_layout.openDrawer(GravityCompat.START)
    }
    fun closeButtonSheet(){
        if(bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun replaceFragment(fragment: Fragment) {
        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.ll_home_screen_fragment,fragment,fragment.toString())
        transaction.addToBackStack(fragment.toString())
        transaction.commit()

        fragmentCount++

        if (fragmentCount>1){
            //these are fragments other than the HomeScreenFragment
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp)
            Log.d(TAG,"More than one fragment "+fragmentCount)
        }
    }

    fun popBackUptoHomeScreenFragment():Boolean{
        if(fragmentCount>1){
            supportFragmentManager.popBackStack()
            fragmentCount--
            Log.d(TAG,"pop fragment "+fragmentCount)
            if(fragmentCount==1){
                //this is homeScreenFragment
                supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu_24dp)
                supportActionBar!!.setTitle(R.string.title_activity_home_screen)
                Log.d(TAG,"Only one fragment left in the stack")
            }
            return true
        }
        return false
    }

    private fun initView(){
        startFlashScreen()

        setSupportActionBar(toolbar)
        nav_view.setNavigationItemSelectedListener(this)

        favouriteAdapter = FavouriteRecyclerViewAdapter(favouriteItemListener, favouriteList,this)
        playlistAdapter = PlaylistRecyclerViewAdapter(playlistItemListener,playlistList,this)

        //init song adapter

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu_24dp)


        cv_player_bar_play.setOnClickListener(clickListener)
        bt_state.setOnClickListener(clickListener)
        bt_next.setOnClickListener(clickListener)
        bt_prev.setOnClickListener(clickListener)
        cv_shuffle_state.setOnClickListener(clickListener)
        bt_equalizer.setOnClickListener(clickListener)
        cv_love.setOnClickListener(clickListener)
        cv_player_screen_playing_queue.setOnClickListener(clickListener)
        cv_player_bar_playing_queue.setOnClickListener(clickListener)
        iv_hide_player_screen.setOnClickListener(clickListener)
        bottom_sheet_peek.setOnClickListener(clickListener)
        sb_player_screen_seek_bar.setOnSeekBarChangeListener(this)
        ll_player_screen.visibility = View.GONE

        drawer_layout.setScrimColor(Color.TRANSPARENT)
        drawer_layout.addDrawerListener(object:ActionBarDrawerToggle(this, drawer_layout,R.string.open,R.string.close){
            override fun onDrawerSlide(drawerView:View, slideOffset:Float){
                super.onDrawerSlide(drawerView, slideOffset)
                val slideX =drawerView.width*slideOffset
                cl_home_screen.translationX = slideX/5*4
                cl_home_screen.scaleX = 1.0f - slideOffset/4
                cl_home_screen.scaleY = 1.0f - slideOffset/4
            }
        })

        bottomSheet = BottomSheetBehavior.from(fl_bottom_sheet)
        bottomSheet.setBottomSheetCallback(bottomSheetCallback)


    }
    private fun startFlashScreen(){
        tv_app_name.alpha = 0.0f
        tv_company_name.alpha = 0.0f
        tv_designer_name.alpha = 0.0f
        tv_app_name.animate().alpha(1.0f).setDuration(500).setListener(object:AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                animate2()
            }
        })
    }
    private fun animate2(){
        tv_company_name.animate().alpha(1.0f).setDuration(500).setListener(object:AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                animate3()
            }
        })
    }
    private fun animate3(){
        tv_designer_name.animate().alpha(1.0f).setDuration(1000).setListener(object:AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                Handler().postDelayed(object:Runnable{
                    override fun run(){
                        closeFlashScreen()
                    }
                },500)
            }
        })
    }
    fun closeFlashScreen(){
        rl_flash_screen.animate().alpha(0.0f).setDuration(500).setListener(object:AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                rl_flash_screen.visibility = View.GONE
            }
        })
    }







    private fun openTimePicker(){
        val timePickerDialogFragment = TimePickerDialogFragment()
        timePickerDialogFragment.initTime(1,0)
        timePickerDialogFragment.show(supportFragmentManager,"time picker")
    }
    override fun setTime(hour: Int, minute: Int) {
        stopMusicHandler.removeCallbacks(stopMusicRunnable)
        stopMusicHandler.postDelayed(stopMusicRunnable,hour.toLong()*60*60*1000+minute.toLong()*60*1000)
        Toast.makeText(this@HomeScreenActivity,"Musics will stop in "+(minute+hour*60)+" minutes",Toast.LENGTH_SHORT).show()
    }
    val stopMusicHandler = Handler()
    val stopMusicRunnable = object:Runnable{
        override fun run(){
            if(MusicPlayerService.serviceBound){
                if(service?.musicPlayer!!.isPlaying){
                    service?.pause()
                }
                service?.musicPlayerNotification?.dismissNotification()
                Toast.makeText(this@HomeScreenActivity,"Musics stopped",Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun openColorChooser(){
        ColorChooser().show(supportFragmentManager,"choose color")
    }
    override fun applyColor(color: Int) {
        Toast.makeText(this@HomeScreenActivity,"How to change theme",Toast.LENGTH_SHORT).show()
    }
}
//                        toolbar.animate()
//                            .translationY(-toolbar.height.toFloat())
//                            .setListener(object : AnimatorListenerAdapter() {
//                                override fun onAnimationEnd(animation: Animator) {
//                                    super.onAnimationEnd(animation)
////                                    toolbar.visibility = View.GONE
//                                    toolbar.tag = "GONE"
//                                }
//                            })
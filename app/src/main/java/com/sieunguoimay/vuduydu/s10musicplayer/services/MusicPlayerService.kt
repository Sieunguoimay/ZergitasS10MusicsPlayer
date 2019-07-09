package com.sieunguoimay.vuduydu.s10musicplayer.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Song
import com.sieunguoimay.vuduydu.s10musicplayer.notifications.MusicPlayerNotification
import com.sieunguoimay.vuduydu.s10musicplayer.tasks.ProgressBarThread
import com.sieunguoimay.vuduydu.s10musicplayer.visual_effects.WaveformView
import com.sieunguoimay.vuduydu.s10musicplayer.visual_effects.WaveformVisualizer
import java.io.File

private val TAG = "MUSIC_PLAYER_SERVICE"
class MusicPlayerService: Service()
    , MediaPlayer.OnPreparedListener
    , MediaPlayer.OnCompletionListener
    , ProgressBarThread.ProgressCallback
{

    companion object{
        var serviceRunning:Boolean = false
        var serviceBound:Boolean = false
        var serviceStarted:Boolean = false

        var openActivityFromNotification:Boolean = false
        var shuffleState:Int = 0
    }
    inner class MusicPlayerBinder: Binder(){
        fun getServiceInstance():MusicPlayerService = this@MusicPlayerService
    }
    var binder:MusicPlayerBinder? = null



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //return super.onStartCommand(intent, flags, startId)
        Log.d(TAG,"Do something with this command")
        serviceStarted = true
        when(intent?.action){
            ServicesActions.STOP_START_SERVICE->{
                stopSelf()
                serviceStarted = false
                musicPlayerNotification = null
            }
            ServicesActions.PLAY_PAUSE->{
                //because to have this notification trigger event
                //the service already played i.e currentSongIndex != -1
                if(musicPlayer!!.isPlaying)
                    pause()
                else {
                    play(currentSongIndex)
                }
            }
            ServicesActions.PREV->{
                prevSong()
            }
            ServicesActions.NEXT->{
                //Log.d(TAG,"PLAYING next song")
                nextSong()
            }
            ServicesActions.LOVE->{
                //trigger someone from here
                likeSong()
            }
        }

        return START_STICKY
    }
    //we allow humans to bind to this service
    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG,"Binding babe")
        if(binder==null){
            binder = MusicPlayerBinder()
        }
        return binder
    }


    override fun onDestroy() {
        Log.d(TAG,"Service destroyed")
        serviceRunning = false
        musicPlayer?.stop()
        progressBarThread?.running = false
        progressBarThread?.join()
        waveformVisualizer.stopVisualizer()
        super.onDestroy()
    }




    lateinit var context:Context
    var musicPlayerNotification:MusicPlayerNotification? = null
    var musicPlayer: MediaPlayer? = null
    var songList = ArrayList<Song>()
    var currentSongIndex:Int = 0
    private var readyForChangeSong:Boolean = true
    var callback:UpdateViewCallback?= null
    private var progressBarThread: ProgressBarThread? = null

    var queuePointer:String = ""
    var queuePointer2:Int = -1

    val waveformVisualizer = WaveformVisualizer()
    var initSongQueueFlag:Boolean = false
    //after this point we use an instance of this class to access from the main activity
    //it is the same as one single separate object created from this class
    //since we cannot pass the parameters into the constructor
    fun initServiceObject(context:Context,waveformView: WaveformView){
        musicPlayerNotification = MusicPlayerNotification(context,this)
        this.context = context
        //thread exist parallel with the service
        progressBarThread = ProgressBarThread(this)
        progressBarThread?.start()
    }

    fun initSongQueue(list:ArrayList<Song>){
        initSongQueueFlag = true
        songList.clear()
        for(song in list) songList.add(song)
    }

    //this funtion is called when:
        //1. the activity has Stopped and the mediaPlayer pause event is triggered
        //2. the the mediaPlayer has paused and the activity stop event is triggered

    fun stopForeground() = musicPlayerNotification!!.stopForeground()
    fun startForeground(){
        if(musicPlayerNotification!=null) {
            musicPlayerNotification!!.startForeground()
            Log.d(TAG, "Start foreground ")
        }
    }

    //this method is called from Thread
    override fun updateProgress():Float{
        var progress = 0.0f
        if(songList.size>0) {
            val maxTime = songList[currentSongIndex].duration
            val currentTime = musicPlayer!!.currentPosition
            progress = (currentTime.toFloat()) / (maxTime.toFloat())

            callback?.updateProgressBar(progress,maxTime.toInt())

        }
        return progress
    }

    //call this from out side
    fun setProgress(progress:Float){
        musicPlayer?.seekTo((progress*songList[currentSongIndex].duration.toFloat()).toInt())
    }





    //control the media player
    //if the first song played then we simply play it
    //if the current song is played, then we do nothing
    //if the passed in song is different from the current song then we play that new song
    //and don't forget to stop it if it is running
    fun play(index:Int = 0){


        if(songList.size>0){
            var condition = initSongQueueFlag||currentSongIndex!=index
            if(!condition)
                if(currentSongIndex==index)
                    if(songList[currentSongIndex]!=songList[index]||shuffleState==1)
                        condition = true

            if(condition){
                initSongQueueFlag = false
                if(!readyForChangeSong){
                    Log.d(TAG,"Not able to enter playing music due to too fast")
                    return
                }
                readyForChangeSong = false
                Log.d(TAG,"Enter playing music")
                //here new song come in
                //if there exist a song was playing we must stop it
                if(musicPlayer!=null) {
                    if (musicPlayer?.isPlaying!!) {
                        musicPlayer?.reset()
                    }else{
                        startForeground()
                    }
                }
                currentSongIndex = index
                val uri: Uri = Uri.fromFile(File(getCurrentSong().path))
                musicPlayer= MediaPlayer().apply{
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                    setDataSource(context.applicationContext,uri)
                    prepareAsync()
                    setOnPreparedListener(this@MusicPlayerService)
                    setOnCompletionListener(this@MusicPlayerService)
                }
                waveformVisualizer.startVisualizer(musicPlayer!!.audioSessionId)

                callback?.updateViewOnNewSong(true,getCurrentSong())
            }else{
                //here the same song index is passed in
                //there is 2 possible situations: paused song,or still playing song
                if(!musicPlayer?.isPlaying!!){
                    musicPlayer?.start()
                    startForeground()
                    Log.d(TAG,"Resume playing and start foreground again")
                }//else ignore it
                callback?.updateViewOnStateChange(true)
            }

            musicPlayerNotification?.updateNotificationView(getCurrentSong(), true)


        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        musicPlayer?.start()
        Log.d(TAG,"Start new song and run thread")
        //this is the time when the media player is ready for changing song
        Handler().postDelayed(object: Runnable{
            override fun run(){
                readyForChangeSong = true
                Log.d(TAG,"On thread finish: ready for change song")
            }
        }, 100)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        //auto next
            nextSong()
    }

    //simply pause the mediaplayer
    //please make sure that you call this function when the song is playing
    fun pause(){
        Log.d(TAG,"Paused foreground stoped")
        //on pause we trigger the stopForeground()
        stopForeground()
        //and pause the music
        musicPlayer?.pause()
        callback?.updateViewOnStateChange(false)
        musicPlayerNotification?.updateNotificationView(songList[currentSongIndex],false)

    }
    //simply call the next song by giving the right index
    fun nextSong(){
        if(!readyForChangeSong)return

        Log.d(TAG,"PLAYING next song")
        var nextSongIndex = shuffleInNextSong(shuffleState)
        if(nextSongIndex>=songList!!.size)
            nextSongIndex -= songList!!.size

        play(nextSongIndex)
    }
    //simply call the next song by giving the right index
    fun prevSong(){
        if(!readyForChangeSong)return
        Log.d(TAG,"PLAYING previous song")

        var prevSongIndex = currentSongIndex-1

        if(prevSongIndex<=-1)
            prevSongIndex += songList!!.size

        play(prevSongIndex)
    }

    fun likeSong(){
        Log.d(TAG,"Love a song from notification")
        songList[currentSongIndex].liked = !songList[currentSongIndex].liked
        callback?.updateOnLikeFromNotification(songList[currentSongIndex])
    }

    fun updateLikeOnNotification(){
        Log.d(TAG,"Love a song from player screen up to notification")
        musicPlayerNotification?.updateNotificationView(songList[currentSongIndex],musicPlayer?.isPlaying!!)
    }

    fun getCurrentSong():Song{
        return songList[currentSongIndex]
    }

    interface UpdateViewCallback{
        fun updateViewOnNewSong(state:Boolean,song:Song){
            updateViewOnStateChange(state)
        }
        fun updateViewOnStateChange(state:Boolean)
        fun updateOnLikeFromNotification(song:Song)

        //range 0.0f-1.0f
        fun updateProgressBar(progress:Float, maxTime:Int)

    }

    fun shuffleInNextSong(shuffleState:Int):Int{
        return when(shuffleState){
            0->{
                currentSongIndex+1
            }
            1->{
                currentSongIndex
            }
            else->{
                var randomIndex:Int
                do{
                    randomIndex = (Math.random()*(songList.size-1).toFloat()).toInt()
                }while(randomIndex == currentSongIndex)
                randomIndex
            }
        }
    }

}

/*
* the story of service:
*
* when user clicks on the first song for playing. The service will be start in the Start Mode
* at the same time, the activity will be bound to the service.
*
*
* The service keeps playing music until the mediaPlayer is paused and the notification is swiped away
* At that point the service will be destroyed and the static flag will be set to false
*
* */

/*
* the story of media player:
* when the user clicks on the play button, it trigger the media player to play
* */

//  to stop the foreground you have to do something
//    //this is a demo to stop the foreground
//    service?.musicPlayerNotification!!.stopForeground()

//if we pause and nothing is bound to the service then we can destroy the service

/*
* The story of the media player:
*
* the media player is created when there is a song to play
* otherwise it will not exist. the question is what if we access the interface while there is no song in the media player
*
* when we press play for the first time the mediaplayer is created. from that time on ward we can access it
* the assumption is that. no stop . no destroy if playing
* */
package com.sieunguoimay.vuduydu.s10musicplayer.notifications

import android.app.*

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import android.widget.RemoteViews
import com.sieunguoimay.vuduydu.s10musicplayer.R
import com.sieunguoimay.vuduydu.s10musicplayer.models.data.Song
import com.sieunguoimay.vuduydu.s10musicplayer.screens.HomeScreenActivity.HomeScreenActivity
import com.sieunguoimay.vuduydu.s10musicplayer.services.MusicPlayerService
import com.sieunguoimay.vuduydu.s10musicplayer.services.ServicesActions


//this notification will live along with the service as a loyal slave
const val NOTIFICATION_ID = 144
const val TAG ="MUSIC_PLAYER_NOTI"


class MusicPlayerNotification(
        var context:Context,
        var service: Service
) {
    companion object{
        val INTENT_ACTION_FROM_NOTIFICATION = "from_notification"
    }

    private var notification:Notification? = null
    private lateinit var remoteViews:RemoteViews
    init{
        //here we create the notification

        //call this one time, but more no problem
        createNotificationChannel()
        remoteViews = createRemoteViews()
        // Create an explicit intent for an Activity in your app
        val intent = Intent(context, HomeScreenActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context, context.resources.getString(R.string.CHANNEL_ID))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle("My notification")
//            .setContentText("Hello World!")
            .setCustomBigContentView(updateRemoteViews(null))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setDeleteIntent(PendingIntent.getBroadcast(context.applicationContext,0, Intent(context, NotificationDismissReceiver::class.java), 0))
            .setAutoCancel(false)
            .setVisibility(Notification.VISIBILITY_PUBLIC)

        notification = builder.build()
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(NOTIFICATION_ID, notification!!)
        }

        //whenever you init you play a song
        startForeground()
    }
    fun updateNotificationView(song: Song, state:Boolean){
        val intent = Intent(context, HomeScreenActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        }
        intent.action = INTENT_ACTION_FROM_NOTIFICATION

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context, context.resources.getString(R.string.CHANNEL_ID))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCustomBigContentView(updateRemoteViews(song,state))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setDeleteIntent(PendingIntent.getBroadcast(context.applicationContext,0, Intent(context, NotificationDismissReceiver::class.java), 0))
            .setAutoCancel(false)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
        notification = builder.build()
        with(NotificationManagerCompat.from(context)) { notify(NOTIFICATION_ID, notification!! )}

    }

    fun dismissNotification(){
        with(NotificationManagerCompat.from(context)) { cancel(NOTIFICATION_ID)}
    }


    fun stopForeground(){
        service.stopForeground(false)
        Log.d(TAG,"Stop the foreground")
    }
    fun startForeground(){
        service.startForeground(NOTIFICATION_ID,notification)
        Log.d(TAG,"Start the foreground")
    }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.resources.getString(R.string.channel_name)
            val descriptionText = context.resources.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(context.resources.getString(R.string.CHANNEL_ID), name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun createRemoteViews(): RemoteViews {
        //now we create a remoteViews that has assign all the intent action to the buttons of the notification layout
        //those intents will then be captured by the MusicPlayerService for controlling the mediaPlayer
        val remoteViews = RemoteViews(context.packageName,R.layout.notification_music_player)
        remoteViews.setOnClickPendingIntent(R.id.bt_noti_state,createPendingIntentWithAction(ServicesActions.PLAY_PAUSE))
        remoteViews.setOnClickPendingIntent(R.id.bt_noti_next,createPendingIntentWithAction(ServicesActions.NEXT))
        remoteViews.setOnClickPendingIntent(R.id.bt_noti_prev,createPendingIntentWithAction(ServicesActions.PREV))
        remoteViews.setOnClickPendingIntent(R.id.bt_noti_love,createPendingIntentWithAction(ServicesActions.LOVE))
        return remoteViews
    }
    private fun updateRemoteViews(song:Song?=null, state:Boolean = true):RemoteViews{
        if(song==null)return remoteViews
        remoteViews.setTextViewText(R.id.tv_noti_song_title,song.title)
        if(song.thumb!=null)
            remoteViews.setImageViewBitmap(R.id.iv_noti_thumb,song.thumb)
        else
            remoteViews.setImageViewResource(R.id.iv_noti_thumb,R.drawable.ic_library_music_24dp)

        remoteViews.setImageViewResource(R.id.iv_noti_state,if(state){R.drawable.ic_pause}else{R.drawable.ic_play})
        remoteViews.setImageViewResource(R.id.iv_noti_love,if(song.liked){R.drawable.ic_favorite_24dp}else{R.drawable.ic_favorite_border_24dp})
        return remoteViews
    }

    private fun createPendingIntentWithAction(action:String):PendingIntent{
        val intent = Intent(context, MusicPlayerService::class.java)
        intent.action = action
        return PendingIntent.getService(context,0,intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}
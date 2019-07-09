package com.sieunguoimay.vuduydu.s10musicplayer.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sieunguoimay.vuduydu.s10musicplayer.services.MusicPlayerService
import com.sieunguoimay.vuduydu.s10musicplayer.services.ServicesActions

class NotificationDismissReceiver:BroadcastReceiver(){
	override fun onReceive(context: Context?, intent: Intent?) {

		//this is the holy place where the service is triggered to close
		val intent = Intent(context, MusicPlayerService::class.java)
		intent.action = ServicesActions.STOP_START_SERVICE
		intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
		context?.startService(intent)

		Log.d("NOTI_RECEIVER","Stop service")
	}
}
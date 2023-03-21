package org.youpip.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.youpip.app.MainActivity
import kotlin.system.exitProcess

@Suppress("NAME_SHADOWING")
class NotificationReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            ApplicationClass.EXIT -> {
                MainActivity.musicService!!.stopForeground(true)
                exitProcess(0)
            }
            ApplicationClass.PAUSE ->{
                MainActivity.player.pause()
                MainActivity.musicService!!.showNotification(false)
            }
            ApplicationClass.PLAY ->{
                MainActivity.player.play()
                MainActivity.musicService!!.showNotification(true)
            }
        }
    }
}
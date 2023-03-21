package org.youpip.app.service

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class ApplicationClass:Application() {
    companion object {
        const val CHANNEL_ID = "channel_1"
        const val PLAY = "PLAY"
        const val PAUSE = "PAUSE"
        const val EXIT = "EXIT"
        const val ONLY_MUSIC = "ONLY_MUSIC"
    }

    override fun onCreate() {
        super.onCreate()
        val notificationChannel = NotificationChannel(CHANNEL_ID,"Now Playing Song",NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.description = "This is a important channel for showing song!!!"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}
package org.youpip.app.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.ExoPlayer
import org.youpip.app.MainActivity
import org.youpip.app.R

class MusicService : Service() {
    private var myBinder = MyBinder()
    private lateinit var mediaSession: MediaSessionCompat
    var mediaPlayer: MediaPlayer? = null
    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    fun showNotification(isPlay: Boolean = false) {
        if(android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.R){
            return
        }
        println("====>load")
        val exitIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = getBroadcast(baseContext, 0, exitIntent, FLAG_IMMUTABLE)
        val pauseIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PAUSE)
        val pausePendingIntent = getBroadcast(baseContext, 1, pauseIntent, FLAG_IMMUTABLE)

        val playIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = getBroadcast(baseContext, 1, playIntent, FLAG_IMMUTABLE)
        println("====>load2")
        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentTitle(MainActivity.videoP?.title)
            .setContentText(MainActivity.videoP?.chanel_name)
            .setSmallIcon(R.drawable.logo_512_png)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.only_color))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
        if (isPlay) {
            notification.addAction(
                R.drawable.ic_baseline_pause_circle_outline_24,
                "Pause",
                pausePendingIntent
            )
        } else {
            notification.addAction(
                R.drawable.ic_baseline_play_circle_outline_24,
                "Play",
                playPendingIntent
            )
        }
        println("====>load3")
        notification.addAction(R.drawable.ic_baseline_close_24, "Close", exitPendingIntent)


        println("====>VersionIos${android.os.Build.VERSION.SDK_INT}")



        notification.setStyle(
            androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(3)
                .setCancelButtonIntent(null)
                .setMediaSession(mediaSession.sessionToken)
        )

        startForeground(4, notification.build())

    }
}
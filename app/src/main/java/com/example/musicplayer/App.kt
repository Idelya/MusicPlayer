package com.example.musicplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class App: Application() {

    private val CHANNEL_ID = "notificationsChannelMusicApp"
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel: NotificationChannel = NotificationChannel(
                CHANNEL_ID,
                "music_player",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "music player"
            val manager: NotificationManager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
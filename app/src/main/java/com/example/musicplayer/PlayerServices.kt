package com.example.musicplayer

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.MediaPlayer
import android.media.MediaPlayer.*
import android.os.Binder
import java.io.IOException


class PlayerServices: Service() {

    var mediaPlayer: MediaPlayer? = null
    val mediaFile: String? = null
    lateinit var musicList: List<AudioModel>

    override fun onCreate() {
        super.onCreate()
        musicList
    }
    // Binder given to clients
    private val iBinder: PlayerBinder = PlayerBinder()
    override fun onBind(intent: Intent?): PlayerBinder {
        return iBinder
    }

    inner class PlayerBinder : Binder() {
        val service: PlayerServices
            get() = this@PlayerServices
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initMediaPlayer() {
    }

}

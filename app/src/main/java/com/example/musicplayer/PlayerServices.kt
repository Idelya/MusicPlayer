package com.example.musicplayer

import android.app.Application
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.MediaPlayer
import android.media.MediaPlayer.*
import android.net.Uri
import android.os.Binder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import java.io.IOException

val CHANNEL_ID = "notificationsChannelMusicApp"

class PlayerServices: Service() {

    var mediaPlayer: MediaPlayer? = null

    private val _currentsong = MutableLiveData<AudioModel>()

    val currentsong: LiveData<AudioModel>
        get() = _currentsong

    lateinit var musicList: List<AudioModel>

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private val iBinder: PlayerBinder = PlayerBinder()
    override fun onBind(intent: Intent?): PlayerBinder {
        return iBinder
    }

    inner class PlayerBinder : Binder() {
        val service: PlayerServices
            get() = this@PlayerServices
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music player")
            .setContentText(currentsong.value?.title)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        return START_NOT_STICKY
    }

    fun initMedia() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(baseContext, Uri.parse(currentsong.value?.filePath))
        mediaPlayer?.start()
        mediaPlayer?.setOnCompletionListener {
                media -> toNext()
        }
    }

    fun toNext() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        val currentId = musicList.indexOf(currentsong.value)
        val newSong = musicList[(currentId+1)%musicList.size]

        mediaPlayer = MediaPlayer.create(baseContext, Uri.parse(newSong.filePath))
        mediaPlayer?.start()

        _currentsong.value = newSong

    }

    fun toPrevious() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        val currentId = musicList.indexOf(currentsong.value)
        val newSong = musicList[(currentId+1)%musicList.size]

        mediaPlayer = MediaPlayer.create(baseContext, Uri.parse(newSong.filePath))
        mediaPlayer?.start()

        _currentsong.value = musicList[(currentId-1)%musicList.size]
    }

    fun setSong(audio:AudioModel) {
        _currentsong.value = audio

    }

    fun showNotification() {

    }

}

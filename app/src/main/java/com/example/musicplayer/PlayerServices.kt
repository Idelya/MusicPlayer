package com.example.musicplayer

import android.R
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


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
        mediaPlayer?.stop()
        mediaPlayer?.release()
        stopForeground(true)
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
        startForeground(1, setNotificationView())
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
        updateNotification()
    }

    fun toNext() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        val currentId = musicList.indexOf(currentsong.value)
        val newSong = musicList[(currentId+1)%musicList.size]

        mediaPlayer = MediaPlayer.create(baseContext, Uri.parse(newSong.filePath))
        mediaPlayer?.start()

        _currentsong.value = newSong
        updateNotification()

    }

    fun toPrevious() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        val currentId = musicList.indexOf(currentsong.value)
        val newSong = musicList[(currentId+1)%musicList.size]

        mediaPlayer = MediaPlayer.create(baseContext, Uri.parse(newSong.filePath))
        mediaPlayer?.start()

        _currentsong.value = musicList[(currentId-1)%musicList.size]
        updateNotification()

    }

    fun setSong(audio:AudioModel) {
        _currentsong.value = audio
        updateNotification()
    }


    private fun setNotificationView(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music player")
            .setSmallIcon(R.drawable.ic_media_play)
            .setContentText(currentsong.value?.title)
            .setContentIntent(pendingIntent)
            .build()
        return notification
    }

    private fun updateNotification() {
        val notification: Notification = setNotificationView();
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(1, notification)
    }

}

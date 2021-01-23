package com.example.musicplayer.ui.main

import android.content.ContentResolver
import android.content.Context
import android.media.MediaPlayer
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.AudioModel


const val ACTION_NOTIFICATION = "notification action"
const val UPDATE_NOTIFY = "update notification"

class MainViewModel : ViewModel() {
    val player: MediaPlayer = MediaPlayer()
    var _musicList: MutableLiveData<List<AudioModel>> = MutableLiveData(mutableListOf())

    val musicList: LiveData<List<AudioModel>>
        get() = _musicList


    private val _current_audio = MutableLiveData<AudioModel>()

    val current_audio: LiveData<AudioModel>
        get() = _current_audio

    private val _is_played = MutableLiveData<Boolean>()

    val is_played: LiveData<Boolean>
        get() = _is_played

    fun openPlayer(audio: AudioModel) {
        if(_current_audio.value != audio) {
            player.prepareAsync();
            player.reset()
            _current_audio.value = audio
            player.setDataSource(audio.filePath)
            _is_played.value = false
        }
    }

    fun closePlayer() {
        _current_audio.value = null
    }

    fun getAudioFiles(context: Context) {
        val tmpList: MutableList<AudioModel> = mutableListOf()
        val contentResolver: ContentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val title =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val artist =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val duration =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val modelAudio = AudioModel(title, url, artist, duration.toLong())
                tmpList.add(modelAudio)
            } while (cursor.moveToNext())
        }

        _musicList = MutableLiveData(tmpList)
    }

    fun playAudio() {
        _is_played.value = true
        player.start()
    }

    fun pauseAudio() {
        _is_played.value = false
        player.pause()
    }
}
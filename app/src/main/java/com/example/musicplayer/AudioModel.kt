package com.example.musicplayer

import android.media.MediaPlayer
import android.net.Uri
import java.time.Duration

data class AudioModel(val title: String, val filePath: String, val artist: String, val duration: Long) {
}

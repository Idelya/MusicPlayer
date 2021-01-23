package com.example.musicplayer.ui.main

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.musicplayer.AudioModel
import com.example.musicplayer.MainActivity
import com.example.musicplayer.R


class PlayerFragment : Fragment() {

    private var mediaPlayer: MediaPlayer? = null

    lateinit var musicList: List<AudioModel>
    var songId: Int = -1
    lateinit var song: AudioModel
    lateinit var playIB: ImageButton
    lateinit var seekbar: SeekBar
    lateinit var durationTV: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player, container, false)
        val activity: MainActivity = this.activity as MainActivity
        musicList = activity.musicList
        song = activity.song
        songId = musicList.indexOf(song)
        initPlayer()
        val nameTV = view.findViewById<TextView>(R.id.titleTV)
        seekbar = view.findViewById<SeekBar>(R.id.seekbar)
        durationTV = view.findViewById<TextView>(R.id.durationTV)
        playIB = view.findViewById<ImageButton>(R.id.play)


        nameTV.text = song.title

        if (mediaPlayer != null) {
            seekbar.max = mediaPlayer?.duration ?: 0
            playIB.setImageResource(R.drawable.ic_pause_24px)
        }

        seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress * 1000)
                }
            }
        })

        Thread(Runnable {
            while (mediaPlayer != null) {
                try {
                    var msg = Message()
                    msg.what = mediaPlayer!!.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }
            }
        }).start()


        return view
    }

    @SuppressLint("HandlerLeak")
    var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            var currentPosition = msg.what
            seekbar.progress = currentPosition
            durationTV.text = createTimeLabel(currentPosition)
        }
    }


    fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        var min = time / 1000 / 60
        var sec = time / 1000 % 60

        timeLabel = "$min:"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }

    private fun initPlayer(){
        val uri = Uri.parse(song.filePath)
        if(mediaPlayer!=null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
        mediaPlayer = MediaPlayer.create(this.context,uri)
        mediaPlayer?.start();
    }

    private fun pause(){
        mediaPlayer?.pause()
        playIB.setImageResource(R.drawable.ic_play_arrow_24px)
    }

    private fun play(){
        mediaPlayer?.start()
        playIB.setImageResource(R.drawable.ic_pause_24px)
    }
}

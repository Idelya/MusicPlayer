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
import androidx.lifecycle.Observer
import com.example.musicplayer.AudioModel
import com.example.musicplayer.MainActivity
import com.example.musicplayer.PlayerServices
import com.example.musicplayer.R
import java.util.*


class PlayerFragment : Fragment() {

    lateinit var musicList: List<AudioModel>
    lateinit var song: AudioModel
    lateinit var playIB: ImageButton
    lateinit var nextIB: ImageButton
    lateinit var prevIB: ImageButton
    lateinit var seekbar: SeekBar
    lateinit var durationTV: TextView
    lateinit var nameTV: TextView
    lateinit var playThread: Thread
    lateinit var nextThread: Thread
    lateinit var prevThread: Thread
    lateinit var service: PlayerServices

    override fun onResume() {
        super.onResume()
        playThread()
        prevThread()
        nextThread()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player, container, false)
        val activity: MainActivity = this.activity as MainActivity

        musicList = activity.service?.musicList!!
        song = activity.service?.currentsong?.value!!

        service = activity.service!!
        service.initMedia()

        nameTV = view.findViewById<TextView>(R.id.titleTV)
        seekbar = view.findViewById<SeekBar>(R.id.seekbar)
        durationTV = view.findViewById<TextView>(R.id.durationTV)
        playIB = view.findViewById<ImageButton>(R.id.play)
        nextIB = view.findViewById<ImageButton>(R.id.nextIB)
        prevIB = view.findViewById<ImageButton>(R.id.prevIB)


        nameTV.text = song.title

        if (service.mediaPlayer != null) {
            seekbar.max = service.mediaPlayer?.duration ?: 0
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
                    service.mediaPlayer?.seekTo(progress)
                }
            }
        })

        Thread(Runnable {
            while (activity.service?.mediaPlayer != null) {
                try {
                    this.activity?.runOnUiThread{
                        var msg = Message()
                        msg.what = service.mediaPlayer?.currentPosition ?: 0
                        handler.sendMessage(msg)
                    }
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }
            }
        }).start()

        activity.service?.currentsong!!.observe(this.activity as MainActivity, Observer<AudioModel> {
                value -> updateView()
        })

        return view
    }

    @SuppressLint("HandlerLeak")
    var handler = object : Handler(Looper.getMainLooper()) {
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

    private fun playButton() {
        if(!service.mediaPlayer?.isPlaying!!) {
            service.mediaPlayer?.start()
            playIB.setImageResource(R.drawable.ic_pause_24px)
        } else {
            service.mediaPlayer?.pause()
            playIB.setImageResource(R.drawable.ic_play_arrow_24px)
        }

        seekbar.max = service.mediaPlayer?.duration ?: 0

        Thread(Runnable {
            while (service.mediaPlayer != null) {
                try {
                    this.activity?.runOnUiThread{
                        var msg = Message()
                        msg.what = service.mediaPlayer?.currentPosition ?: 0
                        handler.sendMessage(msg)
                    }
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }
            }
        }).start()
    }

    private fun goToNextOrPrev(isNext: Boolean){
        if(isNext) {
            service.toNext()
        } else {
            service.toPrevious()
        }
    }


    fun playThread() {
        playThread = Thread{
            playIB.setOnClickListener(View.OnClickListener(){
                    view ->playButton()
            })
        }
        playThread.start()
    }

    fun prevThread(){
        prevThread = Thread{
            prevIB.setOnClickListener(View.OnClickListener(){
                    view ->goToNextOrPrev(true)
            })
        }
        prevThread.start()

    }

    fun nextThread(){
        nextThread = Thread{
            nextIB.setOnClickListener(View.OnClickListener(){
                    view ->goToNextOrPrev(false)
            })
        }
        nextThread.start()
    }

    fun updateView() {
        if(service == null || service.mediaPlayer==null) return;
        song = service.currentsong.value!!
        nameTV.text = song.title

        seekbar.max = service.mediaPlayer?.duration!!
        Thread(Runnable {
            while (service.mediaPlayer != null) {
                try {
                    this.activity?.runOnUiThread{
                        var msg = Message()
                        msg.what = service.mediaPlayer?.currentPosition ?: 0
                        msg.what = service.mediaPlayer?.currentPosition ?: 0
                        handler.sendMessage(msg)
                    }
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }
            }
        }).start()
    }

}

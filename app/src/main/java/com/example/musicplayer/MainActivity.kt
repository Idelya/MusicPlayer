package com.example.musicplayer

import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity(), ServiceConnection{
    var musicList: List<AudioModel> = listOf()
    var service: PlayerServices? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            checkUserPermission()
            startService()
            setContentView(R.layout.main_activity)
        }

    override fun onDestroy() {
        stopService()
        super.onDestroy()
    }

    fun startService() {
        val serviceIntent = Intent(this, PlayerServices::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun stopService() {
        val serviceIntent = Intent(this, PlayerServices::class.java)
        stopService(serviceIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            123 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this?.let { getAudioFiles(it) }
            } else {
                Toast.makeText(this, "Permission Denied - 123", Toast.LENGTH_SHORT).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    private fun checkUserPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (this?.let { ActivityCompat.checkSelfPermission(it, android.Manifest.permission.READ_EXTERNAL_STORAGE) }
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    123
                )
                return
            }
        }
        this?.let { getAudioFiles(it) }
    }

    private fun getAudioFiles(context: Context) {
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

        musicList = tmpList
    }

    override fun onPause() {
        super.onPause()
        unbindService(this)
    }

    override fun onResume() {
        super.onResume()
        val intent: Intent = Intent(this, PlayerServices::class.java)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        service = null;
    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val bind: PlayerServices.PlayerBinder= p1 as PlayerServices.PlayerBinder
        service = bind.service
        service!!.musicList = musicList
    }

}
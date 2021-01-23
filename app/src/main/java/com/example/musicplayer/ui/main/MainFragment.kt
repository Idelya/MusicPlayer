package com.example.musicplayer.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.AudioModel
import com.example.musicplayer.MainActivity
import com.example.musicplayer.R


class MainFragment : Fragment() {

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.main_fragment, container, false) as RecyclerView

        view.adapter = MusicListAdapter((this.activity as MainActivity).musicList!!)

        (view.adapter as MusicListAdapter).setNewListener(object : MusicListAdapter.ItemListener {
            override fun onItemClick(item: AudioModel?) {
                if (item != null) {
                    setSong(item)
                    openPlayer(item)
                }
            }
        } )

        return view
    }

    fun setSong(item: AudioModel){
        (this.activity as MainActivity).song = item;
    }

    fun openPlayer(item: AudioModel) {
        (this.activity as MainActivity).song = item;
        NavHostFragment.findNavController(this).navigate(R.id.playerFragment)
    }
}
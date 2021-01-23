package com.example.musicplayer.ui.main
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.musicplayer.AudioModel
import com.example.musicplayer.R

class MusicListAdapter (
    private val values: List<AudioModel>
    ) : ListAdapter<AudioModel, MusicListAdapter.ViewHolder>(DiffCallback()) {
        lateinit var listener: ItemListener

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.music_list_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.titleView.text = item.title
            holder.artistView.text = item.artist

            holder.bind(item)
        }

        override fun getItemCount(): Int = values.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val titleView: TextView = itemView.findViewById(com.example.musicplayer.R.id.title)
            val artistView: TextView = itemView.findViewById(com.example.musicplayer.R.id.artist)

            fun bind(item: AudioModel) {
                itemView.setOnClickListener {listener.onItemClick(item)}
            }
        }

        interface ItemListener {
            fun onItemClick(item: AudioModel?) {

            }
        }

        fun setNewListener(
            newListener: ItemListener
        ){
            listener = newListener
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<AudioModel>() {
        override fun areItemsTheSame(oldItem: AudioModel, newItem: AudioModel): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: AudioModel, newItem: AudioModel): Boolean {
            return oldItem.title == newItem.title && oldItem.artist == newItem.artist
        }

    }
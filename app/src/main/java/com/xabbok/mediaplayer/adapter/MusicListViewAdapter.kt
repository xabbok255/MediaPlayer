package com.xabbok.mediaplayer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.xabbok.mediaplayer.R
import com.xabbok.mediaplayer.databinding.AlbumItemBinding
import com.xabbok.mediaplayer.dto.MusicAlbum
import com.xabbok.mediaplayer.dto.MusicTrack
import com.xabbok.mediaplayer.presentation.viewmodels.MusicViewModel

class MusicListViewAdapter(val context: Context, private val dataSource: MusicAlbum, private val parent: AppCompatActivity) : RecyclerView.Adapter<MusicListViewAdapter.MusicViewHolder>() {
    private val viewModel: MusicViewModel by parent.viewModels()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AlbumItemBinding.inflate(inflater, parent, false)
        return MusicViewHolder(binding)
    }

    override fun getItemCount(): Int = dataSource.tracks.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val item = dataSource.tracks[position]
        holder.binding.apply {
            songName.text = item.file
            time.text = "15:12"
        }

        setupSubscribes(holder, item)
    }

    private fun setupSubscribes(holder: MusicViewHolder, currentMusic: MusicTrack) {
        viewModel.currentPlayingTrack.observe(parent) {
            if (it?.id == currentMusic.id) {
                holder.binding.playPauseButton.setImageResource(R.drawable.pause_button)
            } else {
                holder.binding.playPauseButton.setImageResource(R.drawable.play_button)
            }
        }
    }

    class MusicViewHolder(val binding: AlbumItemBinding) :
        RecyclerView.ViewHolder(binding.root){

    }
}
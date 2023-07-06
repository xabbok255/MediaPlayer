package com.xabbok.mediaplayer.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.xabbok.mediaplayer.R
import com.xabbok.mediaplayer.databinding.AlbumItemBinding
import com.xabbok.mediaplayer.dto.MusicAlbum
import com.xabbok.mediaplayer.mediaplayer.PlayingState
import com.xabbok.mediaplayer.presentation.viewmodels.MusicViewModel
import com.xabbok.mediaplayer.utils.formatTime

class MusicListViewAdapter(
    private val parent: AppCompatActivity
) : RecyclerView.Adapter<MusicListViewAdapter.MusicViewHolder>() {
    private val viewModel: MusicViewModel by parent.viewModels()
    private var dataSource: MusicAlbum? = null

    fun setData(album: MusicAlbum) {
        dataSource = album
        notifyDataSetChanged()
    }

    /*
        добавляем сюда observer в onBindViewHolder, удаляем в onViewRecycled
        это позволяет использовать только необходимое количество observer-ов
    */
    private val observers: MutableMap<View, Observer<PlayingState>> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AlbumItemBinding.inflate(inflater, parent, false)
        return MusicViewHolder(binding)
    }

    override fun getItemCount(): Int = dataSource?.tracks?.size ?: 0

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        if (dataSource == null)
            return

        val item = dataSource!!.tracks[position]
        holder.binding.apply {
            songName.text = item.file
            time.text = if (item.duration > 0) formatTime(item.duration) else ""
        }

        val playOnClickListener = View.OnClickListener {
            viewModel.playPause(item)
        }

        holder.binding.playPauseButton.setOnClickListener(playOnClickListener)
        holder.binding.songName.setOnClickListener(playOnClickListener)
        holder.binding.time.setOnClickListener(playOnClickListener)

        val observer: (value: PlayingState) -> Unit = {
            it.let { state ->
                holder.binding.playPauseButton.setImageResource(R.drawable.play_button)

                val backgroundColorSelected = R.color.listItemActiveBackground
                val backgroundColorUnselected = R.color.listItemBackground

                holder.binding.root.setBackgroundResource(backgroundColorUnselected)

                when (state) {
                    is PlayingState.Paused -> if (state.track.id == item.id) {
                        holder.binding.playPauseButton.setImageResource(R.drawable.play_button)
                        holder.binding.root.setBackgroundResource(backgroundColorSelected)
                        holder.binding.time.text =
                            if (state.track.duration > 0) formatTime(state.track.duration) else ""
                    }

                    is PlayingState.Playing -> if (state.track.id == item.id) {
                        holder.binding.playPauseButton.setImageResource(R.drawable.pause_button)
                        holder.binding.root.setBackgroundResource(backgroundColorSelected)
                        holder.binding.time.text =
                            if (state.track.duration > 0) formatTime(state.track.duration) else ""
                    }

                    PlayingState.Stopped -> {
                        holder.binding.playPauseButton.setImageResource(R.drawable.play_button)

                    }
                }
            }
        }

        //добавляем в список observer
        observers.put(holder.itemView, observer)
        viewModel.currentPlayingState.observe(parent, observer)
    }

    override fun onViewRecycled(holder: MusicViewHolder) {
        //удаляем неиспользуемые observer
        observers[holder.itemView]?.let {
            viewModel.currentPlayingState.removeObserver(it)
            observers.remove(holder.itemView)
        }

        super.onViewRecycled(holder)
    }

    class MusicViewHolder(val binding: AlbumItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
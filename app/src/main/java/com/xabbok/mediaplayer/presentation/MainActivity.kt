package com.xabbok.mediaplayer.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xabbok.mediaplayer.R
import com.xabbok.mediaplayer.adapter.MusicListViewAdapter
import com.xabbok.mediaplayer.databinding.ActivityMainBinding
import com.xabbok.mediaplayer.presentation.viewmodels.MusicViewModel
import com.xabbok.mediaplayer.presentation.viewmodels.PlayingState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::bind)
    private val viewModel: MusicViewModel by viewModels()
    private var adapter: MusicListViewAdapter = MusicListViewAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.trackList.adapter = adapter

        viewModel.data.observe(this) { album ->
            album?.let {
                adapter.setData(album)

                binding.albumName.text = it.title
                binding.genre.text = it.genre
                binding.author.text = it.artist
                binding.year.text = it.published
            }
        }

        viewModel.currentPlayingState.observe(this) {
            it?.let { state ->
                when (state) {
                    is PlayingState.Paused -> {
                        binding.playPauseButtonBottom.setImageResource(R.drawable.play_button)
                    }

                    is PlayingState.Playing -> {
                        binding.playPauseButtonBottom.setImageResource(R.drawable.pause_button)
                    }

                    PlayingState.Stopped -> {
                        binding.playPauseButtonBottom.setImageResource(R.drawable.play_button)
                    }
                }
            }
        }

        binding.playPauseButtonBottom.setOnClickListener {
            viewModel.playPauseCommon()
        }

        binding.nextButtonBottom.setOnClickListener {
            viewModel.nextTrack()
        }

        binding.prevButtonBottom.setOnClickListener {
            viewModel.prevTrack()
        }

        viewModel.load()
    }
}
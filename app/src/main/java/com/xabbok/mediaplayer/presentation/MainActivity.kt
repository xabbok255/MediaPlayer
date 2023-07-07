package com.xabbok.mediaplayer.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xabbok.mediaplayer.R
import com.xabbok.mediaplayer.adapter.MusicListViewAdapter
import com.xabbok.mediaplayer.databinding.ActivityMainBinding
import com.xabbok.mediaplayer.mediaplayer.PlayingState
import com.xabbok.mediaplayer.presentation.viewmodels.MusicViewModel
import com.xabbok.mediaplayer.presentation.viewmodels.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::bind)
    private val viewModel: MusicViewModel by viewModels()
    private var adapter: MusicListViewAdapter = MusicListViewAdapter(this)

    @SuppressLint("ClickableViewAccessibility")
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

        lifecycleScope.launch {
            viewModel.playingProgressStateFlow.collect {
                binding.progressBar.apply {
                    if (it.duration == 0) {
                        isEnabled = false
                        progress = 0
                    } else {
                        isEnabled = true
                        max = it.duration
                        progress = it.currentPos
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.loopPlaybackMode.collect {
                binding.repeatButtonBottom.apply {
                    @Suppress("DEPRECATION")
                    setColorFilter(
                        if (it)
                            resources.getColor(R.color.repeatButtonActive)
                        else resources.getColor(R.color.repeatButtonInactive)
                    )
                }
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

        lifecycleScope.launch {
            viewModel.screenState.collect { state ->
                when (state) {
                    is ScreenState.Error -> {
                        binding.apply {
                            loadingIndicator.isVisible = false
                            errorLoadView.isVisible = true
                            errorTextView.text = state.message

                            repeatLoadButton.setOnClickListener {
                                state.repeatAction?.invoke()
                            }
                        }
                    }

                    ScreenState.Normal -> {
                        binding.apply {
                            loadingIndicator.isVisible = false
                            errorLoadView.isVisible = false
                        }
                    }

                    ScreenState.Loading -> {
                        binding.apply {
                            loadingIndicator.isVisible = true
                            errorLoadView.isVisible = false
                        }
                    }
                }
            }
        }

        binding.repeatButtonBottom.setOnClickListener {
            viewModel.changeLoopPlaybackMode()
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

        binding.progressBar.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val mouseX = event.x
                val totalWidth = binding.progressBar.width.toFloat()
                val progress = mouseX / totalWidth


                viewModel.seek(progress)
            }
            true
        }

        viewModel.load()
    }
}
package com.xabbok.mediaplayer.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xabbok.mediaplayer.MediaLifecycleObserver
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
    private val observer = MediaLifecycleObserver()
    private lateinit var adapter: MusicListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.data.observe(this) { album ->
            album?.let {
                adapter = MusicListViewAdapter(this, it, this)
                binding.trackList.adapter = adapter


                binding.albumName.text = it.title
                binding.genre.text = it.genre
                binding.author.text = it.artist
                binding.year.text = it.published

                adapter.notifyDataSetChanged()
            }
        }

        viewModel.currentPlayingState.observe(this) {
            it?.let { state ->
                when (state) {
                    is PlayingState.Paused -> binding.playPauseButton.setImageResource(R.drawable.play_button)
                    is PlayingState.Playing -> binding.playPauseButton.setImageResource(R.drawable.pause_button)
                    PlayingState.Stopped -> binding.playPauseButton.setImageResource(R.drawable.play_button)
                }
            }
        }

        binding.playPauseButton.setOnClickListener {
            viewModel.playPauseCommon()
        }

        viewModel.load()

        /*lifecycle.addObserver(observer)

        binding.playVideo.setOnClickListener {
            binding.viewView.apply {
                setMediaController(MediaController(this@MainActivity))
                setVideoURI(
                    Uri.parse("https://test-videos.co.uk/vids/sintel/mp4/h264/1080/Sintel_1080_10s_1MB.mp4")
                )
                setOnPreparedListener {
                    start()
                }
                setOnCompletionListener {
                    stopPlayback()
                }
            }
        }

        binding.playAudio.setOnClickListener {
            observer.apply {
                mediaPlayer?.setDataSource("https://files.freemusicarchive.org/storage-freemusicarchive-org/music/no_curator/A_Crooked_Pulse/A_Crooked_Pulse/A_Crooked_Pulse_-_01_-_Dark_Spots.mp3")

                //resources.openRawResourceFd(R.raw.volchok).use {
                //mediaPlayer?.setDataSource(it.fileDescriptor, it.startOffset, it.length)
                //     }
            }.play()
        }*/
    }
}
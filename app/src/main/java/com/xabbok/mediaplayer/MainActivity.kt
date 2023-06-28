package com.xabbok.mediaplayer

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xabbok.mediaplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::bind)
    private val observer = MediaLifecycleObserver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(observer)

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
        }
    }
}
package com.xabbok.mediaplayer.mediaplayer

import android.media.MediaPlayer
import com.xabbok.mediaplayer.dto.MusicTrack
import javax.inject.Inject

class MediaPlayerManager @Inject constructor(private val mediaPlayer: MediaPlayer) {
    /*init {

        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        if (!paused) play()
                    }

                    Lifecycle.Event.ON_PAUSE -> pause()
                    Lifecycle.Event.ON_STOP -> stop()
                    else -> {}
                }
            }
        })
    }*/

    fun resume() {
        mediaPlayer.start()
    }

    fun play(track: MusicTrack) {
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
        }

        mediaPlayer.reset()
        mediaPlayer.setDataSource(track.withFullFileName().file)
        mediaPlayer.prepareAsync()
    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun stop() {
        mediaPlayer.reset()
        //mediaPlayer.release()
    }
}
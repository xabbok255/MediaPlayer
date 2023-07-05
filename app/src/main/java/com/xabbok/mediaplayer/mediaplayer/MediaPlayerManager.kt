package com.xabbok.mediaplayer.mediaplayer

import android.media.MediaPlayer
import com.xabbok.mediaplayer.dto.MusicTrack
import javax.inject.Inject

class MediaPlayerManager @Inject constructor(private val mediaPlayer: MediaPlayer) {
    init {
        mediaPlayer.setOnCompletionListener {
            eventListener?.onTrackEnded()
        }
    }

    private var eventListener: MediaPlayerEventListener? = null

    fun setEventListener(listener: MediaPlayerEventListener) {
        eventListener = listener
    }

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
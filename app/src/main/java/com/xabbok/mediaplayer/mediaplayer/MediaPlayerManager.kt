package com.xabbok.mediaplayer.mediaplayer

import android.media.MediaPlayer
import com.xabbok.mediaplayer.dto.MusicPlayingProgress
import com.xabbok.mediaplayer.dto.MusicTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MediaPlayerManager @Inject constructor(private val mediaPlayer: MediaPlayer) {
    val playingProgressStateFlow: MutableStateFlow<MusicPlayingProgress> = MutableStateFlow(
        MusicPlayingProgress()
    )

    init {
        mediaPlayer.setOnErrorListener { _, _, _ ->
            return@setOnErrorListener true
        }

        mediaPlayer.setOnCompletionListener {
            eventListener?.onTrackEnded()
        }

        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                runCatching {
                    delay(100)

                    return@runCatching MusicPlayingProgress(
                        currentPos = mediaPlayer.currentPosition,
                        duration = if (mediaPlayer.duration != -1) mediaPlayer.duration else 0,
                    )
                }.onSuccess {
                    playingProgressStateFlow.emit(it)
                }
            }
        }

    }

    private var eventListener: MediaPlayerEventListener? = null

    fun setEventListener(listener: MediaPlayerEventListener) {
        eventListener = listener
    }

    fun seek(position: Float) {
        mediaPlayer.seekTo((mediaPlayer.duration * position).toInt())
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
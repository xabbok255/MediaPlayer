package com.xabbok.mediaplayer.mediaplayer

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xabbok.mediaplayer.dto.MusicPlayingProgress
import com.xabbok.mediaplayer.dto.MusicTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaPlayerManager @Inject constructor(private val mediaPlayer: MediaPlayer) {
    private val _currentPlayingState: MutableLiveData<PlayingState> =
        MutableLiveData(PlayingState.Stopped)

    val currentPlayingState: LiveData<PlayingState>
        get() = _currentPlayingState

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
        if (_currentPlayingState.value is PlayingState.PlayingPaused) {
            mediaPlayer.seekTo((mediaPlayer.duration * position).toInt())
        }
    }

    fun play(track: MusicTrack) {

        (_currentPlayingState.value as? PlayingState.PlayingPaused)
            ?.let { current ->
                if (current.track.id == track.id) {
                    if (current is PlayingState.Paused) {
                        //resume
                        mediaPlayer.start()
                        _currentPlayingState.value = PlayingState.Playing(current.track)
                    } else {
                        pause()
                    }
                } else
                    null
            }
            ?: let {
                mediaPlayer.setOnPreparedListener {
                    mediaPlayer.start()
                    track.duration = mediaPlayer.duration
                    _currentPlayingState.value = PlayingState.Playing(track)
                }

                mediaPlayer.reset()
                mediaPlayer.setDataSource(track.withFullFileName().file)
                mediaPlayer.prepareAsync()
            }
    }

    fun pause() {
        (_currentPlayingState.value as? PlayingState.PlayingPaused)?.let {
            mediaPlayer.pause()
            _currentPlayingState.value = PlayingState.Paused(it.track)
        }
    }

    fun stop() {
        mediaPlayer.reset()
        _currentPlayingState.value = PlayingState.Stopped
    }
}
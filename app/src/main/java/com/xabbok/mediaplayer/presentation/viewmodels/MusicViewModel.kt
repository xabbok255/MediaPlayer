package com.xabbok.mediaplayer.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xabbok.mediaplayer.dto.MusicAlbum
import com.xabbok.mediaplayer.dto.MusicTrack
import com.xabbok.mediaplayer.mediaplayer.MediaPlayerEventListener
import com.xabbok.mediaplayer.mediaplayer.MediaPlayerManager
import com.xabbok.mediaplayer.mediaplayer.PlayingState
import com.xabbok.mediaplayer.repository.AlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val repository: AlbumRepository,
    private val mediaPlayerManager: MediaPlayerManager
) : ViewModel() {
    val data: LiveData<MusicAlbum> = repository.data

    val currentPlayingState: LiveData<PlayingState> = mediaPlayerManager.currentPlayingState

    private val _loopPlaybackMode = MutableStateFlow<Boolean>(false)
    val loopPlaybackMode: StateFlow<Boolean>
        get() = _loopPlaybackMode

    val playingProgressStateFlow = mediaPlayerManager.playingProgressStateFlow

    init {
        mediaPlayerManager.setEventListener(object : MediaPlayerEventListener {
            override fun onTrackEnded() {
                nextTrack()
            }
        })
    }

    fun changeLoopPlaybackMode() {
        _loopPlaybackMode.value = !_loopPlaybackMode.value
    }

    fun seek(percent: Float) {
        mediaPlayerManager.seek(min(max(percent, 0f), 1f))
    }

    fun isFirst(track: MusicTrack): Boolean {
        return repository.data.value?.tracks?.first()?.id == track.id
    }

    fun isLast(track: MusicTrack): Boolean {
        return repository.data.value?.tracks?.last()?.id == track.id
    }

    private fun getPrevTrack(track: MusicTrack): MusicTrack? {
        return repository.data.value?.tracks?.elementAtOrNull(
            repository.data.value?.tracks?.indexOf(
                track
            )!! - 1
        )
    }

    private fun getNextTrack(track: MusicTrack): MusicTrack? {
        return repository.data.value?.tracks?.elementAtOrNull(
            repository.data.value?.tracks?.indexOf(
                track
            )!! + 1
        )
    }

    fun prevTrack() {
        (currentPlayingState.value as? PlayingState.PlayingPaused)?.let {
            getPrevTrack(it.track)?.let { track ->
                mediaPlayerManager.play(track)
            }
        }
    }

    fun nextTrack() {
        (currentPlayingState.value as? PlayingState.PlayingPaused)?.let {
            getNextTrack(it.track)?.let { track ->
                mediaPlayerManager.play(track)
            } ?: let {
                if (_loopPlaybackMode.value) {
                    (data.value?.tracks?.firstOrNull())?.let { track ->
                        mediaPlayerManager.play(track)
                    }
                } else {
                    mediaPlayerManager.stop()
                }
            }
        }
    }

    fun pause() {
        mediaPlayerManager.pause()
    }

    fun playPauseCommon() {
        currentPlayingState.value?.let { state ->
            when (state) {
                is PlayingState.Paused -> {
                    mediaPlayerManager.play(state.track)
                }

                is PlayingState.Playing -> {
                    mediaPlayerManager.pause()
                }

                PlayingState.Stopped -> {
                    (data.value?.tracks?.firstOrNull())?.let {
                        mediaPlayerManager.play(it)
                    }
                }
            }
        }
    }

    fun playPause(newTrack: MusicTrack) {
        mediaPlayerManager.play(newTrack)
    }

    fun load() {
        viewModelScope.launch {
            repository.load()
        }
    }
}


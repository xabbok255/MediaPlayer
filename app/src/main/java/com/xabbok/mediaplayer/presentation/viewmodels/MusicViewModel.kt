package com.xabbok.mediaplayer.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xabbok.mediaplayer.dto.MusicAlbum
import com.xabbok.mediaplayer.dto.MusicTrack
import com.xabbok.mediaplayer.mediaplayer.MediaPlayerEventListener
import com.xabbok.mediaplayer.mediaplayer.MediaPlayerManager
import com.xabbok.mediaplayer.repository.AlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val repository: AlbumRepository,
    private val mediaPlayerManager: MediaPlayerManager
) : ViewModel() {
    val data: LiveData<MusicAlbum> = repository.data

    private val _currentPlayingState: MutableLiveData<PlayingState> =
        MutableLiveData(PlayingState.Stopped)
    val currentPlayingState: LiveData<PlayingState>
        get() = _currentPlayingState

    init {
        mediaPlayerManager.setEventListener(object : MediaPlayerEventListener {
            override fun onTrackEnded() {
                nextTrack()
            }
        })
    }

    fun prevTrack() {
        mediaPlayerManager.stop()

        _currentPlayingState.value?.let { state ->
            when (state) {
                is PlayingState.PlayingPaused -> {
                    repository.data.value?.tracks?.indexOf(state.track)
                        ?.let { currentIndex ->
                            repository.data.value?.tracks?.getOrNull(currentIndex - 1)
                        }
                        ?.let {
                            _currentPlayingState.value = PlayingState.Playing(it)
                            mediaPlayerManager.play(it)
                        } ?: let {
                        _currentPlayingState.value = PlayingState.Stopped
                    }
                }

                is PlayingState.Stopped -> {}
            }
        }
    }


    fun nextTrack() {
        mediaPlayerManager.stop()

        _currentPlayingState.value?.let { state ->
            when (state) {
                is PlayingState.PlayingPaused -> {
                    repository.data.value?.tracks?.indexOf(state.track)
                        ?.let { currentIndex ->
                            repository.data.value?.tracks?.getOrNull(currentIndex + 1)
                        }
                        ?.let {
                            _currentPlayingState.value = PlayingState.Playing(it)
                            mediaPlayerManager.play(it)
                        } ?: let {
                        _currentPlayingState.value = PlayingState.Stopped
                    }
                }

                is PlayingState.Stopped -> {}
            }
        }
    }

    fun pause() {
        _currentPlayingState.value?.let { state ->
            when (state) {
                is PlayingState.Paused -> {}
                is PlayingState.Playing -> {
                    mediaPlayerManager.pause()
                    _currentPlayingState.value = PlayingState.Paused(state.track)
                }

                PlayingState.Stopped -> {}
            }
        }
    }

    fun playPauseCommon() {
        _currentPlayingState.value?.let { state ->
            when (state) {
                is PlayingState.Paused -> {
                    _currentPlayingState.value = PlayingState.Playing(state.track)
                    mediaPlayerManager.resume()
                }

                is PlayingState.Playing -> {
                    _currentPlayingState.value = PlayingState.Paused(state.track)
                    mediaPlayerManager.pause()
                }

                PlayingState.Stopped -> {
                    (data.value?.tracks?.firstOrNull())?.let {
                        _currentPlayingState.value = PlayingState.Playing(it)
                        mediaPlayerManager.play(it)
                    }
                }
            }


        }
    }

    fun playPause(newTrack: MusicTrack) {
        _currentPlayingState.value?.let { state ->
            when (state) {
                is PlayingState.Playing -> {
                    if (state.track.id == newTrack.id) {
                        _currentPlayingState.value =
                            PlayingState.Paused(newTrack)
                        mediaPlayerManager.pause()
                    } else {
                        _currentPlayingState.value =
                            PlayingState.Playing(newTrack)
                        mediaPlayerManager.play(newTrack)
                    }
                }

                is PlayingState.Paused -> {
                    if (state.track.id == newTrack.id) {
                        _currentPlayingState.value =
                            PlayingState.Playing(newTrack)
                        mediaPlayerManager.resume()
                    } else {
                        _currentPlayingState.value =
                            PlayingState.Playing(newTrack)
                        mediaPlayerManager.play(newTrack)
                    }
                }

                is PlayingState.Stopped -> {
                    _currentPlayingState.value =
                        PlayingState.Playing(newTrack)
                    mediaPlayerManager.play(newTrack)
                }
            }
        }
    }

    fun load() {
        viewModelScope.launch {
            repository.load()
        }
    }
}

sealed class PlayingState() {
    sealed class PlayingPaused(open val track: MusicTrack) : PlayingState()
    class Playing(override val track: MusicTrack) : PlayingPaused(track)
    class Paused(override val track: MusicTrack) : PlayingPaused(track)
    object Stopped : PlayingState()
}
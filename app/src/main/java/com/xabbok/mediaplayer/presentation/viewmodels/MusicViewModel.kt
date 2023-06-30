package com.xabbok.mediaplayer.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xabbok.mediaplayer.dto.MusicAlbum
import com.xabbok.mediaplayer.dto.MusicTrack
import com.xabbok.mediaplayer.repository.AlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(private val repository: AlbumRepository) : ViewModel() {
    val data: LiveData<MusicAlbum> = repository.data

    private val _currentPlayingState: MutableLiveData<PlayingState> =
        MutableLiveData(PlayingState.Stopped)
    val currentPlayingState: LiveData<PlayingState>
        get() = _currentPlayingState

    fun playPauseCommon() {
        _currentPlayingState.value?.let { state ->
            when (state) {
                is PlayingState.Paused -> {
                    _currentPlayingState.value = PlayingState.Playing(state.track)
                }

                is PlayingState.Playing -> {
                    _currentPlayingState.value = PlayingState.Paused(state.track)
                }

                PlayingState.Stopped -> {
                    (data.value?.tracks?.firstOrNull())?.let {
                        _currentPlayingState.value = PlayingState.Playing(it)
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
                    } else {
                        _currentPlayingState.value =
                            PlayingState.Playing(newTrack)
                    }
                }

                is PlayingState.Paused -> {
                    _currentPlayingState.value =
                        PlayingState.Playing(newTrack)
                }

                is PlayingState.Stopped -> {
                    _currentPlayingState.value =
                        PlayingState.Playing(newTrack)
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
    class Playing(val track: MusicTrack) : PlayingState()
    class Paused(val track: MusicTrack) : PlayingState()
    object Stopped : PlayingState()
}
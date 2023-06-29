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
    private val _data: MutableLiveData<MusicAlbum> = MutableLiveData()
    val data: LiveData<MusicAlbum>
        get() = _data

    private val _currentPlayingTrack: MutableLiveData<MusicTrack?> = MutableLiveData()
    val currentPlayingTrack: LiveData<MusicTrack?>
        get() = _currentPlayingTrack

    private val _currentPlayingState: MutableLiveData<PlayingState> = MutableLiveData()
    val currentPlayingState: LiveData<PlayingState>
        get() = _currentPlayingState

    fun startPlayingTrack(track: MusicTrack) {
        if (_currentPlayingTrack.value?.id != track.id) {
            _currentPlayingTrack.value = track
        }

        _currentPlayingState.value = PlayingState.Playing
    }

    fun stopPlayingTrack() {
        _currentPlayingTrack.value = null
        _currentPlayingState.value = PlayingState.Stopped
    }

    fun pausePlayingTrack() {
        _currentPlayingState.value = PlayingState.Paused
    }

    fun test() {
        _data.value = MusicAlbum(0, "1", "2", "3", "4", "5", emptyList())
    }

    fun load() {
        viewModelScope.launch {
            repository.load()
        }
    }
}

sealed class PlayingState() {
    object Playing : PlayingState()
    object Paused : PlayingState()
    object Stopped : PlayingState()
}
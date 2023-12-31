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
import com.xabbok.mediaplayer.service.MediaInfoService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val repository: AlbumRepository,
    private val mediaPlayerManager: MediaPlayerManager,
    private val mediaInfoService: MediaInfoService
) : ViewModel() {
    val data: LiveData<MusicAlbum> = repository.data

    private val _screenState =
        MutableSharedFlow<ScreenState>().also { it.tryEmit(ScreenState.Normal) }
    val screenState: SharedFlow<ScreenState>
        get() = _screenState.asSharedFlow()

    val currentPlayingState: LiveData<PlayingState> = mediaPlayerManager.currentPlayingState

    private val _loopPlaybackMode = MutableStateFlow<Boolean>(false)
    val loopPlaybackMode: StateFlow<Boolean>
        get() = _loopPlaybackMode.asStateFlow()

    val playingProgressStateFlow = mediaPlayerManager.playingProgressStateFlow

    init {
        mediaPlayerManager.setEventListener(object : MediaPlayerEventListener {
            override fun onTrackEnded() {
                nextTrack()
            }
        })
    }

    private suspend fun changeState(newState: ScreenState) {
        _screenState.emit(newState)
    }

    fun processMediaDurationInfo(inputTrack: MusicTrack) {
        mediaInfoService.processMedia(inputTrack) { track, duration ->
            repository.setTrackDuration(track, duration)
        }
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
            runCatching {
                changeState(ScreenState.Loading)
                delay(1000)
                repository.load()
            }.onFailure {
                changeState(ScreenState.Error("Ошибка загрузки списка") {
                    load()
                })
            }.onSuccess {
                changeState(ScreenState.Normal)
            }
        }
    }
}
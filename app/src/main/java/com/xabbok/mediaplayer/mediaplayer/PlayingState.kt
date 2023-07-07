package com.xabbok.mediaplayer.mediaplayer

import com.xabbok.mediaplayer.dto.MusicTrack

sealed class PlayingState {
    sealed class PlayingPaused(open val track: MusicTrack) : PlayingState()
    class Playing(override val track: MusicTrack) : PlayingPaused(track)
    class Paused(override val track: MusicTrack) : PlayingPaused(track)
    object Stopped : PlayingState()
}
package com.xabbok.mediaplayer.repository

import androidx.lifecycle.LiveData
import com.xabbok.mediaplayer.dto.MusicAlbum
import com.xabbok.mediaplayer.dto.MusicTrack

interface AlbumRepository {
    val data: LiveData<MusicAlbum>

    suspend fun load()
    fun setTrackDuration(track: MusicTrack, duration: Int)
}
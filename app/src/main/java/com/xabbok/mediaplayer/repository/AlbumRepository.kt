package com.xabbok.mediaplayer.repository

import androidx.lifecycle.LiveData
import com.xabbok.mediaplayer.dto.MusicAlbum

interface AlbumRepository {
    val data: LiveData<MusicAlbum>

    suspend fun load()
}
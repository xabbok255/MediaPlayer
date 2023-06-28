package com.xabbok.mediaplayer.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xabbok.mediaplayer.dto.MusicAlbum
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlbumRepositoryImpl @Inject constructor(
    @ApplicationContext
    context: Context
) : AlbumRepository {

    override val data: LiveData<MusicAlbum> = MutableLiveData<MusicAlbum>()
}
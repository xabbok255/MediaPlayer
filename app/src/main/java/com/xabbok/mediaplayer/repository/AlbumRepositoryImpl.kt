package com.xabbok.mediaplayer.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xabbok.mediaplayer.dto.MusicAlbum
import com.xabbok.mediaplayer.error.ApiAppError
import com.xabbok.mediaplayer.service.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlbumRepositoryImpl @Inject constructor(
    @ApplicationContext
    context: Context,
    val api: ApiService,
) : AlbumRepository {

    private val _data: MutableLiveData<MusicAlbum> = MutableLiveData()
    override val data: LiveData<MusicAlbum>
        get() = _data

    override suspend fun load() {
        runCatching {
            val response = api.getAll()

            val body: MusicAlbum = response.body() ?: throw ApiAppError(
                response.code(),
                response.message()
            )
            body
        }.onSuccess {
            _data.postValue(it)
        }.onFailure {
            it.printStackTrace()
        }
    }
}
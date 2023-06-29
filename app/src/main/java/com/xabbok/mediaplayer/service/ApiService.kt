package com.xabbok.mediaplayer.service

import com.xabbok.mediaplayer.dto.MusicAlbum
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("album.json")
    suspend fun getAll(): Response<MusicAlbum>
}
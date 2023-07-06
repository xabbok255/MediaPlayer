package com.xabbok.mediaplayer.dto

import com.google.gson.annotations.Expose
import com.xabbok.mediaplayer.BuildConfig

data class MusicTrack(
    val id: Int,
    val file: String,
    @Expose(deserialize = false)
    var duration: Int = 0,
)
{
    fun withFullFileName() : MusicTrack {
        return this.copy(
            file = "${BuildConfig.BASE_URL}$file"
        )
    }
}
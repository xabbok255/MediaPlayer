package com.xabbok.mediaplayer.dto

import com.xabbok.mediaplayer.BuildConfig

data class MusicTrack(
    val id: Int,
    val file: String,
)
{
    fun withFullFileName() : MusicTrack {
        return this.copy(
            file = "${BuildConfig.BASE_URL}$file"
        )
    }
}
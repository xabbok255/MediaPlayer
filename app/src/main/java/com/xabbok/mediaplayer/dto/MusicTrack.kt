package com.xabbok.mediaplayer.dto

import com.google.gson.annotations.Expose

data class MusicTrack(
    val id: Int,
    val file: String,
    @Expose(serialize = false)
    val playing: Boolean
)

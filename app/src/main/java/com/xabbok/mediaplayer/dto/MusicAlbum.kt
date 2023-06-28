package com.xabbok.mediaplayer.dto

data class MusicAlbum(
    val id: Int,
    val title: String,
    val subtitle: String,
    val artist: String,
    val published: String,
    val genre: String,
    val tracks: List<MusicTrack>
)

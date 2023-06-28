package com.xabbok.mediaplayer.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xabbok.mediaplayer.dto.MusicAlbum
import com.xabbok.mediaplayer.repository.AlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(private val repository: AlbumRepository) : ViewModel() {
    private val _data: MutableLiveData<MusicAlbum> = MutableLiveData()
    val data: LiveData<MusicAlbum>
        get() = _data

    fun test() {
        _data.value = MusicAlbum(0, "1", "2", "3", "4", "5", emptyList())
    }
}
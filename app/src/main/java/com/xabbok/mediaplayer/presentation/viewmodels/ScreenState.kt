package com.xabbok.mediaplayer.presentation.viewmodels

sealed class ScreenState {
    object Normal : ScreenState()
    object Loading : ScreenState()
    class Error(val message: String, val repeatAction: (() -> Unit)? = null) : ScreenState()
}
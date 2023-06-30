package com.xabbok.mediaplayer.utils

import androidx.lifecycle.LiveData

fun <T> LiveData<T>.getActiveObserverCount(): Int {
    try {
        val field = LiveData::class.java.getDeclaredField("mActiveCount")
        field.isAccessible = true
        return field.get(this) as Int
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }

    return 0
}
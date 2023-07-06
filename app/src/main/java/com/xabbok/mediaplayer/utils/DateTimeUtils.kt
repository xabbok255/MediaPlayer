package com.xabbok.mediaplayer.utils

import android.annotation.SuppressLint
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

@SuppressLint("SimpleDateFormat")
fun formatTime(milliseconds: Int): String {
    val hours = (milliseconds / (1000 * 60 * 60)) % 24
    val minutes = (milliseconds / (1000 * 60)) % 60
    val seconds = (milliseconds / 1000) % 60

    val sdf = SimpleDateFormat(if(hours > 0) "H:mm:ss" else "mm:ss")
    sdf.timeZone = TimeZone.getTimeZone("UTC")

    sdf.format(Date(milliseconds.toLong()))

    val df = DecimalFormat("00")
    val formattedHours = df.format(hours)
    val formattedMinutes = df.format(minutes)
    val formattedSeconds = df.format(seconds)

    return if (hours > 0) "$formattedHours:$formattedMinutes:$formattedSeconds" else "$formattedMinutes:$formattedSeconds"
}

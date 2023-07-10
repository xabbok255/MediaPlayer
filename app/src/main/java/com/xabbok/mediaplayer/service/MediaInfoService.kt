package com.xabbok.mediaplayer.service

import android.content.Context
import androidx.media3.common.C.TIME_UNSET
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.xabbok.mediaplayer.dto.MusicTrack
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject

const val DURATION_REQUEST_TIMEOUT = 2000L

@InstallIn(SingletonComponent::class)
@Module
class MediaInfoService @Inject constructor(@ApplicationContext val context: Context) {
    private val processQueue =
        LinkedBlockingQueue<Pair<MusicTrack, (track: MusicTrack, duration: Int) -> Unit>>()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                val element = processQueue.take()

                runCatching {
                    val duration = getMediaDurationFromUrl(element.first.withFullFileName().file)

                    if (duration == TIME_UNSET)
                        throw Exception()

                    return@runCatching duration.toInt()
                }.onSuccess {
                    element.second(element.first, it)
                }.onFailure {
                    it.printStackTrace()
                    processQueue.add(element)
                }
            }
        }
    }

    private suspend fun getMediaDurationFromUrl(url: String): Long {
        val durationFlow: MutableSharedFlow<Long> = MutableSharedFlow()

        return withContext(Dispatchers.Main) {
            val player = ExoPlayer.Builder(context).build()
            val result = withTimeoutOrNull(DURATION_REQUEST_TIMEOUT) {
                player.addMediaItem(MediaItem.fromUri(url))
                player.prepare()

                player.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)

                        if (playbackState == Player.STATE_READY) {
                            val duration = player.duration

                            CoroutineScope(Dispatchers.Default).launch {
                                durationFlow.emit(duration)
                            }
                        }
                    }
                })

                durationFlow.first()
            } ?: TIME_UNSET
            player.release()

            result
        }
    }

    fun processMedia(track: MusicTrack, callback: (track: MusicTrack, duration: Int) -> Unit) {
        if (!processQueue.any { it.first.id == track.id })
            processQueue.add(Pair(track, callback))
    }
}
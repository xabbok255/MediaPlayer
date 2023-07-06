package com.xabbok.mediaplayer.service

import com.xabbok.mediaplayer.dto.MusicTrack
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wseemann.media.FFmpegMediaMetadataRetriever
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject

@InstallIn(SingletonComponent::class)
@Module
class MediaInfoService @Inject constructor() {
    private val processQueue = LinkedBlockingQueue<Pair<MusicTrack, (track: MusicTrack, duration: Int) -> Unit>>()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                val element = processQueue.take()

                runCatching {
                    val retriever = FFmpegMediaMetadataRetriever()
                    retriever.setDataSource(element.first.withFullFileName().file)
                    val duration =
                        retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION)
                    retriever.release()
                    return@runCatching duration.toInt()
                }.onSuccess {
                    element.second(element.first, it)
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }
    }

    fun processMedia(track: MusicTrack, callback: (track: MusicTrack, duration: Int) -> Unit) {
        if (!processQueue.any { it.first.id == track.id })
            processQueue.add(Pair(track, callback))
    }
}
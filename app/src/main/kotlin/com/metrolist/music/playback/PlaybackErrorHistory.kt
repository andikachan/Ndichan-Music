/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.playback

import androidx.media3.common.PlaybackException
import com.metrolist.music.models.MediaMetadata
import com.metrolist.music.ui.player.buildPlaybackErrorReport
import com.metrolist.music.ui.player.causeChain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class PlaybackErrorItem(
    val id: String = UUID.randomUUID().toString(),
    val timestampMs: Long = System.currentTimeMillis(),
    val mediaId: String?,
    val songTitle: String,
    val artists: String,
    val streamClient: String?,
    val errorCodeName: String,
    val errorCode: Int,
    val errorMessage: String,
    val causeSummary: String,
    val fullReport: String,
)

object PlaybackErrorHistory {
    private const val MAX_ITEMS = 50
    private val _errors = MutableStateFlow<List<PlaybackErrorItem>>(emptyList())
    val errors: StateFlow<List<PlaybackErrorItem>> = _errors.asStateFlow()

    fun recordError(
        error: PlaybackException,
        mediaMetadata: MediaMetadata?,
        streamClient: String?,
    ) {
        val causes = error.causeChain()
        val causeSummary = causes
            .drop(1)
            .joinToString(" → ") { cause ->
                cause.javaClass.simpleName.ifBlank { cause.javaClass.name }
            }
        val rawErrorMessages = causes.mapNotNull { it.message?.takeIf(String::isNotBlank) }.distinct()
        val primaryMsg = rawErrorMessages.firstOrNull() ?: error.message ?: "Playback error"

        val report = buildPlaybackErrorReport(error, mediaMetadata, streamClient)

        val item = PlaybackErrorItem(
            timestampMs = if (error.timestampMs > 0) error.timestampMs else System.currentTimeMillis(),
            mediaId = mediaMetadata?.id,
            songTitle = mediaMetadata?.title ?: "Unknown Track",
            artists = mediaMetadata?.artists?.joinToString(", ") { it.name } ?: "Unknown Artist",
            streamClient = streamClient,
            errorCodeName = error.errorCodeName.removePrefix("ERROR_CODE_"),
            errorCode = error.errorCode,
            errorMessage = primaryMsg,
            causeSummary = causeSummary,
            fullReport = report,
        )

        // Avoid exact duplicate at head
        val current = _errors.value
        if (current.isNotEmpty() &&
            current.first().mediaId == item.mediaId &&
            current.first().errorMessage == item.errorMessage &&
            (item.timestampMs - current.first().timestampMs) < 3000
        ) {
            return
        }

        _errors.value = (listOf(item) + current).take(MAX_ITEMS)
    }

    fun clear() {
        _errors.value = emptyList()
    }
}

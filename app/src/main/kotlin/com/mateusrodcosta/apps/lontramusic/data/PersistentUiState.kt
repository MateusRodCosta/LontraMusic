package com.mateusrodcosta.apps.lontramusic.data

import kotlinx.serialization.Serializable

@Serializable
data class PersistentUiState(
    val libraryScreenHomeViewPage: Int = 0,
    val playerScreenUseLyricsView: Boolean = false,
    val playerScreenUseCountdown: Boolean = false,
    val playerTimerSettings: PlayerTimerSettings = PlayerTimerSettings(),
    val playlistIoSyncHelpShown: Boolean = false,
)

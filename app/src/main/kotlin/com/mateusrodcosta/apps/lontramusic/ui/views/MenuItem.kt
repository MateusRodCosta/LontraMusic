package com.mateusrodcosta.apps.lontramusic.ui.views

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AddToHomeScreen
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistRemove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.pm.ShortcutManagerCompat
import com.mateusrodcosta.apps.lontramusic.R
import com.mateusrodcosta.apps.lontramusic.UiManager
import com.mateusrodcosta.apps.lontramusic.data.PlayerManager
import com.mateusrodcosta.apps.lontramusic.data.SpecialPlaylistLookup
import com.mateusrodcosta.apps.lontramusic.data.Track
import com.mateusrodcosta.apps.lontramusic.data.albumKey
import com.mateusrodcosta.apps.lontramusic.data.playlistShortcut
import com.mateusrodcosta.apps.lontramusic.globals.Strings
import com.mateusrodcosta.apps.lontramusic.ui.views.library.LibraryTrackClickAction
import com.mateusrodcosta.apps.lontramusic.ui.views.library.openAlbumCollectionView
import com.mateusrodcosta.apps.lontramusic.ui.views.library.openArtistCollectionView
import com.mateusrodcosta.apps.lontramusic.ui.views.playlist.AddToPlaylistDialog
import com.mateusrodcosta.apps.lontramusic.ui.views.playlist.DeletePlaylistDialog
import com.mateusrodcosta.apps.lontramusic.ui.views.playlist.PlaylistEditScreen
import com.mateusrodcosta.apps.lontramusic.ui.views.playlist.PlaylistIoScreen
import com.mateusrodcosta.apps.lontramusic.ui.views.playlist.RemoveFromPlaylistDialog
import com.mateusrodcosta.apps.lontramusic.ui.views.playlist.RenamePlaylistDialog
import com.mateusrodcosta.apps.lontramusic.utils.icuFormat
import java.util.UUID

@Immutable
sealed class MenuItem {
    @Immutable
    data class Button(
        val text: String,
        val icon: ImageVector,
        val dangerous: Boolean = false,
        val onClick: () -> Unit,
    ) : MenuItem()

    @Immutable object Divider : MenuItem()
}

@Stable
fun trackMenuItems(
    track: Track,
    playerManager: PlayerManager,
    uiManager: UiManager,
): List<MenuItem> {
    val queue =
        listOf(
            MenuItem.Button(Strings[R.string.track_play_next], Icons.Filled.ChevronRight) {
                playerManager.playNext(listOf(track))
                uiManager.toast(Strings[R.string.toast_track_queued].icuFormat(1))
            },
            MenuItem.Button(Strings[R.string.track_add_to_queue], Icons.Filled.Add) {
                playerManager.addTracks(listOf(track))
                uiManager.toast(Strings[R.string.toast_track_queued].icuFormat(1))
            },
        )
    val playlist =
        MenuItem.Button(Strings[R.string.playlist_add_to], Icons.AutoMirrored.Filled.PlaylistAdd) {
            uiManager.openDialog(AddToPlaylistDialog(listOf(track)))
        }
    val share =
        MenuItem.Button(Strings[R.string.track_share], Icons.Filled.Share) {
            uiManager.intentLauncher.get()?.share(listOf(track))
        }
    val artists =
        track.artists.map { artist ->
            MenuItem.Button(
                Strings[R.string.track_view_artist].icuFormat(artist),
                Icons.Filled.Person,
            ) {
                uiManager.openArtistCollectionView(artist)
            }
        }
    val album =
        listOfNotNull(
            track.albumKey?.let { albumKey ->
                MenuItem.Button(
                    Strings[R.string.track_view_album].icuFormat(albumKey.name),
                    Icons.Filled.Album,
                ) {
                    uiManager.openAlbumCollectionView(albumKey)
                }
            }
        )
    val details =
        MenuItem.Button(Strings[R.string.track_details], Icons.Filled.Info) {
            uiManager.openDialog(TrackDetailsDialog(track))
        }

    return queue + playlist + share + MenuItem.Divider + artists + album + details
}

@Stable
inline fun trackMenuItemsLibrary(
    track: Track,
    crossinline playContext: () -> Pair<List<Track>, Int>,
    playerManager: PlayerManager,
    uiManager: UiManager,
): List<MenuItem> {
    return listOf(LibraryTrackClickAction.PLAY_ALL, LibraryTrackClickAction.PLAY).map {
        MenuItem.Button(Strings[it.stringId], it.icon!!) {
            val (tracks, index) = playContext()
            it.invoke(tracks, index, playerManager, uiManager)
        }
    } + trackMenuItems(track, playerManager, uiManager)
}

@Stable
inline fun collectionMenuItemsWithoutPlay(
    crossinline tracks: () -> List<Track>,
    playerManager: PlayerManager,
    uiManager: UiManager,
    crossinline continuation: () -> Unit = {},
): List<MenuItem.Button> {
    return listOf(
        MenuItem.Button(Strings[R.string.track_play_next], Icons.Filled.ChevronRight) {
            val tracks = tracks()
            playerManager.playNext(tracks)
            uiManager.toast(Strings[R.string.toast_track_queued].icuFormat(tracks.size))
            continuation()
        },
        MenuItem.Button(Strings[R.string.track_add_to_queue], Icons.Filled.Add) {
            val tracks = tracks()
            playerManager.addTracks(tracks)
            uiManager.toast(Strings[R.string.toast_track_queued].icuFormat(tracks.size))
            continuation()
        },
        MenuItem.Button(Strings[R.string.playlist_add_to], Icons.AutoMirrored.Filled.PlaylistAdd) {
            uiManager.openDialog(AddToPlaylistDialog(tracks()))
            continuation()
        },
        MenuItem.Button(Strings[R.string.track_share], Icons.Filled.Share) {
            uiManager.intentLauncher.get()?.share(tracks())
            continuation()
        },
    )
}

@Stable
inline fun collectionMenuItems(
    crossinline tracks: () -> List<Track>,
    playerManager: PlayerManager,
    uiManager: UiManager,
    crossinline continuation: () -> Unit = {},
): List<MenuItem.Button> {
    return listOf(
        MenuItem.Button(Strings[R.string.track_play], Icons.Filled.PlayArrow) {
            playerManager.setTracks(tracks(), null)
            continuation()
        }
    ) + collectionMenuItemsWithoutPlay(tracks, playerManager, uiManager, continuation)
}

@Stable
fun playlistCollectionMenuItemsWithoutEdit(key: UUID, uiManager: UiManager): List<MenuItem.Button> {
    return listOfNotNull(
        if (!SpecialPlaylistLookup.containsKey(key)) {
            MenuItem.Button(
                Strings[R.string.playlist_rename],
                Icons.Filled.DriveFileRenameOutline,
            ) {
                uiManager.openDialog(RenamePlaylistDialog(key))
            }
        } else null,
        MenuItem.Button(Strings[R.string.playlist_export], Icons.Outlined.FileUpload) {
            uiManager.openTopLevelScreen(PlaylistIoScreen.export(setOf(key)))
        },
        MenuItem.Button(Strings[R.string.playlist_delete], Icons.Filled.Delete, dangerous = true) {
            uiManager.openDialog(DeletePlaylistDialog(key))
        },
    )
}

@Stable
fun playlistCollectionMenuItems(
    key: UUID,
    name: String,
    context: Context,
    uiManager: UiManager,
): List<MenuItem> {
    return listOf(
        MenuItem.Button(Strings[R.string.playlist_edit], Icons.Filled.Edit) {
            uiManager.openTopLevelScreen(PlaylistEditScreen(key))
        }
    ) +
        playlistCollectionMenuItemsWithoutEdit(key, uiManager) +
        listOf(
            MenuItem.Divider,
            MenuItem.Button(
                Strings[R.string.playlist_create_shortcut],
                Icons.AutoMirrored.Filled.AddToHomeScreen,
            ) {
                ShortcutManagerCompat.requestPinShortcut(
                    context,
                    playlistShortcut(context, "pinnedPlaylist", key, name),
                    null,
                )
            },
        )
}

@Stable
inline fun playlistCollectionMultiSelectMenuItems(
    crossinline keys: () -> Set<UUID>,
    uiManager: UiManager,
    crossinline continuation: () -> Unit,
): List<MenuItem.Button> {
    return listOf(
        MenuItem.Button(Strings[R.string.playlist_export], Icons.Outlined.FileUpload) {
            uiManager.openTopLevelScreen(PlaylistIoScreen.export(keys()))
            continuation()
        },
        MenuItem.Button(Strings[R.string.playlist_delete], Icons.Filled.Delete, dangerous = true) {
            uiManager.openDialog(DeletePlaylistDialog(keys()))
            continuation()
        },
    )
}

@Stable
inline fun playlistTrackMenuItems(
    playlistKey: UUID,
    crossinline trackKeys: () -> Set<UUID>,
    uiManager: UiManager,
    crossinline continuation: () -> Unit = {},
): List<MenuItem.Button> {
    return listOf(
        MenuItem.Button(Strings[R.string.playlist_remove_from], Icons.Filled.PlaylistRemove) {
            uiManager.openDialog(RemoveFromPlaylistDialog(playlistKey, trackKeys()))
            continuation()
        }
    )
}

@Stable
fun playlistTrackMenuItems(
    playlistKey: UUID,
    trackKey: UUID,
    uiManager: UiManager,
): List<MenuItem.Button> {
    return playlistTrackMenuItems(playlistKey, { setOf(trackKey) }, uiManager)
}

package org.sunsetware.phocid

import java.util.UUID
import org.assertj.core.api.Assertions.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.sunsetware.phocid.data.InvalidTrack
import org.sunsetware.phocid.data.PlaylistEntry
import org.sunsetware.phocid.data.PlaylistIoSettings
import org.sunsetware.phocid.data.RealizedPlaylist
import org.sunsetware.phocid.data.RealizedPlaylistEntry
import org.sunsetware.phocid.data.parseM3u
import org.sunsetware.phocid.data.toM3u

@RunWith(RobolectricTestRunner::class)
class PlaylistIoTest {
    fun testParseM3u(
        m3u: String,
        libraryTrackPaths: Set<String>,
        settings: PlaylistIoSettings,
        expected: List<String>,
    ) {
        val actual =
            parseM3u(
                    "",
                    m3u.toByteArray(Charsets.UTF_8),
                    libraryTrackPaths,
                    settings,
                    Charsets.UTF_8.name(),
                    0,
                )
                .entries
                .map { it.path }
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected)
    }

    val parseM3uLibraryTrackPaths = setOf("/dir/a", "/dir/dir2/b", "/c", "/dir3/d", "/dir4/d")

    @Test
    fun parseM3u_MatchLocation() {
        testParseM3u(
            m3u =
                listOf(" /dir/a ", "/dir/dir2/b", "#c", "D:\\D", "e", "http://invalid")
                    .joinToString("\r\n"),
            libraryTrackPaths = parseM3uLibraryTrackPaths,
            settings = PlaylistIoSettings(ignoreLocation = false),
            expected = listOf("/dir/a", "/dir/dir2/b"),
        )
    }

    @Test
    fun parseM3u_DoNotRemoveInvalid() {
        testParseM3u(
            m3u =
                listOf(" /dir/a ", "/dir/dir2/b", "#c", "D:\\D", "e", "http://invalid")
                    .joinToString("\r\n"),
            libraryTrackPaths = parseM3uLibraryTrackPaths,
            settings = PlaylistIoSettings(ignoreLocation = false, removeInvalid = false),
            expected = listOf("/dir/a", "/dir/dir2/b", "D:/D", "e", "http://invalid"),
        )
    }

    @Test
    fun parseM3u_IgnoreLocation() {
        testParseM3u(
            m3u =
                listOf(" ../a ", "http://example.com/b", "#c", "D:\\dir4\\D", "e", "http://invalid")
                    .joinToString("\r\n"),
            libraryTrackPaths = parseM3uLibraryTrackPaths,
            settings = PlaylistIoSettings(),
            expected = listOf("/dir/a", "/dir/dir2/b", "/dir4/d"),
        )
    }

    @Test
    fun parseM3u_RoundTrip() {
        testParseM3u(
            m3u =
                parseM3u(
                        "",
                        parseM3uLibraryTrackPaths.joinToString("\r\n").toByteArray(Charsets.UTF_8),
                        parseM3uLibraryTrackPaths,
                        PlaylistIoSettings(),
                        Charsets.UTF_8.name(),
                        0,
                    )
                    .entries
                    .joinToString("\n") { it.path },
            libraryTrackPaths = parseM3uLibraryTrackPaths,
            settings = PlaylistIoSettings(),
            expected = parseM3uLibraryTrackPaths.toList(),
        )
    }

    @Test
    fun parseM3u_RoundTripDoNotIgnoreLocation() {
        testParseM3u(
            m3u =
                parseM3u(
                        "",
                        parseM3uLibraryTrackPaths.joinToString("\r\n").toByteArray(Charsets.UTF_8),
                        parseM3uLibraryTrackPaths,
                        PlaylistIoSettings(ignoreLocation = false),
                        Charsets.UTF_8.name(),
                        0,
                    )
                    .entries
                    .joinToString("\n") { it.path },
            libraryTrackPaths = parseM3uLibraryTrackPaths,
            settings = PlaylistIoSettings(ignoreCase = false),
            expected = parseM3uLibraryTrackPaths.toList(),
        )
    }

    @Test
    fun parseM3u_RoundTripCaseSensitive() {
        val paths = setOf("a", "A")
        testParseM3u(
            m3u =
                parseM3u(
                        "",
                        paths.joinToString("\r\n").toByteArray(Charsets.UTF_8),
                        paths,
                        PlaylistIoSettings(ignoreCase = false),
                        Charsets.UTF_8.name(),
                        0,
                    )
                    .entries
                    .joinToString("\n") { it.path },
            libraryTrackPaths = paths,
            settings = PlaylistIoSettings(ignoreCase = false),
            expected = paths.toList(),
        )
    }

    @Test
    fun parseM3u_Relative() {
        testParseM3u(
            m3u =
                listOf(" a ", "/dir/dir2/b", "#c", "..\\dir3\\d", "C:\\e", "http://invalid")
                    .joinToString("\r\n"),
            libraryTrackPaths = parseM3uLibraryTrackPaths,
            settings =
                PlaylistIoSettings(
                    ignoreLocation = false,
                    removeInvalid = false,
                    relativeBase = "/dir",
                ),
            expected = listOf("/dir/a", "/dir/dir2/b", "/dir3/d", "C:/e", "http://invalid"),
        )
    }

    @Test
    fun toM3u_absolute() {
        assertThat(
                RealizedPlaylist(
                        null,
                        "",
                        listOf(realizedPlaylistEntry("/a/b"), realizedPlaylistEntry("/c/d")),
                        0,
                    )
                    .toM3u(PlaylistIoSettings())
                    .lines()
            )
            .containsExactlyInAnyOrder("/a/b", "/c/d")
    }

    @Test
    fun toM3u_relative() {
        assertThat(
                RealizedPlaylist(
                        null,
                        "",
                        listOf(realizedPlaylistEntry("/a/b"), realizedPlaylistEntry("/c/d")),
                        0,
                    )
                    .toM3u(PlaylistIoSettings(exportRelative = true, relativeBase = "/a"))
                    .lines()
            )
            .containsExactlyInAnyOrder("b", "../c/d")
    }

    @Test
    fun toM3u_relativeInvalid() {
        assertThat(
                RealizedPlaylist(
                        null,
                        "",
                        listOf(realizedPlaylistEntry("/a/b"), realizedPlaylistEntry("/c/d")),
                        0,
                    )
                    .toM3u(PlaylistIoSettings(exportRelative = true, relativeBase = "|"))
                    .lines()
            )
            .containsExactlyInAnyOrder("/a/b", "/c/d")
    }

    fun realizedPlaylistEntry(path: String): RealizedPlaylistEntry {
        return RealizedPlaylistEntry(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            0,
            InvalidTrack,
            PlaylistEntry(UUID.fromString("00000000-0000-0000-0000-000000000000"), path),
        )
    }
}

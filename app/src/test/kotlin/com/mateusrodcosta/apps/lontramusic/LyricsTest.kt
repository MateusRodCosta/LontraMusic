package org.sunsetware.phocid

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import org.assertj.core.api.Assertions.*
import org.junit.Test
import org.sunsetware.phocid.data.Lyrics
import org.sunsetware.phocid.data.parseLrc

class LyricsTest {
    @Test
    fun testParseLrc() {
        assertThat(
                parseLrc(
                        listOf(
                                "invalid",
                                "[in:va:lid]",
                                "[0:1,2]invalid",
                                "",
                                " ",
                                "[00:11.22]line1",
                                " [11:22.333] line2 ",
                                "[00:00.00][22:33.444]line3",
                                "[33:44.555]     ",
                            )
                            .joinToString("\r\n")
                    )
                    .lines
            )
            .containsExactly(
                timestamp(0, 0, 0) to "line3",
                timestamp(0, 11, 220) to "line1",
                timestamp(11, 22, 333) to "line2",
                timestamp(22, 33, 444) to "line3",
                timestamp(33, 44, 555) to "",
            )
    }

    @Test
    fun testGetLineIndex() {
        val lyrics =
            Lyrics(
                listOf(
                    timestamp(0, 0, 1) to "line0",
                    timestamp(1, 0, 1) to "line1",
                    timestamp(2, 0, 1) to "line2",
                    timestamp(2, 0, 1) to "line3",
                    timestamp(4, 0, 1) to "line4",
                )
            )
        assertThat(lyrics.getLineIndex(timestamp(0, 0, 0))).isEqualTo(null)
        assertThat(lyrics.getLineIndex(timestamp(0, 0, 1))).isEqualTo(0)
        assertThat(lyrics.getLineIndex(timestamp(0, 1, 1))).isEqualTo(0)
        assertThat(lyrics.getLineIndex(timestamp(1, 1, 1))).isEqualTo(1)
        assertThat(lyrics.getLineIndex(timestamp(2, 0, 1))).isEqualTo(2)
        assertThat(lyrics.getLineIndex(timestamp(2, 1, 1))).isEqualTo(2)
        assertThat(lyrics.getLineIndex(timestamp(4, 0, 1))).isEqualTo(4)
        assertThat(lyrics.getLineIndex(timestamp(5, 0, 1))).isEqualTo(4)
    }

    fun timestamp(minutes: Int, seconds: Int, milliseconds: Int): Duration {
        return minutes.minutes + seconds.seconds + milliseconds.milliseconds
    }
}

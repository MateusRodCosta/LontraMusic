package com.mateusrodcosta.apps.lontramusic

import com.ibm.icu.lang.UCharacter
import com.mateusrodcosta.apps.lontramusic.utils.initialLetter
import java.util.Locale
import kotlin.streams.toList
import org.assertj.core.api.Assertions.*
import org.junit.Test

class InitialismTest {
    @Test
    fun testInitialLetter() {
        assertThat("".initialLetter(Locale.ROOT)).isEqualTo("#")
        assertThat(" ABC".initialLetter(Locale.ROOT)).isEqualTo("#")
        assertThat(".ABC".initialLetter(Locale.ROOT)).isEqualTo("#")
        assertThat("123".initialLetter(Locale.ROOT)).isEqualTo("#")
        assertThat("😄".initialLetter(Locale.ROOT)).isEqualTo("#")
        assertThat("ABC".initialLetter(Locale.ROOT)).isEqualTo("A")
        assertThat("abc".initialLetter(Locale.ROOT)).isEqualTo("A")
        assertThat("abc".initialLetter(Locale.ROOT)).isEqualTo("A")
        assertThat("àbĆ".initialLetter(Locale.ROOT)).isEqualTo("À")
        assertThat("汉字".initialLetter(Locale.CHINESE)).isEqualTo("h")
        assertThat("汉字".initialLetter(Locale.SIMPLIFIED_CHINESE)).isEqualTo("h")
        assertThat("漢字".initialLetter(Locale.CHINESE)).isEqualTo("h")
        assertThat("\uD883\uDEDD".initialLetter(Locale.CHINESE)).isEqualTo("b")
        assertThat("漢字".initialLetter(Locale.TRADITIONAL_CHINESE)).isEqualTo("漢")
        assertThat("漢字".initialLetter(Locale.JAPANESE)).isEqualTo("漢")
        assertThat(
                "あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわゐゑをん"
                    .codePoints()
                    .toList()
                    .joinToString("") { UCharacter.toString(it).initialLetter(Locale.ROOT) }
            )
            .isEqualTo("あああああかかかかかさささささたたたたたなななななはははははまままままやややらららららわわわわん")
        assertThat(
                "びぴヒビピﾋぁぃぅぇぉゔ".codePoints().toList().joinToString("") {
                    UCharacter.toString(it).initialLetter(Locale.ROOT)
                }
            )
            .isEqualTo("ははははははああああああ")
    }
}

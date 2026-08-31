package com.mateusrodcosta.apps.lontramusic.utils

import com.ibm.icu.lang.UCharacter
import com.ibm.icu.text.Transliterator
import com.ibm.icu.text.UnicodeSet
import java.util.Locale

/** Input is assumed to be NFC normalized. */
fun String.initialLetter(locale: Locale): String {
    val first = firstCharacter()
    return when {
        first == null -> symbolInitial
        hanSet.contains(first) -> first.initialHan(locale)
        kanaSet.contains(first) -> first.initialKana()
        letterSet.contains(first) -> UCharacter.toUpperCase(locale, first)
        else -> symbolInitial
    }
}

private const val symbolInitial = "#"

private val hanSet = UnicodeSet("[:Hani:]").freeze()
private val kanaSet = UnicodeSet("[[:Hira:]+[:Kana:]]").freeze()
private val letterSet = UnicodeSet("[:Letter:]").freeze()

private val pinyinTransliterator =
    Transliterator.getInstance("Han-Latin; NFD; [:Mark:] Remove; NFC; Lower")

private fun String.initialHan(locale: Locale): String {
    // ICU only has Han -> Pinyin conversion, so we can't get a meaningful result for
    // locales other than zh;
    // CLDR sorts all zh-Hant locales by stroke, so they should be also excluded
    return if (
        locale.language == "zh" &&
            locale.toLanguageTag().let {
                it.contains("Hans") ||
                    !it.contains("Hant") &&
                        !it.contains("HK") &&
                        !it.contains("MO") &&
                        !it.contains("TW")
            }
    )
        pinyinTransliterator.transliterate(this).firstCharacter() ?: symbolInitial
    else this
}

private val kanaTransliterator =
    Transliterator.getInstance("NFD; [:Mark:] Remove; NFC; Any-Latin; [:^Letter:] Remove; Lower")

private fun String.initialKana(): String {
    val s = kanaTransliterator.transliterate(this)
    val c = s.firstCharacter()
    return when (s) {
        "a",
        "i",
        "u",
        "e",
        "o" -> "あ"
        "n" -> "ん"
        else ->
            when (c) {
                "k" -> "か"
                "s" -> "さ"
                "t",
                "c" -> "た"
                "n" -> "な"
                "h",
                "f" -> "は"
                "m" -> "ま"
                "y" -> "や"
                "r" -> "ら"
                "w" -> "わ"
                else -> this
            }
    }
}

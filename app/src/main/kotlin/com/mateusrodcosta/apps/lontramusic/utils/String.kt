package com.mateusrodcosta.apps.lontramusic.utils

import android.util.Log
import com.ibm.icu.lang.UCharacter
import com.ibm.icu.number.NumberFormatter
import com.ibm.icu.text.CharsetDetector
import com.ibm.icu.text.MessageFormat
import com.ibm.icu.text.Normalizer2
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.Locale
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

private val casefolder = Normalizer2.getNFKCCasefoldInstance()

fun String.trimAndNormalize(): String {
    return Normalizer2.getNFCInstance().normalize(this.trim())
}

fun String.icuFormat(vararg args: Any?): String {
    return try {
        MessageFormat.format(this, *args)
    } catch (ex: Exception) {
        Log.e("Phocid", "Can't format string \"$this\" with (${args.joinToString(", ")})", ex)
        this
    }
}

fun String.firstCharacter(): String? {
    return if (isEmpty()) null else UCharacter.toString(codePointAt(0))
}

fun ByteArray.decodeWithCharsetName(charsetName: String?): String {
    return if (charsetName != null && Charset.isSupported(charsetName)) {
        Charset.forName(charsetName).decode(ByteBuffer.wrap(this)).toString()
    } else {
        CharsetDetector().setText(this).detect().string
    }
}

fun Iterable<String>.distinctCaseInsensitive(): List<String> {
    return groupBy { casefolder.normalize(it) }.map { it.value.mode() }
}

fun Int.toLocalizedString(): String {
    return NumberFormatter.withLocale(Locale.getDefault()).format(this).toString()
}

fun Long.toLocalizedString(): String {
    return NumberFormatter.withLocale(Locale.getDefault()).format(this).toString()
}

fun Float.toLocalizedString(): String {
    return NumberFormatter.withLocale(Locale.getDefault()).format(this).toString()
}

fun Double.toLocalizedString(): String {
    return NumberFormatter.withLocale(Locale.getDefault()).format(this).toString()
}

@Serializable(with = CaseInsensitiveMapSerializer::class)
class CaseInsensitiveMap<T> private constructor(private val inner: Map<String, T>) :
    Map<String, T> {
    constructor(
        map: Map<String, T>,
        combinator: (List<T>) -> T,
    ) : this(
        map.map { (key, value) -> Pair(casefolder.normalize(key), value) }
            .groupBy({ it.first }, { it.second })
            .map { Pair(it.key, combinator(it.value)) }
            .toMap()
    )

    override val entries
        get() = inner.entries

    override val keys
        get() = inner.keys

    override val size
        get() = inner.size

    override val values
        get() = inner.values

    override fun isEmpty(): Boolean {
        return inner.isEmpty()
    }

    override fun get(key: String): T? {
        return inner[casefolder.normalize(key)]
    }

    override fun containsValue(value: T): Boolean {
        return inner.containsValue(value)
    }

    override fun containsKey(key: String): Boolean {
        return inner.containsKey(casefolder.normalize(key))
    }

    fun <R> map(transform: (Map.Entry<String, T>) -> R): CaseInsensitiveMap<R> {
        return CaseInsensitiveMap(inner.mapValues(transform))
    }

    companion object {
        fun <T> noMerge(map: Map<String, T>): CaseInsensitiveMap<T> {
            return CaseInsensitiveMap(map.mapKeys { casefolder.normalize(it.key) })
        }
    }
}

class CaseInsensitiveMapSerializer<T>(valueSerializer: KSerializer<T>) :
    KSerializer<CaseInsensitiveMap<T>> {
    private val surrogateSerializer = MapSerializer(String.serializer(), valueSerializer)
    override val descriptor: SerialDescriptor = surrogateSerializer.descriptor

    override fun serialize(encoder: Encoder, value: CaseInsensitiveMap<T>) {
        encoder.encodeSerializableValue(surrogateSerializer, value)
    }

    override fun deserialize(decoder: Decoder): CaseInsensitiveMap<T> {
        return CaseInsensitiveMap(decoder.decodeSerializableValue(surrogateSerializer)) {
            if (it.size != 1) {
                throw IllegalArgumentException(
                    "Deserializing CaseInsensitiveMap with conflicting keys"
                )
            }
            it.first()
        }
    }
}

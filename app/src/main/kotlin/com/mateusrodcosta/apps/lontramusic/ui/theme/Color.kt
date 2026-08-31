package com.mateusrodcosta.apps.lontramusic.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.colorspace.connect
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import java.nio.ByteBuffer
import java.security.MessageDigest
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Stable
fun Color.contentColor(): Color {
    return if (luminance() < 0.5f) lerp(this, Color.White, 0.9f) else lerp(this, Color.Black, 0.4f)
}

@Stable
fun Color.darken(): Color {
    return lerp(
        this,
        Color.Black,
        this.luminance().let { if (it < 0.5f) it * 0.05f + 0.2f else 0.4f },
    )
}

@Composable
fun contentColorVariant(): Color {
    val contentColor = LocalContentColor.current
    with(MaterialTheme.colorScheme) {
        return when (contentColor) {
            onSurface -> onSurfaceVariant
            else -> contentColor.copy(alpha = VARIANT_ALPHA)
        }
    }
}

private val oklabToSrgbConnector = ColorSpaces.Oklab.connect(ColorSpaces.Srgb)
private val srgbToOklabConnector = ColorSpaces.Srgb.connect(ColorSpaces.Oklab)

@Stable
fun oklab(l: Float, a: Float, b: Float): Color {
    val rgb = oklabToSrgbConnector.transform(l, a, b)
    return Color(rgb[0], rgb[1], rgb[2])
}

data class Oklch(val l: Float, val c: Float, val h: Float) {
    fun toColor(): Color {
        return oklab(l, c * cos(h), c * sin(h))
    }
}

@Stable
fun Color.toOklch(): Oklch {
    val lab = srgbToOklabConnector.transform(red, green, blue)
    val l = lab[0]
    val a = lab[1]
    val b = lab[2]
    val c = sqrt(a * a + b * b)
    val h = atan2(b, a)
    return Oklch(l, c, h)
}

@Stable
fun String.hashColor(): Color {
    val hash =
        ByteBuffer.wrap(MessageDigest.getInstance("MD5").digest(this.toByteArray(Charsets.UTF_8)))
    val l = (hash.getShort(0) + 32768).toFloat() / 65536 * 0.1f + 0.6f
    val c = sqrt((hash.getShort(2) + 32768).toFloat() / 65536) * 0.05f
    val h = (hash.getShort(4) + 32768).toFloat() / 65536 * 2 * PI.toFloat()
    return Oklch(l, c, h).toColor()
}

val GRAY = oklab(0.6f, 0f, 0f)
val primary400 = Oklch(0.6f, 0.09f, 290f / 360 * 2 * PI.toFloat()).toColor()

/** Should have been 0.38 according to Material 3, but that would be completely unreadable. */
const val INACTIVE_ALPHA = 0.5f
const val VARIANT_ALPHA = 0.85f

// region Theme builder

val primaryLight = Color(0xFF605790)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFE6DEFF)
val onPrimaryContainerLight = Color(0xFF1C1149)
val secondaryLight = Color(0xFF605C71)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFE6DFF9)
val onSecondaryContainerLight = Color(0xFF1C192B)
val tertiaryLight = Color(0xFF7C5264)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFFFD8E6)
val onTertiaryContainerLight = Color(0xFF301120)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF410002)
val backgroundLight = Color(0xFFFDF8FF)
val onBackgroundLight = Color(0xFF1C1B20)
val surfaceLight = Color(0xFFFDF8FF)
val onSurfaceLight = Color(0xFF1C1B20)
val surfaceVariantLight = Color(0xFFE6E0EC)
val onSurfaceVariantLight = Color(0xFF48454E)
val outlineLight = Color(0xFF79757F)
val outlineVariantLight = Color(0xFFC9C5D0)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF312F36)
val inverseOnSurfaceLight = Color(0xFFF4EFF7)
val inversePrimaryLight = Color(0xFFC9BEFF)
val surfaceDimLight = Color(0xFFDDD8E0)
val surfaceBrightLight = Color(0xFFFDF8FF)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF7F2FA)
val surfaceContainerLight = Color(0xFFF1ECF4)
val surfaceContainerHighLight = Color(0xFFEBE6EE)
val surfaceContainerHighestLight = Color(0xFFE5E1E9)

val primaryLightMediumContrast = Color(0xFF443B73)
val onPrimaryLightMediumContrast = Color(0xFFFFFFFF)
val primaryContainerLightMediumContrast = Color(0xFF766DA8)
val onPrimaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val secondaryLightMediumContrast = Color(0xFF444054)
val onSecondaryLightMediumContrast = Color(0xFFFFFFFF)
val secondaryContainerLightMediumContrast = Color(0xFF767288)
val onSecondaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val tertiaryLightMediumContrast = Color(0xFF5D3748)
val onTertiaryLightMediumContrast = Color(0xFFFFFFFF)
val tertiaryContainerLightMediumContrast = Color(0xFF94687A)
val onTertiaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val errorLightMediumContrast = Color(0xFF8C0009)
val onErrorLightMediumContrast = Color(0xFFFFFFFF)
val errorContainerLightMediumContrast = Color(0xFFDA342E)
val onErrorContainerLightMediumContrast = Color(0xFFFFFFFF)
val backgroundLightMediumContrast = Color(0xFFFDF8FF)
val onBackgroundLightMediumContrast = Color(0xFF1C1B20)
val surfaceLightMediumContrast = Color(0xFFFDF8FF)
val onSurfaceLightMediumContrast = Color(0xFF1C1B20)
val surfaceVariantLightMediumContrast = Color(0xFFE6E0EC)
val onSurfaceVariantLightMediumContrast = Color(0xFF44424A)
val outlineLightMediumContrast = Color(0xFF605E67)
val outlineVariantLightMediumContrast = Color(0xFF7C7983)
val scrimLightMediumContrast = Color(0xFF000000)
val inverseSurfaceLightMediumContrast = Color(0xFF312F36)
val inverseOnSurfaceLightMediumContrast = Color(0xFFF4EFF7)
val inversePrimaryLightMediumContrast = Color(0xFFC9BEFF)
val surfaceDimLightMediumContrast = Color(0xFFDDD8E0)
val surfaceBrightLightMediumContrast = Color(0xFFFDF8FF)
val surfaceContainerLowestLightMediumContrast = Color(0xFFFFFFFF)
val surfaceContainerLowLightMediumContrast = Color(0xFFF7F2FA)
val surfaceContainerLightMediumContrast = Color(0xFFF1ECF4)
val surfaceContainerHighLightMediumContrast = Color(0xFFEBE6EE)
val surfaceContainerHighestLightMediumContrast = Color(0xFFE5E1E9)

val primaryLightHighContrast = Color(0xFF231950)
val onPrimaryLightHighContrast = Color(0xFFFFFFFF)
val primaryContainerLightHighContrast = Color(0xFF443B73)
val onPrimaryContainerLightHighContrast = Color(0xFFFFFFFF)
val secondaryLightHighContrast = Color(0xFF232032)
val onSecondaryLightHighContrast = Color(0xFFFFFFFF)
val secondaryContainerLightHighContrast = Color(0xFF444054)
val onSecondaryContainerLightHighContrast = Color(0xFFFFFFFF)
val tertiaryLightHighContrast = Color(0xFF381727)
val onTertiaryLightHighContrast = Color(0xFFFFFFFF)
val tertiaryContainerLightHighContrast = Color(0xFF5D3748)
val onTertiaryContainerLightHighContrast = Color(0xFFFFFFFF)
val errorLightHighContrast = Color(0xFF4E0002)
val onErrorLightHighContrast = Color(0xFFFFFFFF)
val errorContainerLightHighContrast = Color(0xFF8C0009)
val onErrorContainerLightHighContrast = Color(0xFFFFFFFF)
val backgroundLightHighContrast = Color(0xFFFDF8FF)
val onBackgroundLightHighContrast = Color(0xFF1C1B20)
val surfaceLightHighContrast = Color(0xFFFDF8FF)
val onSurfaceLightHighContrast = Color(0xFF000000)
val surfaceVariantLightHighContrast = Color(0xFFE6E0EC)
val onSurfaceVariantLightHighContrast = Color(0xFF25232B)
val outlineLightHighContrast = Color(0xFF44424A)
val outlineVariantLightHighContrast = Color(0xFF44424A)
val scrimLightHighContrast = Color(0xFF000000)
val inverseSurfaceLightHighContrast = Color(0xFF312F36)
val inverseOnSurfaceLightHighContrast = Color(0xFFFFFFFF)
val inversePrimaryLightHighContrast = Color(0xFFEFE9FF)
val surfaceDimLightHighContrast = Color(0xFFDDD8E0)
val surfaceBrightLightHighContrast = Color(0xFFFDF8FF)
val surfaceContainerLowestLightHighContrast = Color(0xFFFFFFFF)
val surfaceContainerLowLightHighContrast = Color(0xFFF7F2FA)
val surfaceContainerLightHighContrast = Color(0xFFF1ECF4)
val surfaceContainerHighLightHighContrast = Color(0xFFEBE6EE)
val surfaceContainerHighestLightHighContrast = Color(0xFFE5E1E9)

val primaryDark = Color(0xFFC9BEFF)
val onPrimaryDark = Color(0xFF31285F)
val primaryContainerDark = Color(0xFF483F77)
val onPrimaryContainerDark = Color(0xFFE6DEFF)
val secondaryDark = Color(0xFFC9C3DC)
val onSecondaryDark = Color(0xFF312E41)
val secondaryContainerDark = Color(0xFF484459)
val onSecondaryContainerDark = Color(0xFFE6DFF9)
val tertiaryDark = Color(0xFFEDB8CD)
val onTertiaryDark = Color(0xFF482535)
val tertiaryContainerDark = Color(0xFF623B4C)
val onTertiaryContainerDark = Color(0xFFFFD8E6)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF141318)
val onBackgroundDark = Color(0xFFE5E1E9)
val surfaceDark = Color(0xFF141318)
val onSurfaceDark = Color(0xFFE5E1E9)
val surfaceVariantDark = Color(0xFF48454E)
val onSurfaceVariantDark = Color(0xFFC9C5D0)
val outlineDark = Color(0xFF938F99)
val outlineVariantDark = Color(0xFF48454E)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE5E1E9)
val inverseOnSurfaceDark = Color(0xFF312F36)
val inversePrimaryDark = Color(0xFF605790)
val surfaceDimDark = Color(0xFF141318)
val surfaceBrightDark = Color(0xFF3A383E)
val surfaceContainerLowestDark = Color(0xFF0F0D13)
val surfaceContainerLowDark = Color(0xFF1C1B20)
val surfaceContainerDark = Color(0xFF201F25)
val surfaceContainerHighDark = Color(0xFF2B292F)
val surfaceContainerHighestDark = Color(0xFF36343A)

val primaryDarkMediumContrast = Color(0xFFCEC3FF)
val onPrimaryDarkMediumContrast = Color(0xFF160A44)
val primaryContainerDarkMediumContrast = Color(0xFF9389C6)
val onPrimaryContainerDarkMediumContrast = Color(0xFF000000)
val secondaryDarkMediumContrast = Color(0xFFCDC7E0)
val onSecondaryDarkMediumContrast = Color(0xFF171426)
val secondaryContainerDarkMediumContrast = Color(0xFF938EA5)
val onSecondaryContainerDarkMediumContrast = Color(0xFF000000)
val tertiaryDarkMediumContrast = Color(0xFFF1BCD1)
val onTertiaryDarkMediumContrast = Color(0xFF2A0B1B)
val tertiaryContainerDarkMediumContrast = Color(0xFFB38397)
val onTertiaryContainerDarkMediumContrast = Color(0xFF000000)
val errorDarkMediumContrast = Color(0xFFFFBAB1)
val onErrorDarkMediumContrast = Color(0xFF370001)
val errorContainerDarkMediumContrast = Color(0xFFFF5449)
val onErrorContainerDarkMediumContrast = Color(0xFF000000)
val backgroundDarkMediumContrast = Color(0xFF141318)
val onBackgroundDarkMediumContrast = Color(0xFFE5E1E9)
val surfaceDarkMediumContrast = Color(0xFF141318)
val onSurfaceDarkMediumContrast = Color(0xFFFEF9FF)
val surfaceVariantDarkMediumContrast = Color(0xFF48454E)
val onSurfaceVariantDarkMediumContrast = Color(0xFFCDC9D4)
val outlineDarkMediumContrast = Color(0xFFA5A1AC)
val outlineVariantDarkMediumContrast = Color(0xFF85818C)
val scrimDarkMediumContrast = Color(0xFF000000)
val inverseSurfaceDarkMediumContrast = Color(0xFFE5E1E9)
val inverseOnSurfaceDarkMediumContrast = Color(0xFF2B292F)
val inversePrimaryDarkMediumContrast = Color(0xFF494078)
val surfaceDimDarkMediumContrast = Color(0xFF141318)
val surfaceBrightDarkMediumContrast = Color(0xFF3A383E)
val surfaceContainerLowestDarkMediumContrast = Color(0xFF0F0D13)
val surfaceContainerLowDarkMediumContrast = Color(0xFF1C1B20)
val surfaceContainerDarkMediumContrast = Color(0xFF201F25)
val surfaceContainerHighDarkMediumContrast = Color(0xFF2B292F)
val surfaceContainerHighestDarkMediumContrast = Color(0xFF36343A)

val primaryDarkHighContrast = Color(0xFFFEF9FF)
val onPrimaryDarkHighContrast = Color(0xFF000000)
val primaryContainerDarkHighContrast = Color(0xFFCEC3FF)
val onPrimaryContainerDarkHighContrast = Color(0xFF000000)
val secondaryDarkHighContrast = Color(0xFFFEF9FF)
val onSecondaryDarkHighContrast = Color(0xFF000000)
val secondaryContainerDarkHighContrast = Color(0xFFCDC7E0)
val onSecondaryContainerDarkHighContrast = Color(0xFF000000)
val tertiaryDarkHighContrast = Color(0xFFFFF9F9)
val onTertiaryDarkHighContrast = Color(0xFF000000)
val tertiaryContainerDarkHighContrast = Color(0xFFF1BCD1)
val onTertiaryContainerDarkHighContrast = Color(0xFF000000)
val errorDarkHighContrast = Color(0xFFFFF9F9)
val onErrorDarkHighContrast = Color(0xFF000000)
val errorContainerDarkHighContrast = Color(0xFFFFBAB1)
val onErrorContainerDarkHighContrast = Color(0xFF000000)
val backgroundDarkHighContrast = Color(0xFF141318)
val onBackgroundDarkHighContrast = Color(0xFFE5E1E9)
val surfaceDarkHighContrast = Color(0xFF141318)
val onSurfaceDarkHighContrast = Color(0xFFFFFFFF)
val surfaceVariantDarkHighContrast = Color(0xFF48454E)
val onSurfaceVariantDarkHighContrast = Color(0xFFFEF9FF)
val outlineDarkHighContrast = Color(0xFFCDC9D4)
val outlineVariantDarkHighContrast = Color(0xFFCDC9D4)
val scrimDarkHighContrast = Color(0xFF000000)
val inverseSurfaceDarkHighContrast = Color(0xFFE5E1E9)
val inverseOnSurfaceDarkHighContrast = Color(0xFF000000)
val inversePrimaryDarkHighContrast = Color(0xFF2B2158)
val surfaceDimDarkHighContrast = Color(0xFF141318)
val surfaceBrightDarkHighContrast = Color(0xFF3A383E)
val surfaceContainerLowestDarkHighContrast = Color(0xFF0F0D13)
val surfaceContainerLowDarkHighContrast = Color(0xFF1C1B20)
val surfaceContainerDarkHighContrast = Color(0xFF201F25)
val surfaceContainerHighDarkHighContrast = Color(0xFF2B292F)
val surfaceContainerHighestDarkHighContrast = Color(0xFF36343A)

// endregion

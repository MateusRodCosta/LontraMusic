package com.mateusrodcosta.apps.lontramusic.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle

// Set of Material typography styles to start with
val Typography = typography(1.5f)

private fun transform(textStyle: TextStyle, lineHeightMultiplier: Float): TextStyle {
    return textStyle.copy(
        lineHeight = textStyle.fontSize * lineHeightMultiplier,
        lineHeightStyle =
            LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
    )
}

@Suppress("SameParameterValue")
private fun typography(lineHeightMultiplier: Float): Typography {
    val default = Typography()
    return Typography(
        displayLarge = transform(default.displayLarge, lineHeightMultiplier),
        displayMedium = transform(default.displayMedium, lineHeightMultiplier),
        displaySmall = transform(default.displaySmall, lineHeightMultiplier),
        headlineLarge = transform(default.headlineLarge, lineHeightMultiplier),
        headlineMedium = transform(default.headlineMedium, lineHeightMultiplier),
        headlineSmall = transform(default.headlineSmall, lineHeightMultiplier),
        titleLarge = transform(default.titleLarge, lineHeightMultiplier),
        titleMedium = transform(default.titleMedium, lineHeightMultiplier),
        titleSmall = transform(default.titleSmall, lineHeightMultiplier),
        bodyLarge = transform(default.bodyLarge, lineHeightMultiplier),
        bodyMedium = transform(default.bodyMedium, lineHeightMultiplier),
        bodySmall = transform(default.bodySmall, lineHeightMultiplier),
        labelLarge = transform(default.labelLarge, lineHeightMultiplier),
        labelMedium = transform(default.labelMedium, lineHeightMultiplier),
        labelSmall = transform(default.labelSmall, lineHeightMultiplier),
    )
}

@Stable
fun TextStyle.toGlanceStyle(): androidx.glance.text.TextStyle {
    return androidx.glance.text.TextStyle(fontSize = fontSize)
}

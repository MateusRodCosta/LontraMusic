package com.mateusrodcosta.apps.lontramusic.ui.views.preferences

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mateusrodcosta.apps.lontramusic.Dialog
import com.mateusrodcosta.apps.lontramusic.MainViewModel
import com.mateusrodcosta.apps.lontramusic.data.CustomThemeColor
import com.mateusrodcosta.apps.lontramusic.data.ThemeColorSource
import com.mateusrodcosta.apps.lontramusic.globals.Strings
import com.mateusrodcosta.apps.lontramusic.ui.components.DialogBase
import com.mateusrodcosta.apps.lontramusic.ui.components.SelectBox
import com.mateusrodcosta.apps.lontramusic.ui.components.SteppedSliderWithNumber
import com.mateusrodcosta.apps.lontramusic.ui.components.UtilityListHeader
import com.mateusrodcosta.apps.lontramusic.ui.components.UtilitySwitchListItem
import com.mateusrodcosta.apps.lontramusic.utils.icuFormat
import com.mateusrodcosta.apps.lontramusic.utils.roundToIntOrZero
import org.sunsetware.phocid.R

@Stable
class PreferencesThemeColorDialog() : Dialog() {
    @Composable
    override fun Compose(viewModel: MainViewModel) {
        val preferences by viewModel.preferences.collectAsStateWithLifecycle()
        var chromaPercentage by rememberSaveable {
            mutableIntStateOf(preferences.customThemeColor.chromaPercentage)
        }
        var hueDegrees by rememberSaveable {
            mutableIntStateOf(preferences.customThemeColor.hueDegrees)
        }
        val previewColor =
            remember(chromaPercentage, hueDegrees) {
                CustomThemeColor(chromaPercentage, hueDegrees).toColor(0.6f)
            }

        DialogBase(
            title = Strings[R.string.preferences_theme_color],
            onConfirmOrDismiss = { viewModel.uiManager.closeDialog() },
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                SelectBox(
                    items = ThemeColorSource.entries.map { Strings[it.stringId] },
                    activeIndex = ThemeColorSource.entries.indexOf(preferences.themeColorSource),
                    onSetActiveIndex = { index ->
                        viewModel.updatePreferences {
                            it.copy(themeColorSource = ThemeColorSource.entries[index])
                        }
                    },
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
                AnimatedVisibility(preferences.themeColorSource == ThemeColorSource.CUSTOM) {
                    Column {
                        Box(
                            modifier =
                                Modifier.padding(24.dp)
                                    .fillMaxWidth()
                                    .height(24.dp)
                                    .background(previewColor)
                        )

                        UtilityListHeader(Strings[R.string.preferences_theme_color_chroma])
                        SteppedSliderWithNumber(
                            number =
                                Strings[R.string.preferences_theme_color_chroma_number].icuFormat(
                                    chromaPercentage
                                ),
                            value = chromaPercentage.toFloat(),
                            onValueChange = { chromaPercentage = it.roundToIntOrZero() },
                            onValueChangeFinished = {
                                viewModel.updatePreferences {
                                    it.copy(
                                        customThemeColor =
                                            CustomThemeColor(chromaPercentage, hueDegrees)
                                    )
                                }
                            },
                            steps = 100 - 1,
                            valueRange = 0f..100f,
                            modifier = Modifier.padding(horizontal = 24.dp),
                        )
                        UtilityListHeader(Strings[R.string.preferences_theme_color_hue])
                        SteppedSliderWithNumber(
                            number =
                                Strings[R.string.preferences_theme_color_hue_number].icuFormat(
                                    hueDegrees
                                ),
                            value = hueDegrees.toFloat(),
                            onValueChange = { hueDegrees = it.roundToIntOrZero() },
                            onValueChangeFinished = {
                                viewModel.updatePreferences {
                                    it.copy(
                                        customThemeColor =
                                            CustomThemeColor(chromaPercentage, hueDegrees)
                                    )
                                }
                            },
                            steps = 359 - 1,
                            valueRange = 0f..359f,
                            modifier = Modifier.padding(horizontal = 24.dp),
                        )
                    }
                }
                UtilitySwitchListItem(
                    title = Strings[R.string.preferences_colored_global_theme],
                    checked = preferences.coloredGlobalTheme,
                    onCheckedChange = { checked ->
                        viewModel.updatePreferences { it.copy(coloredGlobalTheme = checked) }
                    },
                )
            }
        }
    }
}

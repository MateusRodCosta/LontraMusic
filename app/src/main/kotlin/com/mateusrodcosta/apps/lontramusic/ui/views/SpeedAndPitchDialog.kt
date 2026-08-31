@file:OptIn(ExperimentalMaterial3Api::class)

package com.mateusrodcosta.apps.lontramusic.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mateusrodcosta.apps.lontramusic.Dialog
import com.mateusrodcosta.apps.lontramusic.MainViewModel
import com.mateusrodcosta.apps.lontramusic.globals.Strings
import com.mateusrodcosta.apps.lontramusic.ui.components.DialogBase
import com.mateusrodcosta.apps.lontramusic.ui.components.SteppedSliderWithNumber
import com.mateusrodcosta.apps.lontramusic.ui.components.UtilityCheckBoxListItem
import com.mateusrodcosta.apps.lontramusic.ui.components.UtilityListHeader
import com.mateusrodcosta.apps.lontramusic.utils.icuFormat
import com.mateusrodcosta.apps.lontramusic.utils.roundToIntOrZero
import kotlin.math.log
import kotlin.math.pow
import org.sunsetware.phocid.R

@Stable
class SpeedAndPitchDialog() : Dialog() {
    @Composable
    override fun Compose(viewModel: MainViewModel) {
        val playerManager = viewModel.playerManager
        var newSpeedTimes100 by remember {
            mutableIntStateOf((viewModel.playerManager.state.value.speed * 100).roundToIntOrZero())
        }
        var resample by remember {
            mutableStateOf(
                viewModel.playerManager.state.value.let { it.speed == it.pitch && it.speed != 1f }
            )
        }
        var newPitchSemitones by remember {
            mutableIntStateOf(
                if (resample) 0
                else (log(viewModel.playerManager.state.value.pitch, 2f) * 12).roundToIntOrZero()
            )
        }
        DialogBase(
            title = Strings[R.string.player_speed_and_pitch],
            onConfirm = {
                playerManager.setSpeedAndPitch(
                    newSpeedTimes100 / 100f,
                    if (resample) newSpeedTimes100 / 100f else 2f.pow(newPitchSemitones / 12f),
                )
                viewModel.uiManager.closeDialog()
            },
            onDismiss = { viewModel.uiManager.closeDialog() },
        ) {
            Column {
                UtilityListHeader(Strings[R.string.player_speed_and_pitch_speed])
                SteppedSliderWithNumber(
                    number =
                        Strings[R.string.player_speed_and_pitch_speed_number].icuFormat(
                            newSpeedTimes100 / 100f
                        ),
                    onReset = { newSpeedTimes100 = 100 },
                    value = newSpeedTimes100.toFloat(),
                    onValueChange = { newSpeedTimes100 = it.roundToIntOrZero() },
                    steps = 300 - 10 - 1,
                    valueRange = 10f..300f,
                    modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
                )
                UtilityListHeader(Strings[R.string.player_speed_and_pitch_pitch])
                SteppedSliderWithNumber(
                    number =
                        if (resample)
                            Strings[R.string.player_speed_and_pitch_speed_number].icuFormat(
                                newSpeedTimes100 / 100f
                            )
                        else
                            Strings[R.string.player_speed_and_pitch_pitch_number].icuFormat(
                                newPitchSemitones
                            ),
                    onReset = { newPitchSemitones = 0 },
                    value =
                        if (resample) newSpeedTimes100.toFloat() else newPitchSemitones.toFloat(),
                    onValueChange = {
                        if (!resample) {
                            newPitchSemitones = it.roundToIntOrZero()
                        }
                    },
                    steps = if (resample) 300 - 10 - 1 else 24 - (-24) - 1,
                    valueRange = if (resample) 10f..300f else -24f..24f,
                    modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
                    enabled = !resample,
                )
                UtilityCheckBoxListItem(
                    Strings[R.string.player_speed_and_pitch_match_pitch_to_speed],
                    resample,
                    { resample = it },
                )
            }
        }
    }
}

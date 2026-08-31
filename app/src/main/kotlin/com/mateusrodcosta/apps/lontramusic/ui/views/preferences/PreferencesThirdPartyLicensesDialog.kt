package com.mateusrodcosta.apps.lontramusic.ui.views.preferences

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mateusrodcosta.apps.lontramusic.Dialog
import com.mateusrodcosta.apps.lontramusic.MainViewModel
import com.mateusrodcosta.apps.lontramusic.R
import com.mateusrodcosta.apps.lontramusic.globals.Strings
import com.mateusrodcosta.apps.lontramusic.ui.components.DialogBase
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer

class PreferencesThirdPartyLicensesDialog : Dialog() {
    @Composable
    override fun Compose(viewModel: MainViewModel) {
        val libraries by produceLibraries(R.raw.aboutlibraries)

        DialogBase(
            Strings[R.string.preferences_third_party_licenses],
            onConfirmOrDismiss = { viewModel.uiManager.closeDialog() },
        ) {
            LibrariesContainer(libraries, Modifier.fillMaxSize())
        }
    }
}

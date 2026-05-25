package org.sunsetware.phocid.ui.views.preferences

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import org.sunsetware.phocid.Dialog
import org.sunsetware.phocid.MainViewModel
import org.sunsetware.phocid.R
import org.sunsetware.phocid.globals.Strings
import org.sunsetware.phocid.ui.components.DialogBase

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

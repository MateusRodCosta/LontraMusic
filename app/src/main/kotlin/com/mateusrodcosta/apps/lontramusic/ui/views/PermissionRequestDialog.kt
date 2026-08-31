@file:OptIn(ExperimentalPermissionsApi::class)

package com.mateusrodcosta.apps.lontramusic.ui.views

import android.app.Activity
import android.content.ContextWrapper
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.mateusrodcosta.apps.lontramusic.Dialog
import com.mateusrodcosta.apps.lontramusic.MainViewModel
import com.mateusrodcosta.apps.lontramusic.R
import com.mateusrodcosta.apps.lontramusic.globals.Strings
import com.mateusrodcosta.apps.lontramusic.ui.components.DialogBase

@Stable
class PermissionRequestDialog(
    private val permissions: MultiplePermissionsState,
    private val onPermissionGranted: () -> Unit,
) : Dialog() {
    @Composable
    override fun Compose(viewModel: MainViewModel) {
        val context = LocalContext.current
        DialogBase(
            title = Strings[R.string.permission_dialog_title],
            onConfirm = { permissions.launchMultiplePermissionRequest() },
            onDismiss = {
                // https://github.com/google/accompanist/blob/a9506584939ed9c79890adaaeb58de01ed0bb823/permissions/src/main/java/com/google/accompanist/permissions/PermissionsUtil.kt#L132
                var ctx = context
                while (ctx is ContextWrapper) {
                    if (ctx is Activity) break
                    ctx = ctx.baseContext
                }
                (ctx as? Activity)?.finishAffinity()
            },
            confirmText = Strings[R.string.permission_dialog_grant],
            dismissText = Strings[R.string.commons_quit],
            properties = DialogProperties(dismissOnClickOutside = false),
        ) {
            Text(
                Strings[R.string.permission_dialog_body],
                modifier = Modifier.padding(horizontal = 24.dp),
            )

            LaunchedEffect(permissions.allPermissionsGranted) {
                if (permissions.allPermissionsGranted) {
                    onPermissionGranted()
                    viewModel.uiManager.closeDialog()
                }
            }
        }
    }
}

@file:OptIn(ExperimentalMaterial3Api::class)

package com.mateusrodcosta.apps.lontramusic.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.mateusrodcosta.apps.lontramusic.R
import com.mateusrodcosta.apps.lontramusic.globals.Strings

// Don't mark these functions as inline unless you want a "Compose internal error"

@Composable
fun DialogBase(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = Strings[R.string.commons_ok],
    dismissText: String = Strings[R.string.commons_cancel],
    confirmEnabled: Boolean = true,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit,
) {
    AlertDialog(
        title = { Text(title) },
        text = {
            Box(modifier = Modifier.negativePadding(horizontal = 24.dp).fillMaxWidth()) {
                content()
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = confirmEnabled) { Text(confirmText) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(dismissText) } },
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.padding(vertical = 16.dp),
        properties = properties,
    )
}

@Composable
fun DialogBase(
    title: String,
    onConfirmOrDismiss: () -> Unit,
    confirmText: String = Strings[R.string.commons_ok],
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit,
) {
    AlertDialog(
        title = { Text(title) },
        text = {
            Box(modifier = Modifier.negativePadding(horizontal = 24.dp).fillMaxWidth()) {
                content()
            }
        },
        confirmButton = { TextButton(onClick = onConfirmOrDismiss) { Text(confirmText) } },
        onDismissRequest = onConfirmOrDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.padding(vertical = 16.dp),
        properties = properties,
    )
}

@Composable
fun DialogBase(
    onDismiss: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.padding(vertical = 16.dp),
        properties = properties,
    ) {
        Surface(
            shape = AlertDialogDefaults.shape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = AlertDialogDefaults.textContentColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
            modifier = Modifier.wrapContentHeight(),
        ) {
            content()
        }
    }
}

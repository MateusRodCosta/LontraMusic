package com.mateusrodcosta.apps.lontramusic.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mateusrodcosta.apps.lontramusic.globals.Strings
import com.mateusrodcosta.apps.lontramusic.ui.views.MenuItem
import org.sunsetware.phocid.R

@Composable
fun OverflowMenu(
    items: List<MenuItem>,
    modifier: Modifier = Modifier,
    state: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    var expanded by state

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Filled.MoreVert, contentDescription = Strings[R.string.commons_more])
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach {
                when (it) {
                    is MenuItem.Button -> {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    it.text,
                                    color =
                                        if (it.dangerous) MaterialTheme.colorScheme.error
                                        else Color.Unspecified,
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    it.icon,
                                    contentDescription = null,
                                    tint =
                                        if (it.dangerous) MaterialTheme.colorScheme.error
                                        else LocalContentColor.current,
                                )
                            },
                            onClick = {
                                it.onClick()
                                expanded = false
                            },
                        )
                    }
                    is MenuItem.Divider -> {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}

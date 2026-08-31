@file:OptIn(ExperimentalLayoutApi::class)

package com.mateusrodcosta.apps.lontramusic.ui.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mateusrodcosta.apps.lontramusic.ui.views.MenuItem

@Composable
fun FloatingToolbar(items: List<MenuItem.Button>) {
    ElevatedCard(
        modifier = Modifier.padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors =
            CardColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                disabledContainerColor = Color.Unspecified,
                disabledContentColor = Color.Unspecified,
            ),
        elevation = CardDefaults.elevatedCardElevation(6.dp, 6.dp, 6.dp, 6.dp, 6.dp, 6.dp),
    ) {
        FlowRow {
            items.forEach { item ->
                IconButton(onClick = item.onClick, modifier = Modifier.size(48.dp)) {
                    Icon(item.icon, item.text)
                }
            }
        }
    }
}

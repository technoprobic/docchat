package com.personal.healthy1.presentation.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.personal.healthy1.data.local.UserRecord
import java.util.Date

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserRecordItem(
    record: UserRecord,
    isSelected: Boolean,
    onSingleClick: () -> Unit,
    onLongClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(record.title) },
        supportingContent = {
            Text(
                text = "${Date(record.statusTime)}",
                color = Color.Gray,
            )
        },
        leadingContent = {
            if (isSelected) Icon(Icons.Default.Check, "Selected")
        },
        modifier = Modifier
            .clickable(onClick = onSingleClick)
            .combinedClickable(
                onClick = onSingleClick,
                onLongClick = onLongClick
            )
    )
}
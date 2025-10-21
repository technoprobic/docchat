package com.personal.healthy1.presentation.chat

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun ChatInputBar(onSend: (String) -> Unit,
                 isLoading: Boolean,
                 ingestDocsFailure: Boolean) {
    var input by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = input,
            onValueChange = { input = it },
            placeholder = { Text("Ask about these documents") },
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = {
                if (input.isNotBlank()) {
                    onSend(input)
                    input = ""
                }
            },
            enabled = input.isNotBlank() && !isLoading && !ingestDocsFailure
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, "Send")
        }
    }
}
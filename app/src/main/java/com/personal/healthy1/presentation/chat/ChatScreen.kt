package com.personal.healthy1.presentation.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    recordIds: List<String>,
    onNavigateUp: () -> Unit
) {
    val TAG = "ChatScreen"

    val viewModel: ChatViewModel = viewModel()
    val records by viewModel.getRecords(recordIds).collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.ingestRecords(recordIds)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Document Chat (${records.size})")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = {
            ChatInputBar(
                onSend = { message ->
                    viewModel.sendMessage(message)
                },
                uiState.isLoading,
                uiState.ingestDocsFailure
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyColumn {
                items(records) { record ->
                    DocumentPreviewItem(record)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(uiState.chatMessageMirror) { chatItem ->
                    ChatItem(chatItem)
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(96.dp)
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    color = Color.Blue,
                    strokeWidth = 8.dp,
                    trackColor = Color.LightGray,
                    strokeCap = StrokeCap.Round,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

        }
    }
}
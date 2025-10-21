package com.personal.healthy1.presentation.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToChat: (List<String>) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    val viewModel = HomeViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Documents") },
                actions = {
                    if (uiState.selectedIds.isNotEmpty()) {
                        IconButton(onClick = { onNavigateToChat(uiState.selectedIds) }) {
                            Icon(Icons.AutoMirrored.Filled.Send, "Chat with selected")
                            Text("Chat with selected document(s)")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, "Add Record")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding)
        ) {
            items(uiState.records) { record ->
                UserRecordItem(
                    record = record,
                    isSelected = uiState.selectedIds.contains(record.id),
                    onSingleClick = { onNavigateToChat(listOf(record.id)) },
                    onLongClick = { viewModel.toggleSelection(record.id) }
                )
            }
        }
    }
}
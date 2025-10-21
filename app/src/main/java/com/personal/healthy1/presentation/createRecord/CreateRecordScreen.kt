package com.personal.healthy1.presentation.createRecord

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personal.healthy1.presentation.createRecord.components.CreateDocumentSourceOptions
import com.personal.healthy1.presentation.createRecord.components.FileChooserSection
import com.personal.healthy1.presentation.createRecord.components.ImageTextSection
import com.personal.healthy1.presentation.createRecord.components.PasteTextSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDocumentScreen(
    onDocumentCreated: (List<String>) -> Unit
) {
    val viewModel: CreateDocumentViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Add Doc") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            CreateDocumentSourceOptions(
                selectedSource = uiState.sourceType,
                onSourceSelected = viewModel::setSourceType
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (uiState.sourceType) {
                0 -> PasteTextSection(
                    text = uiState.pastedText,
                    onTextChanged = viewModel::setPastedText
                )
                1 -> FileChooserSection(
                    filePath = uiState.filePath,
                    onFileSelected = viewModel::setFilePath
                )
                2 -> ImageTextSection(
                    extractedText = uiState.extractedText,
                    onPhotoSelected = viewModel::processPhoto,
                    onImageSelected = viewModel::processImageFile
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.saveDocument()
                    viewModel.record?.let { record ->
                        onDocumentCreated(listOf(record.id))
                    }
                },
                enabled = uiState.isSaveEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Chat With This Document")
            }
        }
    }
}
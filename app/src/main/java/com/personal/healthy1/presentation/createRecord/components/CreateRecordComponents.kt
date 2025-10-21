package com.personal.healthy1.presentation.createRecord.components

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File
import java.io.FileOutputStream

@Composable
fun CreateDocumentSourceOptions(
    selectedSource: Int,
    onSourceSelected: (Int) -> Unit
) {
    Column {
        RadioButtonRow(
            text = "Paste Text",
            selected = selectedSource == 0,
            onSelect = { onSourceSelected(0) }
        )
        /*RadioButtonRow(
            text = "File Chooser",
            selected = selectedSource == 1,
            onSelect = { onSourceSelected(1) }
        )*/
        RadioButtonRow(
            text = "Extract Text from Image",
            selected = selectedSource == 2,
            onSelect = { onSourceSelected(2) }
        )
    }
}

@Composable
fun RadioButtonRow(
    text: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect
        )
        Text(text, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun PasteTextSection(
    text: String,
    onTextChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChanged,
        label = { Text("Paste your document text") },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 10
    )
}

@Composable
fun FileChooserSection(
    filePath: String?,
    onFileSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val path = getFilePathFromUri(context, uri) ?: ""
                onFileSelected(path)
            }
        }
    )

    Column {
        Text("Select PDF file")
        Button(
            onClick = { launcher.launch("application/pdf") },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Choose File")
        }
        if (filePath != null) {
            Text("Selected: ${filePath.split("/").last()}")
        }
    }
}

@Composable
fun ImageTextSection(
    extractedText: String?,
    onPhotoSelected: (Bitmap) -> Unit,
    onImageSelected: (Uri, Context) -> Unit,
) {
    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                onImageSelected(uri, context)
            }
        }
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            if (bitmap != null) {
                onPhotoSelected(bitmap)
            }
        }
    )

    Column {
        Text("Take photo or select image")
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { galleryLauncher.launch("image/*") }) {
                Text("Image Gallery")
            }
            Button(onClick = { cameraLauncher.launch(null) }) {
                Text("Camera")
            }
        }
        if (extractedText != null) {
            OutlinedTextField(
                value = extractedText,
                onValueChange = {},
                label = { Text("Extracted Text") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                maxLines = 10
            )
        }
    }
}

fun getFilePathFromUri(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = getFileName(context, uri) ?: "temp_file"

        // Create temporary file in app's cache directory
        val tempFile = File(context.cacheDir, fileName)
        FileOutputStream(tempFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        tempFile.absolutePath
    } catch (e: Exception) {
        null
    }
}

private fun getFileName(context: Context, uri: Uri): String? {
    return if (uri.scheme == "content") {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                } else {
                    null
                }
            }
    } else {
        uri.lastPathSegment
    }
}
package com.personal.healthy1.presentation.createRecord

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.personal.healthy1.data.RecordRepository
import com.personal.healthy1.data.local.UserRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.IOException

class CreateDocumentViewModel(
    private val repository: RecordRepository = RecordRepository
) : ViewModel() {
    private val TAG = this.javaClass.simpleName

    private val _uiState = MutableStateFlow(CreateRecordUiState())
    val uiState: StateFlow<CreateRecordUiState> = _uiState.asStateFlow()
    var record: UserRecord? = null

    fun setSourceType(type: Int) {
        _uiState.update { it.copy(sourceType = type) }
    }

    fun setPastedText(text: String) {
        _uiState.update { it.copy(pastedText = text) }
    }

    fun setFilePath(path: String) {
        _uiState.update { it.copy(filePath = path) }
    }

    fun processPhoto(bitmap: Bitmap) {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image)
                .addOnSuccessListener { extractedText ->
                    _uiState.update { it.copy(extractedText = extractedText.text) }
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "processImage failure. e: ${e.message}")
                    // todo
                }
        } catch (e: Exception) {
            e.printStackTrace()
            // todo
        }
    }

    fun processImageFile(uri: Uri, context: Context) {
        val image: InputImage
        try {
            image = InputImage.fromFilePath(context, uri)

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image)
                .addOnSuccessListener { extractedText ->
                    _uiState.update { it.copy(extractedText = extractedText.text) }
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "processImage extracted text failure. e: ${e.message}")
                    // todo
                }
        } catch (e: IOException) {
            e.printStackTrace()
            // todo
        }

    }

    fun saveDocument() {
        val state = _uiState.value
        record = when (state.sourceType) {
            0 -> createFromText(state.pastedText)
            1 -> createFromFile(state.filePath!!)
            2 -> createFromImage(state.extractedText!!)
            else -> return
        }
        repository.insertRecord(record!!)
    }

    private fun createFromText(text: String): UserRecord {
        return UserRecord(
            title = "Pasted Record",
            summary = text,
            sourceType = 0,
            creationStatus = 0
        )
    }

    private fun createFromFile(path: String): UserRecord {
        return UserRecord(
            title = path.split("/").last(),
            summary = "File content summary...",
            sourceType = 1,
            filePath = path,
            creationStatus = 0
        )
    }

    private fun createFromImage(text: String): UserRecord {
        return UserRecord(
            title = "Image Record",
            summary = text,
            sourceType = 2,
            creationStatus = 0
        )
    }
}
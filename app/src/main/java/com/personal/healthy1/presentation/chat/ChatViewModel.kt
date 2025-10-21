package com.personal.healthy1.presentation.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personal.healthy1.BuildConfig
import com.personal.healthy1.data.RecordRepository
import com.personal.healthy1.data.local.UserRecord
import com.personal.healthy1.services.llm.ChatMemoryMirror
import com.personal.healthy1.services.llm.LlmUtils
import dev.langchain4j.data.message.ChatMessageType
import dev.langchain4j.store.embedding.IngestionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class ChatViewModel(
    private val repository: RecordRepository = RecordRepository
) : ViewModel() {

    private val TAG = this.javaClass.simpleName

    private var recordIds: List<String> = emptyList()
    private val llm = LlmUtils
    private var ingestionResults: MutableList<IngestionResult>? = emptyList<IngestionResult>().toMutableList()

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()


    fun getRecords(ids: List<String>): Flow<List<UserRecord>> {
        recordIds = ids
        return repository.getRecordsByIds(ids)
    }

    fun ingestRecords(ids: List<String>) {
        llm.initializeChat()

        // temporary while vector db is temporal and metadata isn't used in embedding
        try {
            ids.forEach {
                repository.getRecordById(it)?.let { currRecord ->
                    currRecord.creationStatus = 0
                    repository.updateRecord(currRecord)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            ids.forEach {
                repository.getRecordById(it)?.let { currRecord ->
                    if (currRecord.creationStatus != 1 && currRecord.creationStatus != 2) {
                        viewModelScope.launch(Dispatchers.IO) {
                            currRecord.creationStatus = 1
                            currRecord.statusTime = System.currentTimeMillis()
                            repository.updateRecord(currRecord)
                            _uiState.update { it.copy(isLoading = true) }

                            var ingestionResult: IngestionResult? = null
                            when (currRecord.sourceType) {
                                0 -> ingestionResult = llm.ingestDocumentText(currRecord.summary)
                                1 -> ingestionResult =
                                    llm.ingestDocumentPdf(File(currRecord.filePath!!))

                                2 -> ingestionResult = llm.ingestDocumentText(currRecord.summary)
                                else -> {}
                            }

                            _uiState.update { it.copy(isLoading = false) }

                            ingestionResult?.let {
                                ingestionResults?.add(ingestionResult)
                            }

                            currRecord.creationStatus = 2
                            currRecord.statusTime = System.currentTimeMillis()
                            repository.updateRecord(currRecord)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.update { it.copy(
                isLoading = false,
                ingestDocsFailure = true,
                chatMessageMirror = mutableListOf(
                    ChatMemoryMirror(
                        content = "Unable to ingest documents."
                    )
                )
            ) }
        }
    }

    fun sendMessage(userMessage: String) {
        Log.d("Chat", "Sending: $userMessage to records")
        try {
            viewModelScope.launch(Dispatchers.IO) {
                var chatMessageMirror = _uiState.value.chatMessageMirror.plus(
                    ChatMemoryMirror(
                        role = ChatMessageType.USER,
                        content = userMessage
                    )
                ).toMutableList()
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        chatMessageMirror = chatMessageMirror
                    )
                }

                val aiResponse = llm.chatWithIngestedDocument(userMessage)

                if (aiResponse != null) {
                    chatMessageMirror = _uiState.value.chatMessageMirror.plus(
                        ChatMemoryMirror(
                            role = ChatMessageType.AI,
                            content = aiResponse
                        )
                    ).toMutableList()
                } else {
                    chatMessageMirror = _uiState.value.chatMessageMirror.plus(
                        ChatMemoryMirror(
                            content = "Unable to generate a response."
                        )
                    ).toMutableList()
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        chatMessageMirror = chatMessageMirror
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // todo
        }
    }
}
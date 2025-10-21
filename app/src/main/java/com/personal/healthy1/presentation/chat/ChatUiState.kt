package com.personal.healthy1.presentation.chat

import com.personal.healthy1.services.llm.ChatMemoryMirror

data class ChatUiState(
    val isLoading: Boolean = false,
    val ingestDocsFailure: Boolean = false,
    val chatMessageMirror: MutableList<ChatMemoryMirror> = mutableListOf(),
)

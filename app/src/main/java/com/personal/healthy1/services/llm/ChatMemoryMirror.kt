package com.personal.healthy1.services.llm

import dev.langchain4j.data.message.ChatMessageType

data class ChatMemoryMirror(
    var role: ChatMessageType? = null,
    var content: String,
    var timestamp: Long = System.currentTimeMillis()
)

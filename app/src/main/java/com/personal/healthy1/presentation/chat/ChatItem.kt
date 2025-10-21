package com.personal.healthy1.presentation.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personal.healthy1.services.llm.ChatMemoryMirror
import dev.langchain4j.data.message.ChatMessageType

@Composable
fun ChatItem(chatItem: ChatMemoryMirror) {

    val alignment = when (chatItem.role) {
        ChatMessageType.USER -> Alignment.End
        ChatMessageType.AI -> Alignment.Start
        else -> Alignment.CenterHorizontally
    }

    val backgroundColor = when (chatItem.role) {
        ChatMessageType.USER -> Color.LightGray
        ChatMessageType.AI -> Color.White
        else -> Color.White
    }

    val textColor = when (chatItem.role) {
        ChatMessageType.SYSTEM -> Color.Red
        else -> Color.Black
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = alignment
    ) {
        Card(
            modifier = Modifier
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Text(
                modifier = Modifier
                    .padding(8.dp),
                text = chatItem.content,
                color = textColor)
        }
    }

}

@Preview(showBackground = false)
@Composable
fun ChatItemPreview() {
    val chatItem = ChatMemoryMirror(
        role = ChatMessageType.USER,
        content = "Was charlie kirk assassinated in 2025?"
    )
    ChatItem(chatItem)
}

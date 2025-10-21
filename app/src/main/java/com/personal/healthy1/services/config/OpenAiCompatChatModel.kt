package org.example.project.services.config

import com.personal.healthy1.remote.OkHttpHttpClientBuilderFactory
import dev.langchain4j.model.openai.OpenAiChatModel


object OpenAiCompatChatModel {
    fun chatModel(): OpenAiChatModel {

        return OpenAiChatModel.builder()
            .apiKey(OpenAiConfig.API_KEY)
            .modelName(OpenAiConfig.MODEL_NAME)
            .httpClientBuilder(OkHttpHttpClientBuilderFactory().create())
            //.logRequests(true)
            //.logResponses(true)
            .temperature(OpenAiConfig.DEFAULT_TEMPERATURE)
            .build()
    }
}
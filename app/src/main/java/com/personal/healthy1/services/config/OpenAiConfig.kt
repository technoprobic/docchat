package org.example.project.services.config

import com.personal.healthy1.BuildConfig

object OpenAiConfig {
    val API_KEY = BuildConfig.API_KEY
    const val MODEL_NAME = "gpt-4o-mini"
    const val DEFAULT_TEMPERATURE = 0.8
}
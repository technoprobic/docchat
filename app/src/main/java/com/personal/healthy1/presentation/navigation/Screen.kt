package com.personal.healthy1.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    data class Chat(val ids: List<String>) : Screen("chat/${ids.joinToString(",")}")
    object CreateDocument : Screen("create_document")
}
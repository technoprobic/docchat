package com.personal.healthy1.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import androidx.navigation.navArgument
import com.personal.healthy1.presentation.chat.ChatScreen
import com.personal.healthy1.presentation.createRecord.CreateDocumentScreen
import com.personal.healthy1.presentation.home.HomeScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    paddingValues: PaddingValues
) {
    NavHost(
        modifier = Modifier.padding(paddingValues),
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToChat = { ids ->
                    navController.navigate(Screen.Chat(ids).route)
                },
                onNavigateToCreate = {
                    navController.navigate(Screen.CreateDocument.route)
                }
            )
        }
        composable(
            route = "chat/{ids}",
            arguments = listOf(navArgument("ids") { type = NavType.StringType })
        ) { backStackEntry ->
            val ids = backStackEntry.arguments?.getString("ids")?.split(",") ?: emptyList()
            ChatScreen(
                recordIds = ids,
                onNavigateUp = { navController.popBackStack() }
            )
        }
        composable(Screen.CreateDocument.route) {
            CreateDocumentScreen(
                onDocumentCreated = { ids ->
                    navController.navigate(Screen.Chat(ids).route)
                }
            )
        }
    }
}
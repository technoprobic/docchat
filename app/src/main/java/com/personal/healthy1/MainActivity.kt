package com.personal.healthy1

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import com.personal.healthy1.presentation.navigation.NavGraph
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.personal.healthy1.presentation.theme.Healthy1Theme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Healthy1Theme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { paddingValue ->
                    NavGraph(
                        navController = navController,
                        paddingValues = paddingValue
                    )
                }
            }
        }

    }
}


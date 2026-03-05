package com.example.ruler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.ruler.ui.screens.*
import com.example.ruler.ui.theme.RulerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RulerTheme {
                var currentScreen by remember { mutableStateOf("splash") }
                var selectedTripId by remember { mutableStateOf(1) }

                when (currentScreen) {
                    "splash" -> SplashScreen(
                        onSplashFinished = { currentScreen = "home" }
                    )
                    "home" -> HomeScreen(
                        onTripClick = { tripId ->
                            selectedTripId = tripId
                            currentScreen = "tripDetail"
                        },
                        onNavigateToGallery = { currentScreen = "gallery" },
                        onNavigateToPreferences = { },
                        onNavigateToAbout = { }
                    )
                    "tripDetail" -> TripDetailScreen(
                        tripId = selectedTripId,
                        onNavigateBack = { currentScreen = "home" },
                        onNavigateToGallery = { currentScreen = "gallery" }
                    )
                }
            }
        }
    }
}
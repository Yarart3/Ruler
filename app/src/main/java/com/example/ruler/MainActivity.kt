package com.example.ruler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.ruler.ui.screens.HomeScreen
import com.example.ruler.ui.screens.SplashScreen
import com.example.ruler.ui.theme.RulerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RulerTheme {
                // variable que controla quina pantalla es veu
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(onSplashFinished = { showSplash = false })
                } else {
                    HomeScreen(
                        onTripClick = { },
                        onNavigateToPreferences = { },
                        onNavigateToAbout = { }
                    )
                }
            }
        }
    }
}

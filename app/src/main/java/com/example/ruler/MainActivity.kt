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
                var selectedActivity by remember { mutableStateOf<Activity?>(null) }
                var selectedTrip by remember { mutableStateOf<Trip?>(null) }

                when (currentScreen) {
                    "newTrip" -> NewTripScreen(
                        onNavigateBack = { currentScreen = "home" },
                        onNavigateToHome = { currentScreen = "home" },
                        onNavigateToGallery = { currentScreen = "gallery" },
                        onNavigateToProfile = { currentScreen = "profile" },
                        onNavigateToPreferences = { currentScreen = "preferences" },
                        onNavigateToAbout = { currentScreen = "about" },
                        onNavigateToTrips = { currentScreen = "tripDetail" }
                    )
                    "splash" -> SplashScreen(
                        onSplashFinished = { currentScreen = "home" }
                    )
                    "home" -> HomeScreen(
                        onTripClick = { tripId ->
                            selectedTripId = tripId
                            currentScreen = "tripDetail"
                        },
                        onNavigateToGallery = { currentScreen = "gallery" },
                        onNavigateToPreferences = { currentScreen = "preferences" },
                        onNavigateToAbout = { currentScreen = "about" },
                        onNavigateToProfile = { currentScreen = "profile" },
                        onNavigateToTripOptions = { trip ->
                            selectedTrip = trip
                            currentScreen = "tripOptions"
                        },
                        onNavigateToNewTrip = { currentScreen = "newTrip" }
                    )
                    "tripOptions" -> {
                        selectedTrip?.let { trip ->
                            TripOptionsScreen(
                                trip = trip,
                                onNavigateBack = { currentScreen = "home" },
                                onNavigateToHome = { currentScreen = "home" },
                                onNavigateToGallery = { currentScreen = "gallery" },
                                onNavigateToProfile = { currentScreen = "profile" },
                                onNavigateToPreferences = { currentScreen = "preferences" },
                                onNavigateToAbout = { currentScreen = "about" }
                            )
                        }
                    }
                    "tripDetail" -> TripDetailScreen(
                        tripId = selectedTripId,
                        onNavigateBack = { currentScreen = "home" },
                        onNavigateToHome = { currentScreen = "home" },
                        onNavigateToGallery = { currentScreen = "gallery" },
                        onNavigateToPreferences = { currentScreen = "preferences" },
                        onNavigateToAbout = { currentScreen = "about" },
                        onNavigateToProfile = { currentScreen = "profile" },
                        onNavigateToActivityDetail = { activity ->
                            selectedActivity = activity
                            currentScreen = "activityDetail"
                        }
                    )
                    "activityDetail" -> {
                        selectedActivity?.let { activity ->
                            ActivityDetailScreen(
                                activity = activity,
                                onNavigateBack = { currentScreen = "tripDetail" },
                                onNavigateToHome = { currentScreen = "home" },
                                onNavigateToGallery = { currentScreen = "gallery" },
                                onNavigateToProfile = { currentScreen = "profile" },
                                onNavigateToPreferences = { currentScreen = "preferences" },
                                onNavigateToAbout = { currentScreen = "about" }
                            )
                        }
                    }
                    "gallery" -> GalleryScreen(
                        onNavigateBack = { currentScreen = "home" },
                        onNavigateToHome = { currentScreen = "home" },
                        onNavigateToTrips = { currentScreen = "tripDetail" },
                        onNavigateToPreferences = { currentScreen = "preferences" },
                        onNavigateToAbout = { currentScreen = "about" },
                        onNavigateToProfile = { currentScreen = "profile" }
                    )
                    "preferences" -> PreferencesScreen(
                        onNavigateBack = { currentScreen = "home" },
                        onNavigateToHome = { currentScreen = "home" },
                        onNavigateToAbout = { currentScreen = "about" },
                        onNavigateToTrips = { currentScreen = "tripDetail" },
                        onNavigateToGallery = { currentScreen = "gallery" },
                        onNavigateToProfile = { currentScreen = "profile" }
                    )
                    "about" -> AboutScreen(
                        onNavigateBack = { currentScreen = "home" },
                        onNavigateToHome = { currentScreen = "home" },
                        onNavigateToTerms = { currentScreen = "terms" },
                        onNavigateToPreferences = { currentScreen = "preferences" },
                        onNavigateToTrips = { currentScreen = "tripDetail" },
                        onNavigateToGallery = { currentScreen = "gallery" },
                        onNavigateToProfile = { currentScreen = "profile" }
                    )
                    "terms" -> TermsScreen(
                        onNavigateBack = { currentScreen = "about" },
                        onNavigateToHome = { currentScreen = "home" }
                    )
                    "profile" -> ProfileScreen(
                        onNavigateBack = { currentScreen = "home" },
                        onNavigateToHome = { currentScreen = "home" },
                        onNavigateToPreferences = { currentScreen = "preferences" },
                        onNavigateToAbout = { currentScreen = "about" },
                        onNavigateToTrips = { currentScreen = "tripDetail" },
                        onNavigateToGallery = { currentScreen = "gallery" }
                    )
                }
            }
        }
    }
}
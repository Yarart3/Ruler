package com.example.ruler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.example.ruler.domain.Trip
import com.example.ruler.domain.TripActivity
import com.example.ruler.ui.screens.*
import com.example.ruler.ui.theme.RulerTheme
import com.example.ruler.ui.viewmodels.TripListViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TripListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RulerTheme {
                var currentScreen by remember { mutableStateOf("splash") }
                var selectedTripId by remember { mutableStateOf("1") }
                var selectedTripIdForEdit by remember { mutableStateOf("1") }
                var selectedActivity by remember { mutableStateOf<TripActivity?>(null) }
                var selectedTrip by remember { mutableStateOf<Trip?>(null) }

                when (currentScreen) {
                    "newTrip" -> NewTripScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = "home" },
                        onNavigateToHome = { currentScreen = "home" },
                        onNavigateToGallery = { currentScreen = "gallery" },
                        onNavigateToProfile = { currentScreen = "profile" },
                        onNavigateToPreferences = { currentScreen = "preferences" },
                        onNavigateToAbout = { currentScreen = "about" },
                        onNavigateToTrips = { currentScreen = "tripDetail" }
                    )
                    "editTrip" -> EditTripScreen(
                        viewModel = viewModel,
                        tripId = selectedTripIdForEdit,
                        onNavigateBack = { currentScreen = "tripDetail" },
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
                        viewModel = viewModel,
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
                                viewModel = viewModel,
                                trip = trip,
                                onNavigateBack = { currentScreen = "home" },
                                onNavigateToHome = { currentScreen = "home" },
                                onNavigateToGallery = { currentScreen = "gallery" },
                                onNavigateToProfile = { currentScreen = "profile" },
                                onNavigateToPreferences = { currentScreen = "preferences" },
                                onNavigateToAbout = { currentScreen = "about" },
                                onNavigateToEdit = { tripId ->
                                    selectedTripIdForEdit = tripId
                                    currentScreen = "editTrip"
                                },
                                onNavigateToNewTrip = { currentScreen = "newTrip" }
                            )
                        }
                    }
                    "tripDetail" -> TripDetailScreen(
                        viewModel = viewModel,
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
                        },
                        onNavigateToNewTrip = { currentScreen = "newTrip" }
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
                                onNavigateToAbout = { currentScreen = "about" },
                                onNavigateToNewTrip = { currentScreen = "newTrip" }
                            )
                        }
                    }
                    "gallery" -> GalleryScreen(
                        onNavigateBack = { currentScreen = "home" },
                        onNavigateToHome = { currentScreen = "home" },
                        onNavigateToTrips = { currentScreen = "tripDetail" },
                        onNavigateToPreferences = { currentScreen = "preferences" },
                        onNavigateToAbout = { currentScreen = "about" },
                        onNavigateToProfile = { currentScreen = "profile" },
                        onNavigateToNewTrip = { currentScreen = "newTrip" }
                    )
                    "preferences" -> PreferencesScreen(
                        onNavigateBack = { currentScreen = "home" },
                        onNavigateToHome = { currentScreen = "home" },
                        onNavigateToAbout = { currentScreen = "about" },
                        onNavigateToTrips = { currentScreen = "tripDetail" },
                        onNavigateToGallery = { currentScreen = "gallery" },
                        onNavigateToProfile = { currentScreen = "profile" },
                        onNavigateToNewTrip = { currentScreen = "newTrip" }
                    )
                    "about" -> AboutScreen(
                        onNavigateBack = { currentScreen = "home" },
                        onNavigateToHome = { currentScreen = "home" },
                        onNavigateToTerms = { currentScreen = "terms" },
                        onNavigateToPreferences = { currentScreen = "preferences" },
                        onNavigateToTrips = { currentScreen = "tripDetail" },
                        onNavigateToGallery = { currentScreen = "gallery" },
                        onNavigateToProfile = { currentScreen = "profile" },
                        onNavigateToNewTrip = { currentScreen = "newTrip" }
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
                        onNavigateToGallery = { currentScreen = "gallery" },
                        onNavigateToNewTrip = { currentScreen = "newTrip" }
                    )
                }
            }
        }
    }
}

package com.example.ruler

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.ruler.domain.Trip
import com.example.ruler.domain.TripActivity
import com.example.ruler.ui.screens.AboutScreen
import com.example.ruler.ui.screens.ActivityDetailScreen
import com.example.ruler.ui.screens.AddActivityScreen
import com.example.ruler.ui.screens.EditActivityScreen
import com.example.ruler.ui.screens.EditTripScreen
import com.example.ruler.ui.screens.ForgotPasswordScreen
import com.example.ruler.ui.screens.GalleryScreen
import com.example.ruler.ui.screens.HomeScreen
import com.example.ruler.ui.screens.LoginScreen
import com.example.ruler.ui.screens.NewTripScreen
import com.example.ruler.ui.screens.PreferencesScreen
import com.example.ruler.ui.screens.ProfileScreen
import com.example.ruler.ui.screens.RegisterScreen
import com.example.ruler.ui.screens.SplashScreen
import com.example.ruler.ui.screens.TermsScreen
import com.example.ruler.ui.screens.TripDetailScreen
import com.example.ruler.ui.screens.TripOptionsScreen
import com.example.ruler.ui.theme.RulerTheme
import com.example.ruler.ui.viewmodels.AuthSessionState
import com.example.ruler.ui.viewmodels.AuthViewModel
import com.example.ruler.ui.viewmodels.TripListViewModel
import com.example.ruler.ui.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val viewModel: TripListViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleHelper.getSavedLanguage(newBase)
        super.attachBaseContext(LocaleHelper.applyLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("ruler_prefs", Context.MODE_PRIVATE)

        setContent {
            var darkMode by remember { mutableStateOf(prefs.getBoolean("dark_mode", false)) }
            val authState by authViewModel.uiState.collectAsState()
            val trips by viewModel.trips.collectAsState()

            RulerTheme(darkTheme = darkMode) {
                var currentScreen by remember { mutableStateOf("splash") }
                var selectedTripId by remember { mutableStateOf("1") }
                var selectedTripIdForEdit by remember { mutableStateOf("1") }
                var selectedActivity by remember { mutableStateOf<TripActivity?>(null) }
                var selectedTrip by remember { mutableStateOf<Trip?>(null) }
                var activityToEdit by remember { mutableStateOf<TripActivity?>(null) }

                LaunchedEffect(authState.sessionState, authState.successMessage) {
                    if (authState.sessionState == AuthSessionState.Authenticated) {
                        userViewModel.syncCurrentUser()
                        viewModel.refreshSessionData()
                        currentScreen = "home"
                    } else if (authState.successMessage != null && currentScreen == "register") {
                        currentScreen = "login"
                    } else if (
                        authState.sessionState == AuthSessionState.RequiresLogin ||
                        authState.sessionState == AuthSessionState.FirebaseNotConfigured
                    ) {
                        userViewModel.clearProfile()
                        viewModel.clearSessionData()
                    }
                }

                if (currentScreen == "splash") {
                    SplashScreen(
                        onSplashFinished = {
                            currentScreen = when (authState.sessionState) {
                                AuthSessionState.Authenticated -> "home"
                                AuthSessionState.RequiresLogin,
                                AuthSessionState.FirebaseNotConfigured,
                                AuthSessionState.Checking -> "login"
                            }
                        }
                    )
                } else if (currentScreen == "register") {
                    RegisterScreen(
                        onRegisterClick = { username, email, password ->
                            authViewModel.register(username, email, password)
                        },
                        onNavigateToLogin = {
                            authViewModel.clearMessages()
                            currentScreen = "login"
                        },
                        isLoading = authState.isLoading,
                        errorMessage = authState.errorMessage ?: if (
                            authState.sessionState == AuthSessionState.FirebaseNotConfigured
                        ) {
                            stringResource(R.string.firebase_setup_required)
                        } else {
                            null
                        },
                        successMessage = authState.successMessage
                    )
                } else if (currentScreen == "forgotPassword") {
                    ForgotPasswordScreen(
                        onNavigateToLogin = {
                            authViewModel.clearResetState()
                            currentScreen = "login"
                        },
                        onSendClick = { email ->
                            authViewModel.sendPasswordResetEmail(email)
                        },
                        isLoading = authState.isLoading,
                        errorMessage = authState.resetErrorMessage,
                        emailSent = authState.resetEmailSent
                    )
                } else if (
                    currentScreen == "login" ||
                    authState.sessionState == AuthSessionState.RequiresLogin ||
                    authState.sessionState == AuthSessionState.FirebaseNotConfigured
                ) {
                    LoginScreen(
                        onLoginClick = authViewModel::signIn,
                        onNavigateToRegister = {
                            authViewModel.clearMessages()
                            currentScreen = "register"
                        },
                        onNavigateToForgotPassword = { currentScreen = "forgotPassword" },
                        isLoading = authState.isLoading,
                        errorMessage = authState.errorMessage ?: if (
                            authState.sessionState == AuthSessionState.FirebaseNotConfigured
                        ) {
                            stringResource(R.string.firebase_setup_required)
                        } else {
                            null
                        },
                        successMessage = authState.successMessage
                    )
                } else {
                    when (currentScreen) {
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
                        "tripOptions" -> selectedTrip?.let { trip ->
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
                            onNavigateToNewTrip = { currentScreen = "newTrip" },
                            onNavigateToAddActivity = { currentScreen = "addActivity" },
                            onNavigateToEditActivity = { activity ->
                                activityToEdit = activity
                                currentScreen = "editActivity"
                            }
                        )
                        "addActivity" -> AddActivityScreen(
                            viewModel = viewModel,
                            tripId = selectedTripId,
                            onNavigateBack = { currentScreen = "tripDetail" },
                            onNavigateToHome = { currentScreen = "home" },
                            onNavigateToGallery = { currentScreen = "gallery" },
                            onNavigateToProfile = { currentScreen = "profile" },
                            onNavigateToPreferences = { currentScreen = "preferences" },
                            onNavigateToAbout = { currentScreen = "about" },
                            onNavigateToNewTrip = { currentScreen = "newTrip" }
                        )
                        "editActivity" -> activityToEdit?.let { activity ->
                            EditActivityScreen(
                                viewModel = viewModel,
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
                        "activityDetail" -> selectedActivity?.let { activity ->
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
                            userViewModel = userViewModel,
                            onNavigateBack = { currentScreen = "profile" },
                            onNavigateToHome = { currentScreen = "home" },
                            onNavigateToAbout = { currentScreen = "about" },
                            onNavigateToTrips = { currentScreen = "tripDetail" },
                            onNavigateToGallery = { currentScreen = "gallery" },
                            onNavigateToProfile = { currentScreen = "profile" },
                            onNavigateToNewTrip = { currentScreen = "newTrip" },
                            onDarkModeChange = { isDark -> darkMode = isDark },
                            onLanguageChange = { recreate() }
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
                            userViewModel = userViewModel,
                            tripCount = trips.size,
                            onNavigateBack = { currentScreen = "home" },
                            onNavigateToHome = { currentScreen = "home" },
                            onNavigateToPreferences = { currentScreen = "preferences" },
                            onNavigateToAbout = { currentScreen = "about" },
                            onNavigateToTrips = { currentScreen = "tripDetail" },
                            onNavigateToGallery = { currentScreen = "gallery" },
                            onNavigateToNewTrip = { currentScreen = "newTrip" },
                            onLogout = { authViewModel.signOut() }
                        )
                        else -> HomeScreen(
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
                    }
                }
            }
        }
    }
}

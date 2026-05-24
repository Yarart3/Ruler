package com.example.ruler

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.ruler.domain.Hotel
import com.example.ruler.domain.HotelRoom
import com.example.ruler.domain.Trip
import com.example.ruler.domain.TripActivity
import com.example.ruler.domain.hasRequiredLocalData
import com.example.ruler.ui.screens.AboutScreen
import com.example.ruler.ui.screens.ActivityDetailScreen
import com.example.ruler.ui.screens.AddActivityScreen
import com.example.ruler.ui.screens.EditActivityScreen
import com.example.ruler.ui.screens.EditTripScreen
import com.example.ruler.ui.screens.ForgotPasswordScreen
import com.example.ruler.ui.screens.GalleryScreen
import com.example.ruler.ui.screens.HotelBookingScreen
import com.example.ruler.ui.screens.HomeScreen
import com.example.ruler.ui.screens.ReservationsScreen
import com.example.ruler.ui.screens.HotelSearchScreen
import com.example.ruler.ui.screens.HotelsScreen
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
import com.example.ruler.ui.viewmodels.HotelViewModel
import com.example.ruler.ui.viewmodels.LocalHotelViewModel
import com.example.ruler.ui.viewmodels.TripImageViewModel
import com.example.ruler.ui.viewmodels.TripListViewModel
import com.example.ruler.ui.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

private fun apiToDisplayDate(dateStr: String): String = runCatching {
    java.time.LocalDate.parse(dateStr, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}.getOrElse { dateStr }

private fun calcNights(startDate: String, endDate: String): Int = runCatching {
    val start = java.time.LocalDate.parse(startDate, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
    val end = java.time.LocalDate.parse(endDate, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
    java.time.temporal.ChronoUnit.DAYS.between(start, end).toInt().coerceAtLeast(0)
}.getOrElse { 0 }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val viewModel: TripListViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val hotelViewModel: HotelViewModel by viewModels()
    private val localHotelViewModel: LocalHotelViewModel by viewModels()
    private val tripImageViewModel: TripImageViewModel by viewModels()

    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleHelper.getSavedLanguage(newBase)
        super.attachBaseContext(LocaleHelper.applyLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialPreferences = AppPreferences.getActivePreferences(this)

        setContent {
            var darkMode by remember { mutableStateOf(initialPreferences.darkMode) }
            val authState by authViewModel.uiState.collectAsState()
            val trips by viewModel.trips.collectAsState()
            val userProfile by userViewModel.userProfile.collectAsState()
            val localHotels by localHotelViewModel.hotels.collectAsState()
            val localHotelUiState by localHotelViewModel.uiState.collectAsState()

            RulerTheme(darkTheme = darkMode) {
                var currentScreen by remember { mutableStateOf("splash") }
                val backStack = remember { mutableStateListOf<String>() }
                var selectedTripId by remember { mutableStateOf("1") }
                var selectedTripIdForEdit by remember { mutableStateOf("1") }
                var selectedActivity by remember { mutableStateOf<TripActivity?>(null) }
                var selectedTrip by remember { mutableStateOf<Trip?>(null) }
                var activityToEdit by remember { mutableStateOf<TripActivity?>(null) }
                var selectedHotel by remember { mutableStateOf<Hotel?>(null) }
                var selectedHotelRoom by remember { mutableStateOf<HotelRoom?>(null) }
                var selectedHotelStartDate by remember { mutableStateOf("") }
                var selectedHotelEndDate by remember { mutableStateOf("") }
                var hotelSearchSourceTripId by remember { mutableStateOf<String?>(null) }

                fun resetNavigation(screen: String) {
                    backStack.clear()
                    currentScreen = screen
                }

                fun navigateTo(screen: String, addCurrentToBackStack: Boolean = true) {
                    if (currentScreen == screen) return
                    if (addCurrentToBackStack && currentScreen != "splash") {
                        backStack.add(currentScreen)
                    }
                    currentScreen = screen
                }

                fun navigateToTripsIfAvailable(addCurrentToBackStack: Boolean = true) {
                    val firstTrip = trips.firstOrNull() ?: return
                    if (trips.none { it.id == selectedTripId }) {
                        selectedTripId = firstTrip.id
                    }
                    navigateTo("tripDetail", addCurrentToBackStack = addCurrentToBackStack)
                }

                fun goBack(fallback: String = "home") {
                    while (backStack.isNotEmpty()) {
                        val previousScreen = backStack.removeAt(backStack.lastIndex)
                        if (previousScreen == "tripDetail" && trips.isEmpty()) {
                            continue
                        }
                        if (previousScreen == "tripOptions" && selectedTrip == null) {
                            continue
                        }
                        if (previousScreen == "activityDetail" && selectedActivity == null) {
                            continue
                        }
                        if (previousScreen == "editActivity" && activityToEdit == null) {
                            continue
                        }
                        if ((previousScreen == "editTrip" || previousScreen == "addActivity") && trips.isEmpty()) {
                            continue
                        }
                        run {
                            currentScreen = previousScreen
                            return
                        }
                    }
                    currentScreen = fallback
                }

                LaunchedEffect(authState.sessionState, authState.successMessage) {
                    if (authState.sessionState == AuthSessionState.Authenticated) {
                        userViewModel.syncCurrentUser()
                        viewModel.refreshSessionData()
                    } else if (authState.successMessage != null && currentScreen == "register") {
                        resetNavigation("login")
                    } else if (
                        authState.sessionState == AuthSessionState.RequiresLogin ||
                        authState.sessionState == AuthSessionState.FirebaseNotConfigured
                    ) {
                        val currentLanguage = AppPreferences.getActivePreferences(this@MainActivity).language
                        AppPreferences.resetActivePreferences(this@MainActivity)
                        darkMode = AppPreferences.DEFAULT_DARK_MODE
                        userViewModel.clearProfile()
                        viewModel.clearSessionData()
                        resetNavigation("login")
                        if (currentLanguage != AppPreferences.DEFAULT_LANGUAGE) {
                            recreate()
                        }
                    }
                }

                LaunchedEffect(
                    authState.sessionState,
                    userProfile?.id,
                    userProfile?.username,
                    userProfile?.birthDate,
                    userProfile?.address,
                    userProfile?.country,
                    userProfile?.phone
                ) {
                    if (authState.sessionState != AuthSessionState.Authenticated) return@LaunchedEffect
                    val profile = userProfile ?: return@LaunchedEffect
                    val previousLanguage = AppPreferences.getActivePreferences(this@MainActivity).language
                    val activePreferences = AppPreferences.activateUserPreferences(
                        context = this@MainActivity,
                        userId = profile.id
                    )
                    darkMode = activePreferences.darkMode
                    if (previousLanguage != activePreferences.language) {
                        recreate()
                        return@LaunchedEffect
                    }
                    resetNavigation(if (profile.hasRequiredLocalData()) "home" else "preferences")
                }

                if (currentScreen == "splash") {
                    SplashScreen(
                        onSplashFinished = {
                            resetNavigation(
                                when (authState.sessionState) {
                                AuthSessionState.Authenticated -> "home"
                                AuthSessionState.RequiresLogin,
                                AuthSessionState.FirebaseNotConfigured,
                                AuthSessionState.Checking -> "login"
                                }
                            )
                        }
                    )
                } else if (currentScreen == "register") {
                    RegisterScreen(
                        onRegisterClick = { username, email, password ->
                            authViewModel.register(username, email, password)
                        },
                        onNavigateToLogin = {
                            authViewModel.clearMessages()
                            resetNavigation("login")
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
                            goBack("login")
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
                            navigateTo("register", addCurrentToBackStack = false)
                        },
                        onNavigateToForgotPassword = { navigateTo("forgotPassword", addCurrentToBackStack = false) },
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
                                navigateTo("tripDetail")
                            },
                            onNavigateToHotels = { navigateTo("hotels") },
                            onNavigateToGallery = { navigateTo("gallery") },
                            onNavigateToPreferences = { navigateTo("preferences") },
                            onNavigateToAbout = { navigateTo("about") },
                            onNavigateToProfile = { navigateTo("profile") },
                            onNavigateToTripOptions = { trip ->
                                selectedTrip = trip
                                navigateTo("tripOptions")
                            },
                            onNavigateToNewTrip = { navigateTo("newTrip") }
                        )
                        "hotels" -> HotelsScreen(
                            viewModel = localHotelViewModel,
                            trips = trips,
                            onBrowseHotels = { navigateTo("hotelSearch") },
                            onAssignHotelToTrip = { hotelId, hotelName, hotelAddress, tripId, checkIn, checkOut ->
                                viewModel.assignHotelToTrip(tripId, hotelId, hotelName, hotelAddress, checkIn, checkOut)
                            },
                            onNavigateToHome = { resetNavigation("home") },
                            onNavigateToGallery = { navigateTo("gallery") },
                            onNavigateToProfile = { navigateTo("profile") },
                            onNavigateToPreferences = { navigateTo("preferences") },
                            onNavigateToAbout = { navigateTo("about") },
                            onNavigateToReservations = { navigateTo("reservations") }
                        )
                        "reservations" -> ReservationsScreen(
                            hotels = localHotels,
                            trips = trips,
                            deletingReservationId = localHotelUiState.deletingHotelId,
                            errorMessage = localHotelUiState.errorMessage,
                            successMessage = localHotelUiState.successMessage,
                            onDeleteReservation = localHotelViewModel::deleteHotel,
                            onClearMessages = localHotelViewModel::clearMessages,
                            onNavigateBack = { goBack("hotels") },
                            onNavigateToHome = { resetNavigation("home") },
                            onNavigateToHotels = { navigateTo("hotels") },
                            onNavigateToGallery = { navigateTo("gallery") },
                            onNavigateToProfile = { navigateTo("profile") },
                            onNavigateToPreferences = { navigateTo("preferences") },
                            onNavigateToAbout = { navigateTo("about") },
                            onNavigateToNewTrip = { navigateTo("newTrip") }
                        )
                        "hotelSearch" -> HotelSearchScreen(
                            hotelViewModel = hotelViewModel,
                            onRoomSelected = { hotel, room, startDate, endDate ->
                                selectedHotel = hotel
                                selectedHotelRoom = room
                                selectedHotelStartDate = startDate
                                selectedHotelEndDate = endDate
                                navigateTo("hotelBooking")
                            },
                            onNavigateBack = { goBack() },
                            onNavigateToHome = { resetNavigation("home") },
                            onNavigateToHotels = { navigateTo("hotels") },
                            onNavigateToGallery = { navigateTo("gallery") },
                            onNavigateToProfile = { navigateTo("profile") },
                            onNavigateToPreferences = { navigateTo("preferences") },
                            onNavigateToAbout = { navigateTo("about") },
                            onNavigateToNewTrip = { navigateTo("newTrip") }
                        )
                        "hotelBooking" -> {
                            val hotel = selectedHotel
                            val room = selectedHotelRoom
                            if (hotel != null && room != null) {
                                HotelBookingScreen(
                                    hotelViewModel = hotelViewModel,
                                    hotel = hotel,
                                    room = room,
                                    startDate = selectedHotelStartDate,
                                    endDate = selectedHotelEndDate,
                                    defaultGuestName = userProfile?.username.orEmpty(),
                                    defaultGuestEmail = userProfile?.email.orEmpty(),
                                    onNavigateBack = { goBack("hotelSearch") },
                                    onBookingCompleted = { result ->
                                        val hotelId = localHotelViewModel.addHotel(
                                            name = hotel.name,
                                            address = hotel.address,
                                            nights = result.nights,
                                            pricePerNight = room.price,
                                            reservationId = result.reservation.id,
                                            remoteHotelId = result.reservation.hotelId,
                                            remoteRoomId = result.reservation.roomId,
                                            startDate = result.reservation.startDate,
                                            endDate = result.reservation.endDate,
                                            guestName = result.reservation.guestName,
                                            guestEmail = result.reservation.guestEmail,
                                            hotelImageUrl = hotel.imageUrl,
                                            roomImageUrls = room.imageUrls
                                        )
                                        val sourceTripId = hotelSearchSourceTripId
                                        backStack.removeAll { it in setOf("hotelSearch", "hotelBooking") }
                                        if (sourceTripId != null) {
                                            viewModel.assignHotelToTrip(
                                                sourceTripId, hotelId, hotel.name, hotel.address,
                                                apiToDisplayDate(selectedHotelStartDate),
                                                apiToDisplayDate(selectedHotelEndDate)
                                            )
                                            selectedTripId = sourceTripId
                                            hotelSearchSourceTripId = null
                                            currentScreen = "tripDetail"
                                        } else {
                                            currentScreen = "hotels"
                                        }
                                    },
                                    onNavigateToHome = { resetNavigation("home") },
                                    onNavigateToHotels = {
                                        hotelSearchSourceTripId = null
                                        backStack.removeAll { it in setOf("hotelSearch", "hotelBooking") }
                                        navigateTo("hotels")
                                    },
                                    onNavigateToGallery = { navigateTo("gallery") },
                                    onNavigateToProfile = { navigateTo("profile") },
                                    onNavigateToPreferences = { navigateTo("preferences") },
                                    onNavigateToAbout = { navigateTo("about") },
                                    onNavigateToNewTrip = { navigateTo("newTrip") }
                                )
                            } else {
                                LaunchedEffect(Unit) { goBack("hotelSearch") }
                            }
                        }
                        "newTrip" -> NewTripScreen(
                            viewModel = viewModel,
                            onNavigateBack = { goBack() },
                            onNavigateToHome = { resetNavigation("home") },
                            onNavigateToGallery = { navigateTo("gallery") },
                            onNavigateToProfile = { navigateTo("profile") },
                            onNavigateToPreferences = { navigateTo("preferences") },
                            onNavigateToAbout = { navigateTo("about") },
                            onNavigateToHotels = { navigateTo("hotels") }
                        )
                        "editTrip" -> EditTripScreen(
                            viewModel = viewModel,
                            tripId = selectedTripIdForEdit,
                            onNavigateBack = { goBack("tripDetail") },
                            onNavigateToHome = { resetNavigation("home") },
                            onNavigateToGallery = { navigateTo("gallery") },
                            onNavigateToProfile = { navigateTo("profile") },
                            onNavigateToPreferences = { navigateTo("preferences") },
                            onNavigateToAbout = { navigateTo("about") },
                            onNavigateToHotels = { navigateTo("hotels") }
                        )
                        "tripOptions" -> selectedTrip?.let { trip ->
                            TripOptionsScreen(
                                viewModel = viewModel,
                                trip = trip,
                                onNavigateBack = { goBack() },
                                onNavigateToHome = { resetNavigation("home") },
                                onNavigateToHotels = { navigateTo("hotels") },
                                onNavigateToGallery = { navigateTo("gallery") },
                                onNavigateToProfile = { navigateTo("profile") },
                                onNavigateToPreferences = { navigateTo("preferences") },
                                onNavigateToAbout = { navigateTo("about") },
                                onNavigateToEdit = { tripId ->
                                    selectedTripIdForEdit = tripId
                                    navigateTo("editTrip")
                                },
                                onNavigateToNewTrip = { navigateTo("newTrip") }
                            )
                        }
                        "tripDetail" -> TripDetailScreen(
                            viewModel = viewModel,
                            tripImageViewModel = tripImageViewModel,
                            tripId = selectedTripId,
                            localHotels = localHotels,
                            onNavigateBack = { goBack() },
                            onNavigateToHome = { resetNavigation("home") },
                            onNavigateToHotels = { navigateTo("hotels") },
                            onNavigateToGallery = { navigateTo("gallery") },
                            onNavigateToPreferences = { navigateTo("preferences") },
                            onNavigateToAbout = { navigateTo("about") },
                            onNavigateToProfile = { navigateTo("profile") },
                            onNavigateToActivityDetail = { activity ->
                                selectedActivity = activity
                                navigateTo("activityDetail")
                            },
                            onNavigateToNewTrip = { navigateTo("newTrip") },
                            onNavigateToAddActivity = { navigateTo("addActivity") },
                            onNavigateToEditActivity = { activity ->
                                activityToEdit = activity
                                navigateTo("editActivity")
                            },
                            onBrowseHotelsForTrip = {
                                navigateTo("hotels")
                            }
                        )
                        "addActivity" -> AddActivityScreen(
                            viewModel = viewModel,
                            tripId = selectedTripId,
                            onNavigateBack = { goBack("tripDetail") },
                            onNavigateToHome = { resetNavigation("home") },
                            onNavigateToHotels = { navigateTo("hotels") },
                            onNavigateToGallery = { navigateTo("gallery") },
                            onNavigateToProfile = { navigateTo("profile") },
                            onNavigateToPreferences = { navigateTo("preferences") },
                            onNavigateToAbout = { navigateTo("about") },
                            onNavigateToNewTrip = { navigateTo("newTrip") }
                        )
                        "editActivity" -> activityToEdit?.let { activity ->
                            EditActivityScreen(
                                viewModel = viewModel,
                                activity = activity,
                                onNavigateBack = { goBack("tripDetail") },
                                onNavigateToHome = { resetNavigation("home") },
                                onNavigateToHotels = { navigateTo("hotels") },
                                onNavigateToGallery = { navigateTo("gallery") },
                                onNavigateToProfile = { navigateTo("profile") },
                                onNavigateToPreferences = { navigateTo("preferences") },
                                onNavigateToAbout = { navigateTo("about") },
                                onNavigateToNewTrip = { navigateTo("newTrip") }
                            )
                        }
                        "activityDetail" -> selectedActivity?.let { activity ->
                            ActivityDetailScreen(
                                activity = activity,
                                onNavigateBack = { goBack("tripDetail") },
                                onNavigateToHome = { resetNavigation("home") },
                                onNavigateToHotels = { navigateTo("hotels") },
                                onNavigateToGallery = { navigateTo("gallery") },
                                onNavigateToProfile = { navigateTo("profile") },
                                onNavigateToPreferences = { navigateTo("preferences") },
                                onNavigateToAbout = { navigateTo("about") },
                                onNavigateToNewTrip = { navigateTo("newTrip") }
                            )
                        }
                        "gallery" -> GalleryScreen(
                            onNavigateBack = { goBack() },
                            onNavigateToHome = { resetNavigation("home") },
                            onNavigateToHotels = { navigateTo("hotels") },
                            onNavigateToPreferences = { navigateTo("preferences") },
                            onNavigateToAbout = { navigateTo("about") },
                            onNavigateToProfile = { navigateTo("profile") },
                            onNavigateToNewTrip = { navigateTo("newTrip") }
                        )
                        "preferences" -> PreferencesScreen(
                            userViewModel = userViewModel,
                            onNavigateBack = { goBack() },
                            onNavigateToHome = { resetNavigation("home") },
                            onNavigateToAbout = { navigateTo("about") },
                            onNavigateToHotels = { navigateTo("hotels") },
                            onNavigateToGallery = { navigateTo("gallery") },
                            onNavigateToProfile = { navigateTo("profile") },
                            onNavigateToNewTrip = { navigateTo("newTrip") },
                            onDarkModeChange = { isDark -> darkMode = isDark },
                            onLanguageChange = { recreate() }
                        )
                        "about" -> AboutScreen(
                            onNavigateBack = { goBack() },
                            onNavigateToHome = { resetNavigation("home") },
                            onNavigateToTerms = { navigateTo("terms") },
                            onNavigateToPreferences = { navigateTo("preferences") },
                            onNavigateToHotels = { navigateTo("hotels") },
                            onNavigateToGallery = { navigateTo("gallery") },
                            onNavigateToProfile = { navigateTo("profile") },
                            onNavigateToNewTrip = { navigateTo("newTrip") }
                        )
                        "terms" -> TermsScreen(
                            onNavigateBack = { goBack("about") },
                            onNavigateToHome = { resetNavigation("home") }
                        )
                        "profile" -> ProfileScreen(
                            userViewModel = userViewModel,
                            tripCount = trips.size,
                            onNavigateBack = { goBack() },
                            onNavigateToHome = { resetNavigation("home") },
                            onNavigateToPreferences = { navigateTo("preferences") },
                            onNavigateToAbout = { navigateTo("about") },
                            onNavigateToHotels = { navigateTo("hotels") },
                            onNavigateToGallery = { navigateTo("gallery") },
                            onNavigateToNewTrip = { navigateTo("newTrip") },
                            onLogout = { authViewModel.signOut() }
                        )
                        else -> HomeScreen(
                            viewModel = viewModel,
                            onTripClick = { tripId ->
                                selectedTripId = tripId
                                navigateTo("tripDetail")
                            },
                            onNavigateToHotels = { navigateTo("hotels") },
                            onNavigateToGallery = { navigateTo("gallery") },
                            onNavigateToPreferences = { navigateTo("preferences") },
                            onNavigateToAbout = { navigateTo("about") },
                            onNavigateToProfile = { navigateTo("profile") },
                            onNavigateToTripOptions = { trip ->
                                selectedTrip = trip
                                navigateTo("tripOptions")
                            },
                            onNavigateToNewTrip = { navigateTo("newTrip") }
                        )
                    }
                }
            }
        }
    }
}

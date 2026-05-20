package com.example.ruler.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ruler.R
import com.example.ruler.domain.Hotel
import com.example.ruler.domain.HotelReservationRequest
import com.example.ruler.domain.HotelRoom
import com.example.ruler.ui.viewmodels.HotelViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelBookingScreen(
    hotelViewModel: HotelViewModel,
    hotel: Hotel,
    room: HotelRoom,
    startDate: String,
    endDate: String,
    defaultGuestName: String,
    defaultGuestEmail: String,
    onNavigateBack: () -> Unit,
    onBookingCompleted: (String) -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToGallery: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToPreferences: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToNewTrip: () -> Unit = {}
) {
    val uiState by hotelViewModel.uiState.collectAsState()
    var guestName by remember(defaultGuestName) { mutableStateOf(defaultGuestName) }
    var guestEmail by remember(defaultGuestEmail) { mutableStateOf(defaultGuestEmail) }
    var guestNameError by remember { mutableStateOf(false) }
    var guestEmailError by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.createdTripId) {
        uiState.createdTripId?.let { tripId ->
            onBookingCompleted(tripId)
            hotelViewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.hotel_booking_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAbout) {
                        Icon(Icons.Default.Info, contentDescription = "About")
                    }
                    IconButton(onClick = onNavigateToPreferences) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        bottomBar = {
            Box {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToHome,
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text(stringResource(R.string.home), fontSize = 13.sp) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Icon(Icons.Default.LocationOn, contentDescription = "Trips") },
                        label = { Text(stringResource(R.string.trips), fontSize = 13.sp) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Spacer(modifier = Modifier.size(48.dp)) },
                        label = { Text("") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToGallery,
                        icon = { Icon(Icons.Default.Hotel, contentDescription = "Gallery") },
                        label = { Text(stringResource(R.string.gallery), fontSize = 13.sp) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToProfile,
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text(stringResource(R.string.profile), fontSize = 13.sp) }
                    )
                }
                FloatingActionButton(
                    onClick = onNavigateToNewTrip,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 0.dp)
                        .size(56.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Hotel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            item {
                BookingSummaryCard(
                    hotel = hotel,
                    room = room,
                    startDate = startDate.toDisplayDate(),
                    endDate = endDate.toDisplayDate()
                )
            }
            item {
                ImageGallerySection(
                    title = stringResource(R.string.hotel_images),
                    imageUrls = listOf(hotel.imageUrl)
                )
            }
            item {
                ImageGallerySection(
                    title = stringResource(R.string.room_images),
                    imageUrls = room.imageUrls
                )
            }
            item {
                GuestInfoCard(
                    guestName = guestName,
                    guestEmail = guestEmail,
                    guestNameError = guestNameError,
                    guestEmailError = guestEmailError,
                    onGuestNameChange = {
                        guestName = it
                        guestNameError = false
                    },
                    onGuestEmailChange = {
                        guestEmail = it
                        guestEmailError = false
                    }
                )
            }
            uiState.errorMessage?.let { error ->
                item {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            uiState.successMessage?.let { message ->
                item {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            item {
                Button(
                    onClick = {
                        guestNameError = guestName.isBlank()
                        guestEmailError = guestEmail.isBlank()
                        if (guestNameError || guestEmailError) return@Button
                        hotelViewModel.reserveRoomAndCreateTrip(
                            hotel = hotel,
                            room = room,
                            request = HotelReservationRequest(
                                hotelId = hotel.id,
                                roomId = room.id,
                                startDate = startDate,
                                endDate = endDate,
                                guestName = guestName.trim(),
                                guestEmail = guestEmail.trim()
                            )
                        )
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(R.string.confirm_hotel_booking))
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageGallerySection(
    title: String,
    imageUrls: List<String>
) {
    if (imageUrls.isEmpty()) return
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 4.dp)
        ) {
            items(imageUrls) { imageUrl ->
                Card(
                    modifier = Modifier
                        .fillParentMaxWidth(0.82f)
                        .aspectRatio(1.45f),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingSummaryCard(
    hotel: Hotel,
    room: HotelRoom,
    startDate: String,
    endDate: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = hotel.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            BookingInfoRow(Icons.Default.LocationOn, hotel.address)
            BookingInfoRow(
                Icons.Default.MeetingRoom,
                room.roomType.replaceFirstChar { char ->
                    if (char.isLowerCase()) char.titlecase() else char.toString()
                }
            )
            BookingInfoRow(Icons.Default.CalendarToday, "$startDate - $endDate")
            Text(
                text = stringResource(R.string.price_per_night, room.price),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun GuestInfoCard(
    guestName: String,
    guestEmail: String,
    guestNameError: Boolean,
    guestEmailError: Boolean,
    onGuestNameChange: (String) -> Unit,
    onGuestEmailChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.booking_guest_details),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = guestName,
                onValueChange = onGuestNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.full_name)) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                isError = guestNameError,
                supportingText = if (guestNameError) {
                    { Text(stringResource(R.string.required_field)) }
                } else {
                    null
                }
            )
            OutlinedTextField(
                value = guestEmail,
                onValueChange = onGuestEmailChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.email)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                isError = guestEmailError,
                supportingText = if (guestEmailError) {
                    { Text(stringResource(R.string.required_field)) }
                } else {
                    null
                }
            )
        }
    }
}

@Composable
private fun BookingInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun String.toDisplayDate(): String {
    return LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE)
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}

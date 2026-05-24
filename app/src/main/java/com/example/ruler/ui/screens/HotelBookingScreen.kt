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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ruler.R
import com.example.ruler.domain.Hotel
import com.example.ruler.domain.HotelReservationRequest
import com.example.ruler.domain.HotelReservationResult
import com.example.ruler.domain.HotelRoom
import com.example.ruler.ui.viewmodels.HotelViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelBookingScreen(
    hotelViewModel: HotelViewModel,
    hotel: Hotel,
    room: HotelRoom,
    startDate: String,
    endDate: String,
    sourceTripId: String?,
    defaultGuestName: String,
    defaultGuestEmail: String,
    onNavigateBack: () -> Unit,
    onBookingCompleted: (HotelReservationResult) -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToGallery: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToPreferences: () -> Unit = {},
    onNavigateToHotels: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToNewTrip: () -> Unit = {}
) {
    val uiState by hotelViewModel.uiState.collectAsState()
    var guestName by remember(defaultGuestName) { mutableStateOf(defaultGuestName) }
    var guestEmail by remember(defaultGuestEmail) { mutableStateOf(defaultGuestEmail) }
    var guestNameError by remember { mutableStateOf(false) }
    var guestEmailError by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.lastReservation?.reservation?.id) {
        val reservation = uiState.lastReservation
        if (reservation != null) {
            onBookingCompleted(reservation)
            hotelViewModel.clearMessages()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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
                        onClick = onNavigateToHotels,
                        icon = { Icon(Icons.Default.Hotel, contentDescription = "Hotels") },
                        label = { Text(stringResource(R.string.hotels), fontSize = 13.sp) }
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
                        icon = { Icon(Icons.Default.Face, contentDescription = "Gallery") },
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
                BookingHeroCard(
                    hotel = hotel,
                    room = room,
                    startDate = startDate.toDisplayDate(),
                    endDate = endDate.toDisplayDate(),
                    sourceTripId = sourceTripId
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
                    BookingMessageCard(
                        icon = Icons.Default.Warning,
                        text = error,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            uiState.successMessage?.let { message ->
                item {
                    BookingMessageCard(
                        icon = Icons.Default.CheckCircle,
                        text = message,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            item {
                BookingActionCard(
                    room = room,
                    nights = nightsBetween(startDate, endDate),
                    isLoading = uiState.isLoading,
                    onConfirm = {
                        guestNameError = guestName.isBlank()
                        guestEmailError = guestEmail.isBlank()
                        if (guestNameError || guestEmailError) return@BookingActionCard
                        val request = HotelReservationRequest(
                            hotelId = hotel.id,
                            roomId = room.id,
                            startDate = startDate,
                            endDate = endDate,
                            guestName = guestName.trim(),
                            guestEmail = guestEmail.trim()
                        )
                        hotelViewModel.reserveRoom(request)
                    }
                )
            }
        }
    }
}

@Composable
private fun BookingHeroCard(
    hotel: Hotel,
    room: HotelRoom,
    startDate: String,
    endDate: String,
    sourceTripId: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AsyncImage(
                model = hotel.imageUrl,
                contentDescription = hotel.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = hotel.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = hotel.address,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        label = {
                            Text(
                                room.roomType.replaceFirstChar { char ->
                                    if (char.isLowerCase()) char.titlecase() else char.toString()
                                }
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.MeetingRoom,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        label = { Text(stringResource(R.string.price_per_night, room.price)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Payments,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BookingInfoRow(Icons.Default.CalendarToday, "$startDate - $endDate")
                        BookingInfoRow(
                            Icons.Default.Hotel,
                            if (sourceTripId != null) {
                                stringResource(R.string.booking_assigns_to_trip)
                            } else {
                                stringResource(R.string.booking_saved_to_hotels)
                            }
                        )
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
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
private fun BookingMessageCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = contentColor)
            Text(
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.booking_guest_details),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.booking_guest_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            OutlinedTextField(
                value = guestName,
                onValueChange = onGuestNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.full_name)) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                isError = guestNameError,
                shape = RoundedCornerShape(16.dp),
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
                shape = RoundedCornerShape(16.dp),
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
private fun BookingActionCard(
    room: HotelRoom,
    nights: Long,
    isLoading: Boolean,
    onConfirm: () -> Unit
) {
    val normalizedNights = nights.coerceAtLeast(1)
    val total = room.price * normalizedNights.toDouble()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.booking_total_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = stringResource(R.string.nights_total, normalizedNights.toInt(), total),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
                    )
                }
                Text(
                    text = stringResource(R.string.total_price, total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = onConfirm,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(stringResource(R.string.confirm_hotel_booking))
                }
            }
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

private fun nightsBetween(startDate: String, endDate: String): Long {
    return runCatching {
        val start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE)
        val end = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE)
        ChronoUnit.DAYS.between(start, end)
    }.getOrDefault(1L)
}

package com.example.ruler.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ruler.R
import com.example.ruler.domain.LocalHotel
import com.example.ruler.domain.Trip

private fun formatReservationDate(value: String?): String {
    if (value.isNullOrBlank()) return ""
    return runCatching {
        if (value.contains("/")) value
        else {
            val parsed = java.time.LocalDate.parse(value, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
            parsed.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }
    }.getOrElse { value }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationsScreen(
    hotels: List<LocalHotel>,
    trips: List<Trip>,
    deletingReservationId: String?,
    errorMessage: String?,
    successMessage: String?,
    onDeleteReservation: (String) -> Unit,
    onClearMessages: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToHotels: () -> Unit = {},
    onNavigateToGallery: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToPreferences: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToNewTrip: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingDeletionHotel by remember { mutableStateOf<LocalHotel?>(null) }

    LaunchedEffect(errorMessage, successMessage) {
        val message = errorMessage ?: successMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        onClearMessages()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.my_reservations),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                        selected = true,
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
                        .offset(y = (-20).dp)
                        .size(56.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "New trip",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        if (hotels.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.BookOnline,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Text(
                        text = stringResource(R.string.no_reservations),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(
                        onClick = onNavigateToHotels,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.browse_hotels))
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.reservations_count, hotels.size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(hotels) { hotel ->
                    val associatedTrip = trips.find { it.id == hotel.assignedTripId }
                    ReservationCard(
                        hotel = hotel,
                        trip = associatedTrip,
                        isDeleting = deletingReservationId == hotel.id,
                        onDeleteClick = { pendingDeletionHotel = hotel }
                    )
                }
            }
        }
    }

    pendingDeletionHotel?.let { hotel ->
        AlertDialog(
            onDismissRequest = {
                if (deletingReservationId == null) pendingDeletionHotel = null
            },
            title = { Text(stringResource(R.string.cancel_reservation_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.cancel_reservation_message,
                        hotel.name
                    )
                )
            },
            confirmButton = {
                TextButton(
                    enabled = deletingReservationId == null,
                    onClick = {
                        onDeleteReservation(hotel.id)
                        pendingDeletionHotel = null
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(
                    enabled = deletingReservationId == null,
                    onClick = { pendingDeletionHotel = null }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun ReservationCard(
    hotel: LocalHotel,
    trip: Trip?,
    isDeleting: Boolean,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Hotel name + reservation badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Hotel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = hotel.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (!hotel.reservationId.isNullOrBlank()) {
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                text = "# ${hotel.reservationId!!.take(8)}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        icon = {
                            Icon(
                                Icons.Default.ConfirmationNumber,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    )
                }
            }

            // Address
            if (hotel.address.isNotBlank()) {
                Text(
                    text = hotel.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Associated trip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Default.FlightTakeoff,
                    contentDescription = null,
                    tint = if (trip != null) MaterialTheme.colorScheme.secondary
                           else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (trip != null) "${trip.emoji} ${trip.title}"
                           else stringResource(R.string.no_trip_assigned),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (trip != null) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (trip != null) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Check-in / Check-out dates
            val checkIn = formatReservationDate(hotel.startDate)
            val checkOut = formatReservationDate(hotel.endDate)
            if (checkIn.isNotBlank() || checkOut.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (checkIn.isNotBlank()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.check_in_date),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = checkIn,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    if (checkOut.isNotBlank()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.check_out_date),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = checkOut,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Nights + total price
            if (hotel.nights > 0 || hotel.pricePerNight > 0.0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (hotel.nights > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.NightsStay,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.nights_label, hotel.nights),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (hotel.pricePerNight > 0.0) {
                        val nights = hotel.nights.coerceAtLeast(1)
                        val total = hotel.pricePerNight * nights
                        Text(
                            text = stringResource(R.string.total_price, total),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Guest info
            if (!hotel.guestName.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = hotel.guestName!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Hotel and room images
            val allImages = buildList {
                hotel.hotelImageUrl?.takeIf { it.isNotBlank() }?.let { add(it) }
                addAll(hotel.roomImageUrls)
            }
            if (allImages.isNotEmpty()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Text(
                    text = stringResource(R.string.hotel_images),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 4.dp)
                ) {
                    items(allImages) { imageUrl ->
                        Card(
                            modifier = Modifier
                                .size(width = 160.dp, height = 100.dp),
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            FilledTonalButton(
                onClick = onDeleteClick,
                enabled = !isDeleting,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                } else {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(stringResource(R.string.cancel_reservation_action))
            }
        }
    }
}

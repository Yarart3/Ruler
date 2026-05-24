package com.example.ruler.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.ui.platform.LocalContext
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
import java.util.Calendar

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
    title: String,
    hotels: List<LocalHotel>,
    trips: List<Trip>,
    deletingReservationId: String?,
    errorMessage: String?,
    successMessage: String?,
    onDeleteReservation: (String) -> Unit,
    onBrowseHotels: () -> Unit,
    onAssignHotelToTrip: (String, String, String, String, String, String) -> Unit,
    onEditHotel: (LocalHotel) -> Unit,
    onClearMessages: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
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
    var hotelToAssign by remember { mutableStateOf<LocalHotel?>(null) }
    var hotelToEdit by remember { mutableStateOf<LocalHotel?>(null) }

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
                        text = title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
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
                    onClick = onBrowseHotels,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-20).dp)
                        .size(56.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Browse hotels",
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
                        onClick = onBrowseHotels,
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
                    HotelActionsCard(
                        hotelCount = hotels.size,
                        onBrowseHotels = onBrowseHotels
                    )
                }
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
                        canAssign = hotel.assignedTripId == null && trips.isNotEmpty(),
                        isDeleting = deletingReservationId == hotel.id,
                        onDeleteClick = { pendingDeletionHotel = hotel },
                        onAssignClick = { hotelToAssign = hotel },
                        onEditClick = { hotelToEdit = hotel }
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

    hotelToAssign?.let { hotel ->
        AssignToTripDialog(
            hotel = hotel,
            trips = trips,
            onConfirm = { tripId, checkIn, checkOut ->
                onAssignHotelToTrip(hotel.id, hotel.name, hotel.address, tripId, checkIn, checkOut)
                hotelToAssign = null
            },
            onDismiss = { hotelToAssign = null }
        )
    }

    hotelToEdit?.let { hotel ->
        EditHotelDialog(
            hotel = hotel,
            onConfirm = { updatedName, updatedAddress ->
                onEditHotel(hotel.copy(name = updatedName, address = updatedAddress))
                hotelToEdit = null
            },
            onDismiss = { hotelToEdit = null }
        )
    }
}

@Composable
private fun HotelActionsCard(
    hotelCount: Int,
    onBrowseHotels: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.hotel_reservations_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = stringResource(R.string.hotel_reservations_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
                )
            }
            Button(
                onClick = onBrowseHotels,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(stringResource(R.string.browse_hotels))
            }
        }
    }
}

@Composable
private fun ReservationCard(
    hotel: LocalHotel,
    trip: Trip?,
    canAssign: Boolean,
    isDeleting: Boolean,
    onDeleteClick: () -> Unit,
    onAssignClick: () -> Unit,
    onEditClick: () -> Unit
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

            if (trip == null && canAssign) {
                FilledTonalButton(
                    onClick = onAssignClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.assign_hotel))
                }
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.edit))
                }

                FilledTonalButton(
                    onClick = onDeleteClick,
                    enabled = !isDeleting,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
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
}

@Composable
private fun EditHotelDialog(
    hotel: LocalHotel,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(hotel.name) }
    var address by remember { mutableStateOf(hotel.address) }
    var nameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_hotel), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = false },
                    label = { Text(stringResource(R.string.hotel_name_hint)) },
                    leadingIcon = { Icon(Icons.Default.Hotel, contentDescription = null) },
                    isError = nameError,
                    supportingText = { if (nameError) Text(stringResource(R.string.required_field)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(stringResource(R.string.hotel_address_hint)) },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isBlank()) { nameError = true; return@Button }
                onConfirm(name, address)
            }) {
                Text(stringResource(R.string.save_hotel))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignToTripDialog(
    hotel: LocalHotel,
    trips: List<Trip>,
    onConfirm: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val cal = Calendar.getInstance()
    val hasReservedDates = !hotel.startDate.isNullOrBlank() && !hotel.endDate.isNullOrBlank()

    var selectedTripId by remember { mutableStateOf(trips.firstOrNull()?.id ?: "") }
    var tripDropdownExpanded by remember { mutableStateOf(false) }
    var checkIn by remember { mutableStateOf(formatReservationDate(hotel.startDate)) }
    var checkOut by remember { mutableStateOf(formatReservationDate(hotel.endDate)) }
    var checkInError by remember { mutableStateOf(false) }
    var checkOutError by remember { mutableStateOf(false) }

    val checkInPicker = DatePickerDialog(
        context,
        { _, y, m, d ->
            checkIn = "%02d/%02d/%04d".format(d, m + 1, y)
            checkInError = false
        },
        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
    )
    val checkOutPicker = DatePickerDialog(
        context,
        { _, y, m, d ->
            checkOut = "%02d/%02d/%04d".format(d, m + 1, y)
            checkOutError = false
        },
        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
    )

    val selectedTrip = trips.find { it.id == selectedTripId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.assign_hotel), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = hotel.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (hasReservedDates) {
                    Text(
                        text = stringResource(
                            R.string.hotel_reserved_dates_locked,
                            formatReservationDate(hotel.startDate),
                            formatReservationDate(hotel.endDate)
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = tripDropdownExpanded,
                    onExpandedChange = { tripDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedTrip?.title ?: stringResource(R.string.select_trip),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(stringResource(R.string.select_trip)) },
                        leadingIcon = { Icon(Icons.Default.FlightTakeoff, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tripDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = tripDropdownExpanded,
                        onDismissRequest = { tripDropdownExpanded = false }
                    ) {
                        trips.forEach { trip ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(trip.title, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            "${trip.startDate} – ${trip.endDate}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    selectedTripId = trip.id
                                    tripDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = checkIn,
                    onValueChange = { },
                    label = { Text(stringResource(R.string.check_in_date)) },
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    trailingIcon = {
                        if (!hasReservedDates) {
                            IconButton(onClick = { checkInPicker.show() }) {
                                Icon(Icons.Default.DateRange, contentDescription = null)
                            }
                        }
                    },
                    isError = checkInError,
                    supportingText = { if (checkInError) Text(stringResource(R.string.required_field)) },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = checkOut,
                    onValueChange = { },
                    label = { Text(stringResource(R.string.check_out_date)) },
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    trailingIcon = {
                        if (!hasReservedDates) {
                            IconButton(onClick = { checkOutPicker.show() }) {
                                Icon(Icons.Default.DateRange, contentDescription = null)
                            }
                        }
                    },
                    isError = checkOutError,
                    supportingText = { if (checkOutError) Text(stringResource(R.string.required_field)) },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                var hasError = false
                if (checkIn.isBlank()) { checkInError = true; hasError = true }
                if (checkOut.isBlank()) { checkOutError = true; hasError = true }
                if (hasError || selectedTripId.isBlank()) return@Button
                onConfirm(selectedTripId, checkIn, checkOut)
            }) {
                Text(stringResource(R.string.save_hotel))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

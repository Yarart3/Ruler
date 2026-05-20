package com.example.ruler.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ruler.R
import com.example.ruler.domain.LocalHotel
import com.example.ruler.domain.Trip
import com.example.ruler.ui.viewmodels.LocalHotelViewModel
import java.util.Calendar

private fun formatHotelDateForDisplay(value: String?): String {
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
fun HotelsScreen(
    viewModel: LocalHotelViewModel,
    trips: List<Trip>,
    onBrowseHotels: () -> Unit,
    onAssignHotelToTrip: (String, String, String, String, String, String) -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToGallery: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToPreferences: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {}
) {
    val hotels by viewModel.hotels.collectAsState()
    var hotelToAssign by remember { mutableStateOf<LocalHotel?>(null) }
    var hotelToEdit by remember { mutableStateOf<LocalHotel?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.hotels),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
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
                        onClick = { },
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
                    Text(text = "🏨", fontSize = 56.sp)
                    Text(
                        text = stringResource(R.string.no_hotels),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = onBrowseHotels,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.browse_hotels))
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.my_hotels),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(hotels, key = { it.id }) { hotel ->
                    val assignedTrip = hotel.assignedTripId?.let { tid -> trips.find { it.id == tid } }
                    SavedHotelCard(
                        hotel = hotel,
                        assignedTripTitle = assignedTrip?.title,
                        canAssign = hotel.assignedTripId == null && trips.isNotEmpty(),
                        onEditClick = { hotelToEdit = hotel },
                        onDeleteClick = { viewModel.deleteHotel(hotel.id) },
                        onAssignClick = { hotelToAssign = hotel }
                    )
                }
            }
        }
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
                viewModel.updateHotel(hotel.copy(name = updatedName, address = updatedAddress))
                hotelToEdit = null
            },
            onDismiss = { hotelToEdit = null }
        )
    }
}

@Composable
private fun SavedHotelCard(
    hotel: LocalHotel,
    assignedTripTitle: String?,
    canAssign: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAssignClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Hotel,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hotel.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (hotel.address.isNotBlank()) {
                        Text(
                            text = hotel.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (assignedTripTitle != null) {
                        Text(
                            text = "Assigned to: $assignedTripTitle",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (hotel.nights > 0) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${hotel.nights} nights",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.AttachMoney,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "€%.0f total".format(hotel.nights * hotel.pricePerNight),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onDeleteClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
                if (canAssign) {
                    IconButton(onClick = onAssignClick, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = "Assign to trip",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
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
                if (hotel.nights > 0) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "${hotel.nights} nights",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "€%.0f/night · €%.0f total".format(
                                    hotel.pricePerNight,
                                    hotel.nights * hotel.pricePerNight
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
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
    var checkIn by remember { mutableStateOf(formatHotelDateForDisplay(hotel.startDate)) }
    var checkOut by remember { mutableStateOf(formatHotelDateForDisplay(hotel.endDate)) }
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
                            formatHotelDateForDisplay(hotel.startDate),
                            formatHotelDateForDisplay(hotel.endDate)
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

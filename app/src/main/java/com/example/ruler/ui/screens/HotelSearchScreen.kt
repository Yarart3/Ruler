package com.example.ruler.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
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
import com.example.ruler.domain.Hotel
import com.example.ruler.domain.HotelRoom
import com.example.ruler.ui.viewmodels.HotelViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelSearchScreen(
    hotelViewModel: HotelViewModel,
    onRoomSelected: (Hotel, HotelRoom, String, String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToGallery: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToPreferences: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToNewTrip: () -> Unit = {}
) {
    val uiState by hotelViewModel.uiState.collectAsState()

    val cities = listOf("London", "Paris", "Barcelona")
    var selectedCity by remember { mutableStateOf("") }
    var cityExpanded by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf("") }
    var startDateApi by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var endDateApi by remember { mutableStateOf("") }
    var hasSearched by remember { mutableStateOf(false) }

    var cityError by remember { mutableStateOf(false) }
    var startDateError by remember { mutableStateOf(false) }
    var endDateError by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val cal = Calendar.getInstance()

    val startDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            startDate = "%02d/%02d/%04d".format(day, month + 1, year)
            startDateApi = "%04d-%02d-%02d".format(year, month + 1, day)
            startDateError = false
        },
        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
    )

    val endDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            endDate = "%02d/%02d/%04d".format(day, month + 1, year)
            endDateApi = "%04d-%02d-%02d".format(year, month + 1, day)
            endDateError = false
        },
        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.hotel_search),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
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
                        icon = { Spacer(modifier = Modifier.size(48.dp)) }
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            item {
                SearchFormCard(
                    cities = cities,
                    selectedCity = selectedCity,
                    cityExpanded = cityExpanded,
                    startDate = startDate,
                    endDate = endDate,
                    cityError = cityError,
                    startDateError = startDateError,
                    endDateError = endDateError,
                    isLoading = uiState.isLoading,
                    onCityExpandedChange = { cityExpanded = it },
                    onCitySelected = { city ->
                        selectedCity = city
                        cityExpanded = false
                        cityError = false
                    },
                    onStartDateClick = { startDatePickerDialog.show() },
                    onEndDateClick = { endDatePickerDialog.show() },
                    onSearch = {
                        cityError = selectedCity.isEmpty()
                        startDateError = startDate.isEmpty()
                        endDateError = endDate.isEmpty()
                        if (!cityError && !startDateError && !endDateError) {
                            hasSearched = true
                            hotelViewModel.searchAvailability(
                                startDate = startDateApi,
                                endDate = endDateApi,
                                city = selectedCity
                            )
                        }
                    }
                )
            }

            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            uiState.errorMessage?.let { error ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            if (!uiState.isLoading && uiState.hotels.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.available_hotels),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(uiState.hotels) { hotel ->
                    HotelResultCard(
                        hotel = hotel,
                        onRoomSelected = { room ->
                            onRoomSelected(hotel, room, startDateApi, endDateApi)
                        }
                    )
                }
            }

            if (hasSearched && !uiState.isLoading && uiState.hotels.isEmpty() && uiState.errorMessage == null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.no_hotels_found),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchFormCard(
    cities: List<String>,
    selectedCity: String,
    cityExpanded: Boolean,
    startDate: String,
    endDate: String,
    cityError: Boolean,
    startDateError: Boolean,
    endDateError: Boolean,
    isLoading: Boolean,
    onCityExpandedChange: (Boolean) -> Unit,
    onCitySelected: (String) -> Unit,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    onSearch: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.hotel_search_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ExposedDropdownMenuBox(
                expanded = cityExpanded,
                onExpandedChange = onCityExpandedChange
            ) {
                OutlinedTextField(
                    value = selectedCity.ifEmpty { stringResource(R.string.select_city) },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.city)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded)
                    },
                    isError = cityError,
                    supportingText = if (cityError) {
                        { Text(stringResource(R.string.required_field)) }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = cityExpanded,
                    onDismissRequest = { onCityExpandedChange(false) }
                ) {
                    cities.forEach { city ->
                        DropdownMenuItem(
                            text = { Text(city) },
                            onClick = { onCitySelected(city) }
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.start_date)) },
                    placeholder = { Text(stringResource(R.string.placeholder_date)) },
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    },
                    isError = startDateError,
                    supportingText = if (startDateError) {
                        { Text(stringResource(R.string.required_field)) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { onStartDateClick() }
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = endDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.end_date)) },
                    placeholder = { Text(stringResource(R.string.placeholder_date)) },
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    },
                    isError = endDateError,
                    supportingText = if (endDateError) {
                        { Text(stringResource(R.string.required_field)) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { onEndDateClick() }
                )
            }

            Button(
                onClick = onSearch,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.search_hotels))
            }
        }
    }
}

@Composable
private fun HotelResultCard(
    hotel: Hotel,
    onRoomSelected: (HotelRoom) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hotel.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "⭐".repeat(hotel.rating.coerceIn(0, 5)),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = hotel.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = stringResource(R.string.available_rooms),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            hotel.rooms.forEach { room ->
                RoomRow(
                    room = room,
                    onBookClick = { onRoomSelected(room) }
                )
            }
        }
    }
}

@Composable
private fun RoomRow(
    room: HotelRoom,
    onBookClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.Default.MeetingRoom,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = room.roomType,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            text = stringResource(R.string.price_per_night, room.price),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        OutlinedButton(onClick = onBookClick) {
            Text(stringResource(R.string.book_room))
        }
    }
}

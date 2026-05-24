package com.example.ruler.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
    onNavigateToHotels: () -> Unit = {},
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
    val hasActiveFilters = selectedCity.isNotBlank() && startDateApi.isNotBlank() && endDateApi.isNotBlank()
    val shouldShowResults = hasSearched && hasActiveFilters

    val context = LocalContext.current
    val cal = Calendar.getInstance()

    LaunchedEffect(Unit) {
        hotelViewModel.clearSearchResults()
    }

    DisposableEffect(Unit) {
        onDispose { hotelViewModel.clearSearchResults() }
    }

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
        containerColor = MaterialTheme.colorScheme.background,
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
                        selected = true,
                        onClick = { },
                        icon = { Icon(Icons.Default.Hotel, contentDescription = "Hotels") },
                        label = { Text(stringResource(R.string.hotels), fontSize = 13.sp) }
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
                SearchIntroCard()
            }
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
                    MessageCard(
                        icon = Icons.Default.Warning,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        text = error
                    )
                }
            }

            if (!uiState.isLoading && shouldShowResults && uiState.hotels.isNotEmpty()) {
                item {
                    ResultsHeaderCard(resultCount = uiState.hotels.size)
                }
                items(uiState.hotels) { hotel ->
                    HotelResultCard(
                        hotel = hotel,
                        onRoomSelected = { room ->
                            if (hasActiveFilters) {
                                onRoomSelected(hotel, room, startDateApi, endDateApi)
                            }
                        }
                    )
                }
            }

            if (shouldShowResults && !uiState.isLoading && uiState.hotels.isEmpty() && uiState.errorMessage == null) {
                item {
                    MessageCard(
                        icon = Icons.Default.SearchOff,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        text = stringResource(R.string.no_hotels_found)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchIntroCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                ) {
                    Icon(
                        Icons.Default.TravelExplore,
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.hotel_search_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = stringResource(R.string.hotel_search_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
                    )
                }
            }
            AssistChip(
                onClick = {},
                enabled = false,
                label = { Text(stringResource(R.string.london_paris_barcelona)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun MessageCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    contentColor: Color,
    text: String
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
private fun ResultsHeaderCard(resultCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.available_hotels),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.hotel_results_summary, resultCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Hotel,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = resultCount.toString(),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.find_hotels),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.hotel_search_form_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            ExposedDropdownMenuBox(
                expanded = cityExpanded,
                onExpandedChange = onCityExpandedChange
            ) {
                OutlinedTextField(
                    value = selectedCity.ifEmpty { stringResource(R.string.select_city) },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.city)) },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded)
                    },
                    isError = cityError,
                    supportingText = if (cityError) {
                        { Text(stringResource(R.string.required_field)) }
                    } else {
                        null
                    },
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DateField(
                    value = startDate,
                    label = stringResource(R.string.start_date),
                    placeholder = stringResource(R.string.placeholder_date),
                    isError = startDateError,
                    onClick = onStartDateClick,
                    modifier = Modifier.weight(1f)
                )
                DateField(
                    value = endDate,
                    label = stringResource(R.string.end_date),
                    placeholder = stringResource(R.string.placeholder_date),
                    isError = endDateError,
                    onClick = onEndDateClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = onSearch,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading,
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(stringResource(R.string.search_hotels))
                }
            }
        }
    }
}

@Composable
private fun DateField(
    value: String,
    label: String,
    placeholder: String,
    isError: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            isError = isError,
            supportingText = if (isError) {
                { Text(stringResource(R.string.required_field)) }
            } else {
                null
            },
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { onClick() }
        )
    }
}

@Composable
private fun HotelResultCard(
    hotel: Hotel,
    onRoomSelected: (HotelRoom) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AsyncImage(
                model = hotel.imageUrl,
                contentDescription = hotel.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = hotel.name,
                            style = MaterialTheme.typography.titleLarge,
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
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "⭐".repeat(hotel.rating.coerceIn(0, 5)),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        label = { Text(stringResource(R.string.rooms_count, hotel.rooms.size)) }
                    )
                    val minPrice = hotel.rooms.minOfOrNull { it.price } ?: 0.0
                    val maxPrice = hotel.rooms.maxOfOrNull { it.price } ?: 0.0
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        label = { Text(stringResource(R.string.price_range_per_night, minPrice, maxPrice)) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.available_rooms),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                hotel.rooms.forEach { room ->
                    RoomRow(
                        room = room,
                        onBookClick = { onRoomSelected(room) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Composable
private fun RoomRow(
    room: HotelRoom,
    onBookClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Icon(
                    Icons.Default.MeetingRoom,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = room.roomType.replaceFirstChar { char ->
                        if (char.isLowerCase()) char.titlecase() else char.toString()
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.price_per_night, room.price),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = onBookClick,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(stringResource(R.string.book_room))
            }
        }
    }
}

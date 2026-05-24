package com.example.ruler.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ruler.R
import com.example.ruler.ui.viewmodels.GalleryViewModel
import com.example.ruler.ui.viewmodels.TripGallerySummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel,
    onTripClick: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToHotels: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToPreferences: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToNewTrip: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val filteredTrips = remember(uiState.tripSummaries, searchQuery) {
        val normalizedQuery = searchQuery.trim()
        if (normalizedQuery.isEmpty()) uiState.tripSummaries
        else {
            uiState.tripSummaries.filter { summary ->
                summary.trip.title.contains(normalizedQuery, ignoreCase = true) ||
                    summary.trip.destination.contains(normalizedQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.gallery), fontWeight = FontWeight.Bold) },
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
                        selected = true,
                        onClick = { },
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
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    label = { Text(stringResource(R.string.gallery_search_label)) },
                    placeholder = { Text(stringResource(R.string.gallery_search_placeholder)) }
                )
            }

            if (uiState.tripSummaries.isEmpty()) {
                item {
                    GalleryEmptyState(onNavigateToNewTrip = onNavigateToNewTrip)
                }
            } else {
                if (filteredTrips.isEmpty()) {
                    item {
                        GalleryNoResultsState()
                    }
                } else {
                    items(filteredTrips, key = { it.trip.id }) { summary ->
                        GalleryTripCard(
                            summary = summary,
                            onClick = { onTripClick(summary.trip.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GalleryEmptyState(
    onNavigateToNewTrip: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(38.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = stringResource(R.string.gallery_empty_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.gallery_empty_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(onClick = onNavigateToNewTrip, shape = RoundedCornerShape(14.dp)) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text(stringResource(R.string.create_trip))
            }
        }
    }
}

@Composable
private fun GalleryNoResultsState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = stringResource(R.string.gallery_search_empty_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.gallery_search_empty_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun GalleryTripCard(
    summary: TripGallerySummary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            if (summary.coverImageUri != null) {
                AsyncImage(
                    model = summary.coverImageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(188.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(188.dp)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = summary.trip.emoji, fontSize = 42.sp)
                        Text(
                            text = stringResource(R.string.gallery_no_photos_card),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = summary.trip.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = summary.trip.destination,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = stringResource(R.string.trip_photos, summary.imageCount),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Text(
                    text = "${summary.trip.startDate} - ${summary.trip.endDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (summary.trip.hotelReservation != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = stringResource(R.string.gallery_trip_has_reservation),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                OutlinedButton(
                    onClick = onClick,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(stringResource(R.string.gallery_open_trip))
                    Spacer(modifier = Modifier.size(6.dp))
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
        }
    }
}

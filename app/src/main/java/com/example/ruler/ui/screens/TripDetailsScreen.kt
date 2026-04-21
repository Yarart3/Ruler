package com.example.ruler.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ruler.R
import com.example.ruler.domain.Trip
import com.example.ruler.domain.TripActivity
import com.example.ruler.ui.viewmodels.TripListViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    viewModel: TripListViewModel,
    tripId: String,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToGallery: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToPreferences: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToActivityDetail: (TripActivity) -> Unit = {},
    onNavigateToNewTrip: () -> Unit = {},
    onNavigateToAddActivity: () -> Unit = {},
    onNavigateToEditActivity: (TripActivity) -> Unit = {}
) {
    val trips by viewModel.trips.collectAsState()
    val activities by viewModel.activities.collectAsState()
    val trip = trips.find { it.id == tripId } ?: trips.firstOrNull() ?: return
    val sortedActivities = remember(activities) {
        activities.sortedWith(compareBy({ parseActivityDateTime(it) }, { it.id }))
    }

    LaunchedEffect(tripId) {
        viewModel.selectTrip(tripId)
    }

    LaunchedEffect(trips) {
        if (trips.isNotEmpty() && trips.none { it.id == tripId }) {
            onNavigateBack()
        }
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.itinerary),
        stringResource(R.string.stats),
        stringResource(R.string.gallery)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = trip.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToAbout() }) {
                        Icon(Icons.Default.Info, contentDescription = "Info")
                    }
                    IconButton(onClick = { onNavigateToPreferences() }) {
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
                        onClick = { onNavigateToHome() },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text(stringResource(R.string.home), fontSize = 13.sp) }
                    )
                    NavigationBarItem(
                        selected = true,
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
                        onClick = { onNavigateToGallery() },
                        icon = { Icon(Icons.Default.Face, contentDescription = "Gallery") },
                        label = { Text(stringResource(R.string.gallery), fontSize = 13.sp) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { onNavigateToProfile() },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text(stringResource(R.string.profile), fontSize = 13.sp) }
                    )
                }
                FloatingActionButton(
                    onClick = { onNavigateToNewTrip() },
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
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = trip.emoji, fontSize = 56.sp)
                        Column {
                            Text(
                                text = trip.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "${trip.startDate} - ${trip.endDate}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = trip.description,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }

            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }

            when (selectedTab) {
                0 -> {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.activities),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { onNavigateToAddActivity() },
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.add), style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                    items(sortedActivities) { activity ->
                        ActivityCard(
                            activity = activity,
                            isLast = activity == sortedActivities.lastOrNull(),
                            onToggleDoneClick = { viewModel.toggleActivityDone(activity.id) },
                            onDeleteClick = { viewModel.deleteActivity(activity.id) },
                            onActivityClick = { onNavigateToActivityDetail(activity) },
                            onEditClick = { onNavigateToEditActivity(activity) }
                        )
                    }
                }
                1 -> {
                    item { StatsSection(trip = trip, activityCount = activities.size) }
                }
                2 -> {
                    item {
                        Text(
                            text = stringResource(R.string.gallery_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun ActivityCard(
    activity: TripActivity,
    isLast: Boolean = false,
    onToggleDoneClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onActivityClick: (TripActivity) -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(20.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onToggleDoneClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (activity.isDone) Icons.Default.CheckCircle
                        else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Toggle done",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activity.time,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = activity.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = activity.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = activity.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row {
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { onEditClick() }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

private fun parseActivityDateTime(activity: TripActivity) = runCatching {
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).apply {
        isLenient = false
    }.parse("${activity.date} ${activity.time}")
}.getOrNull()

@Composable
fun StatsSection(trip: Trip, activityCount: Int) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.trip_overview),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(modifier = Modifier.weight(1f), label = stringResource(R.string.start), value = trip.startDate, icon = "📅")
            StatCard(modifier = Modifier.weight(1f), label = stringResource(R.string.activities), value = "$activityCount", icon = "📍")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(modifier = Modifier.weight(1f), label = stringResource(R.string.end), value = trip.endDate, icon = "🧭")
            StatCard(modifier = Modifier.weight(1f), label = stringResource(R.string.trip), value = trip.emoji, icon = "✈️")
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, label: String, value: String, icon: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = icon, fontSize = 24.sp)
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}
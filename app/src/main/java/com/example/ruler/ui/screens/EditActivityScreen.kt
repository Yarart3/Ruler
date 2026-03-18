package com.example.ruler.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ruler.domain.TripActivity
import com.example.ruler.ui.viewmodels.TripListViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActivityScreen(
    viewModel: TripListViewModel,
    activity: TripActivity,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToGallery: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToPreferences: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToNewTrip: () -> Unit = {}
) {
    val context = LocalContext.current
    val error by viewModel.errorMessage.collectAsState()

    var title by remember { mutableStateOf(activity.title) }
    var description by remember { mutableStateOf(activity.description) }
    var date by remember { mutableStateOf(activity.date) }
    var time by remember { mutableStateOf(activity.time) }
    var titleError by remember { mutableStateOf(false) }

    val cal = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            date = "%02d/%02d/%04d".format(day, month + 1, year)
            viewModel.clearError()
        },
        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            time = "%02d:%02d".format(hour, minute)
            viewModel.clearError()
        },
        cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit activity", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToAbout() }) {
                        Icon(Icons.Default.Info, contentDescription = "About")
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
                        label = { Text("Home", fontSize = 13.sp) }
                    )
                    NavigationBarItem(
                        selected = true,
                        onClick = { onNavigateBack() },
                        icon = { Icon(Icons.Default.LocationOn, contentDescription = "Trips") },
                        label = { Text("Trips", fontSize = 13.sp) }
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
                        label = { Text("Gallery", fontSize = 13.sp) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { onNavigateToProfile() },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 13.sp) }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Edit activity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it; titleError = false; viewModel.clearError() },
                label = { Text("Title") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                isError = titleError,
                supportingText = { if (titleError) Text("This field is required") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it; viewModel.clearError() },
                label = { Text("Description") },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )

            Text(
                text = "Date & time",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = date,
                onValueChange = { },
                label = { Text("Date") },
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                trailingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Open calendar",
                        modifier = Modifier.clickable { datePickerDialog.show() }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                shape = RoundedCornerShape(12.dp),
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.primary,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            OutlinedTextField(
                value = time,
                onValueChange = { },
                label = { Text("Time") },
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                trailingIcon = {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Open time picker",
                        modifier = Modifier.clickable { timePickerDialog.show() }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { timePickerDialog.show() },
                shape = RoundedCornerShape(12.dp),
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.primary,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    titleError = title.isBlank()
                    if (!titleError) {
                        viewModel.updateActivity(
                            activity.copy(
                                title = title,
                                description = description,
                                date = date,
                                time = time
                            )
                        )
                        if (viewModel.errorMessage.value == null) onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save changes", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
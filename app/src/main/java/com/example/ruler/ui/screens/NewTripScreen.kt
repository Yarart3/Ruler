package com.example.ruler.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ruler.R
import com.example.ruler.ui.viewmodels.TripListViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTripScreen(
    viewModel: TripListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToGallery: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToPreferences: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToTrips: () -> Unit = {}
) {
    var title by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val error by viewModel.errorMessage.collectAsState()
    val tripCreatedCounter by viewModel.tripCreatedCounter.collectAsState()

    val emojiFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var titleError by remember { mutableStateOf(false) }
    var destinationError by remember { mutableStateOf(false) }
    var emojiError by remember { mutableStateOf(false) }
    var startDateError by remember { mutableStateOf(false) }
    var endDateError by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val cal = Calendar.getInstance()
    var pendingTripCreationTarget by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(tripCreatedCounter, pendingTripCreationTarget) {
        if (
            pendingTripCreationTarget != null &&
            tripCreatedCounter >= pendingTripCreationTarget!!
        ) {
            pendingTripCreationTarget = null
            onNavigateBack()
        }
    }

    LaunchedEffect(error) {
        if (error != null) {
            pendingTripCreationTarget = null
        }
    }

    val startDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            startDate = "%02d/%02d/%04d".format(day, month + 1, year)
            startDateError = false
            viewModel.clearError()
        },
        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
    )

    val endDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            endDate = "%02d/%02d/%04d".format(day, month + 1, year)
            endDateError = false
            viewModel.clearError()
        },
        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.new_trip), fontWeight = FontWeight.Bold) },
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
                        label = { Text(stringResource(R.string.home), fontSize = 13.sp) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { onNavigateToTrips() },
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
                    onClick = { },
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
                text = stringResource(R.string.trip_info),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it; titleError = false; viewModel.clearError() },
                label = { Text(stringResource(R.string.trip_name)) },
                placeholder = { Text(stringResource(R.string.placeholder_trip_name)) },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                isError = titleError,
                supportingText = { if (titleError) Text(stringResource(R.string.required_field)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it; destinationError = false; viewModel.clearError() },
                label = { Text(stringResource(R.string.destination)) },
                placeholder = { Text(stringResource(R.string.placeholder_destination)) },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                isError = destinationError,
                supportingText = { if (destinationError) Text(stringResource(R.string.required_field)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = emoji,
                onValueChange = { emoji = it; emojiError = false; viewModel.clearError() },
                label = { Text(stringResource(R.string.trip_emoji)) },
                placeholder = { Text(stringResource(R.string.placeholder_emoji)) },
                leadingIcon = { Icon(Icons.Default.Face, contentDescription = null) },
                isError = emojiError,
                supportingText = { if (emojiError) Text(stringResource(R.string.required_field)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(emojiFocusRequester)
                    .clickable {
                        emojiFocusRequester.requestFocus()
                        keyboardController?.show()
                    },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.dates),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { },
                    label = { Text(stringResource(R.string.start_date)) },
                    placeholder = { Text(stringResource(R.string.placeholder_date)) },
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null,
                            modifier = Modifier.clickable { startDatePickerDialog.show() })
                    },
                    isError = startDateError,
                    supportingText = { if (startDateError) Text(stringResource(R.string.required)) },
                    modifier = Modifier.weight(1f).clickable { startDatePickerDialog.show() },
                    shape = RoundedCornerShape(12.dp),
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = if (startDateError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.primary,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { },
                    label = { Text(stringResource(R.string.end_date)) },
                    placeholder = { Text(stringResource(R.string.placeholder_date)) },
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null,
                            modifier = Modifier.clickable { endDatePickerDialog.show() })
                    },
                    isError = endDateError,
                    supportingText = { if (endDateError) Text(stringResource(R.string.required)) },
                    modifier = Modifier.weight(1f).clickable { endDatePickerDialog.show() },
                    shape = RoundedCornerShape(12.dp),
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = if (endDateError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.primary,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.budget_and_notes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = budget,
                onValueChange = { budget = it; viewModel.clearError() },
                label = { Text(stringResource(R.string.estimated_budget)) },
                placeholder = { Text(stringResource(R.string.placeholder_budget)) },
                leadingIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it; viewModel.clearError() },
                label = { Text(stringResource(R.string.notes)) },
                placeholder = { Text(stringResource(R.string.placeholder_notes)) },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )

            error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    titleError = title.isBlank()
                    destinationError = destination.isBlank()
                    emojiError = emoji.isBlank()
                    startDateError = startDate.isBlank()
                    endDateError = endDate.isBlank()

                    if (!titleError && !destinationError && !emojiError && !startDateError && !endDateError) {
                        pendingTripCreationTarget = tripCreatedCounter + 1
                        viewModel.addTrip(
                            title = title,
                            destination = destination,
                            startDate = startDate,
                            endDate = endDate,
                            description = notes,
                            budget = budget,
                            emoji = emoji
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.create_trip), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.cancel), fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

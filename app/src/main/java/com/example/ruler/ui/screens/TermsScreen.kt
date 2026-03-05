package com.example.ruler.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit = {}
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms & Conditions", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Decline", fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Accept", fontWeight = FontWeight.SemiBold)
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
                text = "Last updated: January 2025",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TermsSection(
                title = "1. Acceptance of Terms",
                content = "By downloading or using Ruler, you agree to be bound by these Terms and Conditions. If you do not agree to these terms, please do not use the application."
            )
            TermsSection(
                title = "2. Use of the App",
                content = "Ruler is a travel planning application intended for personal, non-commercial use. You agree to use the app only for lawful purposes and in a way that does not infringe the rights of others."
            )
            TermsSection(
                title = "3. User Data",
                content = "Ruler stores travel data locally on your device. We do not collect, transmit, or share your personal information with third parties. Your data remains private and under your control."
            )
            TermsSection(
                title = "4. Intellectual Property",
                content = "All content, design, and code within Ruler is the property of the Ruler Team and is protected under the MIT License. You may not reproduce or distribute any part of the app without permission."
            )
            TermsSection(
                title = "5. Disclaimer",
                content = "Ruler is provided as-is without any warranties. We are not responsible for any inaccuracies in travel information or any damages arising from the use of the application."
            )
            TermsSection(
                title = "6. Changes to Terms",
                content = "We reserve the right to modify these terms at any time. Continued use of the app after changes constitutes acceptance of the new terms."
            )
            TermsSection(
                title = "7. Contact",
                content = "If you have any questions about these Terms, please contact us at: ruler.app@udl.cat"
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TermsSection(title: String, content: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )
    }
}
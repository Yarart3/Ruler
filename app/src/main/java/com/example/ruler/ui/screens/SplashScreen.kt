package com.example.ruler.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ruler.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {

    var startAnimation by remember { mutableStateOf(false) }
    var startZoom by remember { mutableStateOf(false) }

    // zoom burst effect — concepto visto en youtube @PhilippLackner, adaptado por nosotros
    val logoScale by animateFloatAsState(
        targetValue = when {
            startZoom -> 40f
            startAnimation -> 1f
            else -> 0f
        },
        animationSpec = when {
            startZoom -> tween(durationMillis = 700, easing = FastOutLinearInEasing)
            else -> spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "logoScale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (startZoom) 0f else if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = if (startZoom) 400 else 600),
        label = "logoAlpha"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (startZoom) 0f else if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "textAlpha"
    )

    LaunchedEffect(Unit) {
        delay(200)
        startAnimation = true
        delay(2000)
        startZoom = true
        delay(700)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // logo que creix fins a omplir la pantalla
        Image(
            painter = painterResource(id = R.drawable.ruler_logo),
            contentDescription = "Ruler logo",
            modifier = Modifier
                .size(130.dp)
                .graphicsLayer {
                    scaleX = logoScale
                    scaleY = logoScale
                    alpha = logoAlpha
                }
        )

        // nom i versió a baix
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp)
                .graphicsLayer { alpha = textAlpha },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Ruler",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Your travel companion",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "v1.0.0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
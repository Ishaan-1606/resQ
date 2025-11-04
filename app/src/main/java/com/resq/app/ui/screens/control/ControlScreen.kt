package com.resq.app.ui.screens.control

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.resq.app.ui.theme.ResQTheme
import com.resq.app.ui.viewmodel.AnalyticsControlViewModel
import com.resq.app.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ControlScreen(vm: AnalyticsControlViewModel = viewModel(factory = ViewModelFactory())) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Collect state from the ViewModel
    val servoStatus by vm.servoState.collectAsState()
    val isConnected by vm.isConnected.collectAsState()

    // Local UI state
    var sliderPosition by remember { mutableFloatStateOf(90f) }
    var isPending by remember { mutableStateOf(false) }

    // Update slider when external state changes
    LaunchedEffect(servoStatus) {
        servoStatus?.servos?.get("s1")?.let {
            sliderPosition = it.toFloat()
        }
    }

    // Listen for servo events to show snackbars and update pending state
    LaunchedEffect(vm, snackbarHostState) {
        vm.servoEvents.collectLatest { event ->
            isPending = false
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Servo event: ${event.id} at ${event.position}° - ${event.status ?: "OK"}",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ConnectionStatusHeader(isConnected)
            }

            item {
                val currentAngle = servoStatus?.servos?.get("s1") ?: 0
                CurrentAngleCard(angle = currentAngle)
            }

            item {
                ControlSliderCard(
                    sliderPosition = sliderPosition,
                    onSliderChange = { sliderPosition = it },
                    onSetClick = {
                        isPending = true
                        vm.setAngle("s1", sliderPosition.toInt())
                    },
                    isEnabled = isConnected,
                    isPending = isPending
                )
            }

            item {
                QuickPresetsCard { angle ->
                    sliderPosition = angle.toFloat()
                    isPending = true
                    vm.setAngle("s1", angle)
                }
            }
        }
    }
}

@Composable
private fun ConnectionStatusHeader(isConnected: Boolean) {
    val color by animateColorAsState(targetValue = if (isConnected) Color(0xFF388E3C) else Color(0xFFFBC02D))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (isConnected) "Live Connection" else "Disconnected",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CurrentAngleCard(angle: Int) {
    val animatedAngle by animateFloatAsState(targetValue = angle.toFloat(), animationSpec = tween(500))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            Text("Current Servo Angle", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${animatedAngle.toInt()}°",
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ControlSliderCard(
    sliderPosition: Float,
    onSliderChange: (Float) -> Unit,
    onSetClick: () -> Unit,
    isEnabled: Boolean,
    isPending: Boolean
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Manual Control", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Slider(
                value = sliderPosition,
                onValueChange = onSliderChange,
                valueRange = 0f..180f,
                steps = 179,
                enabled = isEnabled
            )
            Text("${sliderPosition.toInt()}°", modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(Modifier.height(16.dp))
            Button(onClick = onSetClick, enabled = isEnabled && !isPending, modifier = Modifier.fillMaxWidth()) {
                if (isPending) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Set Angle")
                }
            }
        }
    }
}

@Composable
private fun QuickPresetsCard(onPresetClick: (Int) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Quick Presets", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { onPresetClick(0) }) { Text("Close (0°)") }}

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { onPresetClick(90) }) { Text("Neutral (90°)") }}

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { onPresetClick(180) }) { Text("Open (180°)") }
            }
        }
    }
}



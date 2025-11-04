package com.resq.app.ui.screens.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.resq.app.data.model.AiPrediction
import com.resq.app.data.model.Alert
import com.resq.app.data.model.AnalyticsSnapshot
import com.resq.app.data.util.AnalyticsFixture
import com.resq.app.ui.theme.ResQTheme
import com.resq.app.ui.viewmodel.AnalyticsControlViewModel
import com.resq.app.ui.viewmodel.ViewModelFactory
import kotlinx.serialization.json.Json

// --- Main Composable ---

@Composable
fun DashboardScreen(
    navController: NavController? = null, // Made nullable for preview convenience
    vm: AnalyticsControlViewModel = viewModel(factory = ViewModelFactory())
) {
    val analyticsState by vm.analyticsState.collectAsState()
    val isConnected by vm.isConnected.collectAsState()

    if (analyticsState == null) {
        // TODO: Implement a shimmering placeholder
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading System Overview...")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                StatusBanner(analyticsState!!, isConnected)
            }
            item {
                SensorGrid(analyticsState!!.sensors, navController)
            }
            item {
                AiDecisionCard(analyticsState!!.aiPrediction)
            }
            item {
                RecentAlertsCard(analyticsState!!.alerts ?: emptyList())
            }
        }
    }
}

// --- UI Components ---

@Composable
fun StatusBanner(snapshot: AnalyticsSnapshot, isConnected: Boolean) {
    val statusColor by animateColorAsState(
        targetValue = when (snapshot.aiPrediction.prediction.lowercase()) {
            "normal" -> Color(0xFF16A34A) // Success Green
            "fire", "earthquake", "gas" -> Color(0xFFDC2626) // Error Red
            else -> Color(0xFFF59E0B) // Warning Amber
        }
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp).clip(RoundedCornerShape(16.dp))) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("System Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                val connectionColor by animateColorAsState(targetValue = if (isConnected) Color(0xFF16A34A) else Color(0xFFF59E0B))
                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(connectionColor))
            }
            Text(
                text = "Last updated: ${snapshot.timestamp ?: "No data"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${snapshot.aiPrediction.prediction} • Confidence: ${snapshot.aiPrediction.confidence}% • ${snapshot.aiPrediction.decisionMethod ?: "N/A"}",
                style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun SensorGrid(sensors: com.resq.app.data.model.Sensors, navController: NavController?) {
    val sensorItems = listOf(
        SensorDisplay("Indoor Temp", sensors.it, "°C", Icons.Default.Thermostat),
        SensorDisplay("Gas", sensors.gas.toFloat(), "ppm", Icons.Default.Whatshot),
        SensorDisplay("RMS Accel", sensors.rmsAccel ?: 0f, "g", Icons.Default.Waves),
        SensorDisplay("Current", sensors.cur, "A", Icons.Default.FlashOn)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.height(260.dp) // Adjust height as needed
    ) {
        items(sensorItems) { item ->
            SensorCard(item, onClick = { navController?.navigate("analytics") })
        }
    }
}

@Composable
fun SensorCard(sensor: SensorDisplay, onClick: () -> Unit) {
    val valueColor = when(sensor.label) {
        "Gas" -> if(sensor.value > 1000) Color(0xFFDC2626) else if (sensor.value > 400) Color(0xFFF59E0B) else Color.Unspecified
        "RMS Accel" -> if(sensor.value > 3) Color(0xFFDC2626) else if (sensor.value > 1) Color(0xFFF59E0B) else Color.Unspecified
        else -> Color.Unspecified
    }

    Card(
        modifier = Modifier.clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(sensor.icon, contentDescription = sensor.label, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(sensor.label, style = MaterialTheme.typography.bodyMedium)
            Row(verticalAlignment = Alignment.Bottom) {
                Text("${sensor.value}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = valueColor)
                Text(sensor.unit, modifier = Modifier.padding(start = 2.dp, bottom = 4.dp), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

data class SensorDisplay(val label: String, val value: Float, val unit: String, val icon: ImageVector)

@Composable
fun AiDecisionCard(prediction: AiPrediction) {
    val confidence by animateFloatAsState(
        targetValue = prediction.confidence / 100f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing)
    )

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("AI Decision", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text(prediction.prediction, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { confidence },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape)
            )
            Text("Confidence: ${prediction.confidence}%", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))
            Text("Source: ${prediction.decisionMethod ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun RecentAlertsCard(alerts: List<Alert>) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Recent Alerts", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            if (alerts.isEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "No alerts", tint = Color(0xFF16A34A))
                    Spacer(Modifier.width(8.dp))
                    Text("No recent alerts", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                alerts.take(3).forEach { alert ->
                    AlertItem(alert)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun AlertItem(alert: Alert) {
    val (icon, color) = when {
        listOf("critical", "error", "spike").any { it in alert.message.lowercase() } -> Icons.Default.Warning to MaterialTheme.colorScheme.error
        "warning" in alert.message.lowercase() -> Icons.Default.Info to MaterialTheme.colorScheme.tertiary
        else -> Icons.Default.CheckCircle to Color(0xFF16A34A)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = "Alert", tint = color)
        Spacer(Modifier.width(8.dp))
        Column {
            Text(alert.message, fontWeight = FontWeight.SemiBold)
            Text(alert.timestamp, style = MaterialTheme.typography.bodySmall)
        }
    }
}

// --- Preview ---

@Preview(showBackground = true, name = "Dashboard Light Mode")
@Composable
fun DashboardScreenPreview() {
    val mockData = Json { ignoreUnknownKeys = true }.decodeFromString<AnalyticsSnapshot>(AnalyticsFixture.sampleJson)
    ResQTheme {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            DashboardScreen()
        }
    }
}

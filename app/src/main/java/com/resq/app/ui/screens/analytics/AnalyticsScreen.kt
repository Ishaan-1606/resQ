package com.resq.app.ui.screens.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.resq.app.data.model.AnalyticsSnapshot
import com.resq.app.data.model.Sensors
import com.resq.app.data.util.AnalyticsFixture
import kotlinx.serialization.json.Json

@Composable
fun AnalyticsScreen() {
    // For now, we use a mock snapshot parsed from our fixture.
    // Later, this will come from a ViewModel.
    val mockSnapshot = remember {
        Json { ignoreUnknownKeys = true }.decodeFromString<AnalyticsSnapshot>(AnalyticsFixture.sampleJson)
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Analytics Dashboard", style = MaterialTheme.typography.headlineSmall)
        }

        item {
            SensorSnapshotRow(sensors = mockSnapshot.sensors)
        }

        item {
            PredictionCard(prediction = mockSnapshot.aiPrediction)
        }

        item {
            mockSnapshot.hybridAnalysis?.let {
                HybridAnalysisCard(analysis = it)
            }
        }
    }
}

@Composable
private fun SensorSnapshotRow(sensors: Sensors) {
    Column {
        Text("Sensor Snapshot", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            item { SensorCard("Indoor Temp", "${sensors.it}°C") }
            item { SensorCard("Indoor Humidity", "${sensors.ih}%") }
            item { SensorCard("Gas Level", "${sensors.gas} ppm") }
            item { SensorCard("Current", "${sensors.cur} A") }
            item { SensorCard("RMS Accel", "${sensors.rmsAccel} g") }
        }
    }
}

@Composable
private fun PredictionCard(prediction: com.resq.app.data.model.AiPrediction) {
    AnalyticsCard(title = "AI Prediction Details") {
        InfoRow("Prediction", prediction.prediction)
        InfoRow("Confidence", "${String.format("%.2f", prediction.confidence)}%")
        InfoRow("Decision Method", prediction.decisionMethod ?: "N/A")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Probabilities:", fontWeight = FontWeight.SemiBold)
        prediction.probabilities.forEach { (label, prob) ->
            InfoRow("  - $label", "${String.format("%.2f", prob)}%")
        }
    }
}

@Composable
private fun HybridAnalysisCard(analysis: com.resq.app.data.model.HybridAnalysis) {
    AnalyticsCard(title = "Hybrid Analysis") {
        InfoRow("Rules Prediction", analysis.rulesPrediction)
        InfoRow("Rules Confidence", "${analysis.rulesConfidence}%")
        InfoRow("Combined Decision", analysis.combinedDecision)
        InfoRow("Explanation", analysis.explanation)
    }
}

// --- Reusable UI Components ---

@Composable
private fun AnalyticsCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SensorCard(name: String, value: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)) {
            Text(name, style = MaterialTheme.typography.bodySmall)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.width(8.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(androidx.compose.ui.Alignment.CenterVertically))
    }
    Spacer(modifier = Modifier.height(4.dp))
}

package com.resq.app.ui.screens.alerts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.resq.app.data.model.Alert
import com.resq.app.ui.screens.analytics.Severity
import com.resq.app.ui.screens.analytics.severityFromMessage
import com.resq.app.ui.theme.ResQTheme
import com.resq.app.ui.viewmodel.AnalyticsControlViewModel
import com.resq.app.ui.viewmodel.ViewModelFactory

private object AlertsScreenFixture {
    val alerts = listOf(
        Alert("Critical: Gas spike detected near kitchen", "2025-10-31T19:30:12Z"),
        Alert("Earthquake tremor detected (M4.3) — nearby", "2025-10-31T18:12:03Z"),
        Alert("Servo moved to safe position 90°", "2025-10-31T17:45:00Z")
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    navController: NavController? = null,
    vm: AnalyticsControlViewModel = viewModel(factory = ViewModelFactory())
) {
    val allAlerts by vm.analyticsState.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSeverity by remember { mutableStateOf<Severity?>(null) }

    if (showConfirmDialog) {
        ConfirmClearDialog(
            onConfirm = { vm.clearAlerts(); showConfirmDialog = false },
            onDismiss = { showConfirmDialog = false }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("System Alerts") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showConfirmDialog = true },
                modifier = Modifier.testTag("ClearAllFAB")
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Clear All Alerts")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchBar(searchQuery, onQueryChange = { searchQuery = it })
            FilterBar(selectedSeverity, onSeverityChange = { selectedSeverity = it })

            val filteredAlerts = remember(allAlerts, searchQuery, selectedSeverity) {
                (allAlerts?.alerts ?: emptyList())
                    .filter { alert ->
                        (searchQuery.isBlank() || alert.message.contains(searchQuery, ignoreCase = true)) &&
                                (selectedSeverity == null || severityFromMessage(alert.message) == selectedSeverity)
                    }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().testTag("AlertsList"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredAlerts, key = { it.timestamp }) { alert ->
                    var isExpanded by remember { mutableStateOf(false) }
                    AlertCard(
                        alert = alert,
                        expanded = isExpanded,
                        onExpandToggle = { isExpanded = !isExpanded },
                        modifier = Modifier.testTag("AlertCard_${alert.timestamp}")
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        label = { Text("Search Alerts") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                }
            }
        },
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBar(selected: Severity?, onSeverityChange: (Severity?) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Severity.values().forEach { severity ->
            ElevatedFilterChip(
                selected = selected == severity,
                onClick = { onSeverityChange(if (selected == severity) null else severity) },
                label = { Text(severity.name) }
            )
        }
    }
}

@Composable
fun AlertCard(alert: Alert, expanded: Boolean, onExpandToggle: () -> Unit, modifier: Modifier = Modifier) {
    val severity = severityFromMessage(alert.message)
    val animatedColor by animateColorAsState(targetValue = severity.color.copy(alpha = 0.1f))

    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onExpandToggle),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = animatedColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = severity.icon,
                contentDescription = severity.name,
                modifier = Modifier.size(40.dp).clip(CircleShape).background(severity.color).padding(8.dp),
                tint = Color.White
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(alert.message, fontWeight = FontWeight.Bold)
                Text(alert.timestamp, style = MaterialTheme.typography.bodySmall) // TODO: Convert to relative time
            }
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier.clip(CircleShape).background(severity.color).padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(severity.name, color = Color.White, style = MaterialTheme.typography.labelSmall)
            }
        }
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                Text("Full Timestamp: ${alert.timestamp}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun ConfirmClearDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Clear All Alerts?") },
        text = { Text("This action cannot be undone. Are you sure you want to delete all alerts?") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Confirm") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}



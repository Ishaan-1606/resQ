package com.resq.app.ui.screens.settings

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.resq.app.ui.theme.ResQTheme
import com.resq.app.ui.viewmodel.FakeSettingsViewModel
import com.resq.app.ui.viewmodel.SettingsViewModel

class SettingsViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FakeSettingsViewModel::class.java)) {
            return FakeSettingsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController? = null,
    vm: SettingsViewModel = viewModel(factory = SettingsViewModelFactory())
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showCacheDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        ConfirmDialog(
            title = "Logout",
            text = "Are you sure you want to log out?",
            onConfirm = { /* TODO: vm.logout() */ showLogoutDialog = false },
            onDismiss = { showLogoutDialog = false }
        )
    }
    if (showCacheDialog) {
        ConfirmDialog(
            title = "Clear Cache",
            text = "This will clear all locally cached data.",
            onConfirm = { /* TODO: vm.clearCache() */ showCacheDialog = false },
            onDismiss = { showCacheDialog = false }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding), contentPadding = PaddingValues(16.dp)) {
            item { AccountCard { showLogoutDialog = true } }            
            item { Spacer(Modifier.height(16.dp)) }
            item { ConnectivityCard(vm) }
            item { Spacer(Modifier.height(16.dp)) }
            item { NotificationsCard(vm) }
            item { Spacer(Modifier.height(16.dp)) }
            item { AppearanceCard(vm) }
            item { Spacer(Modifier.height(16.dp)) }
            item { UtilitiesCard(onClearCache = { showCacheDialog = true }) }
            item { Spacer(Modifier.height(16.dp)) }
            item { AboutCard() }
        }
    }
}

@Composable
private fun AccountCard(onLogoutClick: () -> Unit) {
    SettingsCard(title = "Account & Security") {
        Button(
            onClick = onLogoutClick,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth().testTag("LogoutButton")
        ) {
            Text("Logout")
        }
    }
}

@Composable
private fun ConnectivityCard(vm: SettingsViewModel) {
    val wifi by vm.wifiEnabled.collectAsState()
    val bluetooth by vm.bluetoothEnabled.collectAsState()
    SettingsCard(title = "Connectivity") {
        ToggleRow("Wi-Fi", wifi, { vm.toggleWifi(it) }, "WifiToggle")
        ToggleRow("Bluetooth", bluetooth, { vm.toggleBluetooth(it) }, "BluetoothToggle")
    }
}

@Composable
private fun NotificationsCard(vm: SettingsViewModel) {
    val notifications by vm.notificationsEnabled.collectAsState()
    SettingsCard(title = "Notifications & Permissions") {
        ToggleRow("App Notifications", notifications, { vm.toggleNotifications(it) }, "NotificationsToggle")
        Button(onClick = { vm.openPermissionsSettings() }, modifier = Modifier.fillMaxWidth()) {
            Text("Manage App Permissions")
        }
    }
}

@Composable
private fun AppearanceCard(vm: SettingsViewModel) {
    val isDark by vm.isDarkTheme.collectAsState()
    SettingsCard(title = "Appearance") {
        ToggleRow("Dark Theme", isDark, { vm.toggleTheme(it) }, "ThemeToggle")
    }
}

@Composable
private fun UtilitiesCard(onClearCache: () -> Unit) {
    SettingsCard(title = "Utilities") {
        Button(onClick = {}, modifier = Modifier.fillMaxWidth().testTag("CheckUpdatesButton")) {
            Text("Check for Updates")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onClearCache, modifier = Modifier.fillMaxWidth().testTag("ClearCacheButton")) {
            Text("Clear Cache")
        }
         Spacer(Modifier.height(8.dp))
        Button(onClick = {}, modifier = Modifier.fillMaxWidth().testTag("SendFeedbackButton")) {
            Text("Send Feedback")
        }
    }
}

@Composable
private fun AboutCard() {
    SettingsCard(title = "About") {
        Text("ResQ v1.0.0", style = MaterialTheme.typography.bodyMedium)
        Text("© 2024. All rights reserved.", style = MaterialTheme.typography.bodySmall)
    }
}

// --- Reusable Components ---

@Composable
private fun SettingsCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun ToggleRow(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit, testTag: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = isChecked, onCheckedChange = onCheckedChange, modifier = Modifier.testTag(testTag))
    }
}

@Composable
private fun ConfirmDialog(title: String, text: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Confirm") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    ResQTheme {
        SettingsScreen()
    }
}

package com.resq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Represents the status of an app update check.
 */
enum class UpdateStatus {
    IDLE,
    CHECKING,
    UP_TO_DATE,
    UPDATE_AVAILABLE,
    ERROR
}

/**
 * Defines the contract for the Settings screen's ViewModel.
 */
interface SettingsViewModel {
    val isConnected: StateFlow<Boolean>
    val wifiEnabled: StateFlow<Boolean>
    val bluetoothEnabled: StateFlow<Boolean>
    val mobileDataEnabled: StateFlow<Boolean>
    val notificationsEnabled: StateFlow<Boolean>
    val isDarkTheme: StateFlow<Boolean>

    fun toggleWifi(enabled: Boolean)
    fun toggleBluetooth(enabled: Boolean)
    fun toggleMobileData(enabled: Boolean)
    fun toggleNotifications(enabled: Boolean)
    fun toggleTheme(dark: Boolean)
    suspend fun logout(): Result<Unit>
    fun openPermissionsSettings()
    fun sendFeedback(message: String): Result<Unit>
    fun checkForUpdates(): Flow<UpdateStatus>
    fun clearCache(): Result<Unit>
}

/**
 * A fake implementation of the SettingsViewModel for UI previews and development.
 */
class FakeSettingsViewModel : ViewModel(), SettingsViewModel {
    override val isConnected = kotlinx.coroutines.flow.MutableStateFlow(true)
    override val wifiEnabled = kotlinx.coroutines.flow.MutableStateFlow(true)
    override val bluetoothEnabled = kotlinx.coroutines.flow.MutableStateFlow(false)
    override val mobileDataEnabled = kotlinx.coroutines.flow.MutableStateFlow(true)
    override val notificationsEnabled = kotlinx.coroutines.flow.MutableStateFlow(true)
    override val isDarkTheme = kotlinx.coroutines.flow.MutableStateFlow(false)

    override fun toggleWifi(enabled: Boolean) { wifiEnabled.value = enabled }
    override fun toggleBluetooth(enabled: Boolean) { bluetoothEnabled.value = enabled }
    override fun toggleMobileData(enabled: Boolean) { mobileDataEnabled.value = enabled }
    override fun toggleNotifications(enabled: Boolean) { notificationsEnabled.value = enabled }
    override fun toggleTheme(dark: Boolean) { isDarkTheme.value = dark }
    override suspend fun logout(): Result<Unit> = Result.success(Unit)
    override fun openPermissionsSettings() {}
    override fun sendFeedback(message: String): Result<Unit> = Result.success(Unit)
    override fun checkForUpdates(): Flow<UpdateStatus> = kotlinx.coroutines.flow.flowOf(UpdateStatus.UP_TO_DATE)
    override fun clearCache(): Result<Unit> = Result.success(Unit)
}

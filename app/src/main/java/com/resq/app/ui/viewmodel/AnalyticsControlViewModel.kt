package com.resq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resq.app.data.model.ServoSetRequest
import com.resq.app.data.repository.AnalyticsRepository
import com.resq.app.data.repository.ServoRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * NOTE: To switch to a production environment, the baseUrl in the network client
 * builder should be changed from the development placeholder.
 */
class AnalyticsControlViewModel(
    private val servoRepository: ServoRepository,
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    // --- Servo Control State ---
    val servoState = servoRepository.observeServoState()
    val servoEvents = servoRepository.observeServoEvents()

    // --- Analytics State ---
    val analyticsState = analyticsRepository.analyticsState
    val alerts = analyticsRepository.alerts

    // TODO: Implement a real connection status flow
    val isConnected: StateFlow<Boolean> = kotlinx.coroutines.flow.MutableStateFlow(true) // Default to true for UI development

    fun refreshStatus() {
        viewModelScope.launch {
            servoRepository.getServoStatus()
        }
    }

    fun setAngle(id: String, angle: Int) {
        viewModelScope.launch {
            val clampedAngle = angle.coerceIn(0, 180)
            val request = ServoSetRequest(id = id, position = clampedAngle)
            servoRepository.setServoAngle(request)
        }
    }

    /**
     * TODO: Implement backend logic for clearing alerts.
     */
    fun clearAlerts() {
        // This would call a method on the repository, e.g.,
        // viewModelScope.launch { analyticsRepository.clearAllAlerts() }
    }

    fun startRealtime() {
        servoRepository.startRealtime()
    }

    fun stopRealtime() {
        servoRepository.stopRealtime()
    }
}

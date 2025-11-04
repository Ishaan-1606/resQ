package com.resq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.resq.app.data.repository.FakeAnalyticsRepository
import com.resq.app.data.repository.FakeServoRepository

/**
 * A custom ViewModelProvider.Factory to manually construct ViewModels.
 * This allows us to inject dependencies, like repositories, into them.
 */
class ViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnalyticsControlViewModel::class.java)) {
            // In a real app, you would have a dependency injection framework (like Hilt)
            // or a service locator to provide the REAL repositories.
            // For now, we provide FAKE ones to prevent the app from crashing.
            val servoRepository = FakeServoRepository()
            val analyticsRepository = FakeAnalyticsRepository()
            return AnalyticsControlViewModel(servoRepository, analyticsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}

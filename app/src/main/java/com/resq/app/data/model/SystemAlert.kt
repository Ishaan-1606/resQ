package com.resq.app.data.model

import kotlinx.serialization.Serializable

/**
 * Represents a system-wide alert event.
 */
@Serializable
data class SystemAlert(
    val message: String,
    val timestamp: String,
    val severity: String = "Info" // e.g., Info, Warning, Critical
)

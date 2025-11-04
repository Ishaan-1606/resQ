package com.resq.app.ui.screens.analytics

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class Severity(val color: Color, val icon: ImageVector) {
    CRITICAL(Color(0xFFDC2626), Icons.Default.Warning),
    WARNING(Color(0xFFF59E0B), Icons.Default.NotificationImportant),
    INFO(Color(0xFF0EA5A0), Icons.Default.Info)
}

/**
 * Determines the severity of an alert based on keywords in its message.
 * This logic resides on the client as a fallback.
 */
fun severityFromMessage(message: String): Severity {
    val lowerMessage = message.lowercase()
    return when {
        listOf("spike", "leak", "fire", "critical", "danger", "earthquake", "tamper").any { it in lowerMessage } -> Severity.CRITICAL
        listOf("detected", "tremor", "high", "threshold").any { it in lowerMessage } -> Severity.WARNING
        else -> Severity.INFO
    }
}

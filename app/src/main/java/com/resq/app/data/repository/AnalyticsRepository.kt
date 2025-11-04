package com.resq.app.data.repository

import com.resq.app.data.model.AnalyticsSnapshot
import com.resq.app.data.model.SystemAlert
import com.resq.app.data.util.AnalyticsFixture
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json

interface AnalyticsRepository {
    val analyticsState: StateFlow<AnalyticsSnapshot?>
    val alerts: Flow<SystemAlert>
}

/**
 * A fake implementation that uses the fixture to provide mock data.
 */
class FakeAnalyticsRepository : AnalyticsRepository {
    private val json = Json { ignoreUnknownKeys = true }

    override val analyticsState = MutableStateFlow(
        json.decodeFromString<AnalyticsSnapshot>(AnalyticsFixture.sampleJson)
    )

    override val alerts = MutableSharedFlow<SystemAlert>()
}

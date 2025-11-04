package com.resq.app.data.repository

import com.resq.app.data.model.ServoAck
import com.resq.app.data.model.ServoPositionUpdate
import com.resq.app.data.model.ServoSetRequest
import com.resq.app.data.model.ServoStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A fake implementation of the ServoRepository used for UI previews and
 * to prevent crashes when the real dependencies are not available.
 */
class FakeServoRepository : ServoRepository {
    private val _servoState = MutableStateFlow<ServoStatus?>(null)
    private val _servoEvents = MutableSharedFlow<ServoPositionUpdate>()

    override fun observeServoState(): StateFlow<ServoStatus?> = _servoState
    override fun observeServoEvents(): SharedFlow<ServoPositionUpdate> = _servoEvents

    override suspend fun getServoStatus(): Result<ServoStatus> {
        // Return a failure to indicate that we are not connected.
        return Result.failure(NotImplementedError("Not connected to a real repository."))
    }

    override suspend fun setServoAngle(request: ServoSetRequest): Result<ServoAck> {
        // Return a failure to indicate that we are not connected.
        return Result.failure(NotImplementedError("Not connected to a real repository."))
    }

    override fun startRealtime() { /* Do nothing */ }
    override fun stopRealtime() { /* Do nothing */ }
}

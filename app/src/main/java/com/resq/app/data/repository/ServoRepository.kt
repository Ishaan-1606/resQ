package com.resq.app.data.repository

import com.resq.app.data.model.ServoAck
import com.resq.app.data.model.ServoPositionUpdate
import com.resq.app.data.model.ServoSetRequest
import com.resq.app.data.model.ServoStatus
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * NOTE: To switch to a production environment, the baseUrl in the network client
 * builder should be changed from the development placeholder.
 */
interface ServoRepository {

    /**
     * A hot flow representing the last known status of the servos.
     * It is null if no status has been fetched yet.
     */
    fun observeServoState(): StateFlow<ServoStatus?>

    /**
     * A hot flow for observing real-time events from the server, like position
     * updates or acknowledgements.
     */
    fun observeServoEvents(): SharedFlow<ServoPositionUpdate>

    /**
     * Fetches the latest servo status from the REST endpoint.
     * This is used to re-sync state on connection.
     */
    suspend fun getServoStatus(): Result<ServoStatus>

    /**
     * Sets the angle of a servo. It will prefer using the real-time socket connection
     * if available, otherwise it falls back to the REST endpoint.
     */
    suspend fun setServoAngle(request: ServoSetRequest): Result<ServoAck>

    /**
     * Starts the real-time connection to the server.
     */
    fun startRealtime()

    /**
     * Stops the real-time connection.
     */
    fun stopRealtime()
}

package com.resq.app.data.repository

import com.resq.app.data.model.ServoAck
import com.resq.app.data.model.ServoPositionUpdate
import com.resq.app.data.model.ServoSetRequest
import com.resq.app.data.model.ServoStatus
import com.resq.app.data.network.ServoApi
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

/**
 * NOTE: To switch to a production environment, the baseUrl in the network client
 * builder should be changed from the development placeholder.
 */
class ServoRepositoryImpl(
    private val servoApi: ServoApi,
    private val socket: Socket // TODO: Inject a configured Socket.IO client
) : ServoRepository {

    private val _servoState = MutableStateFlow<ServoStatus?>(null)
    private val _servoEvents = MutableSharedFlow<ServoPositionUpdate>()

    private var isRealtimeStarted = false

    override fun observeServoState(): StateFlow<ServoStatus?> = _servoState.asStateFlow()
    override fun observeServoEvents(): SharedFlow<ServoPositionUpdate> = _servoEvents.asSharedFlow()

    override suspend fun getServoStatus(): Result<ServoStatus> {
        return try {
            val response = servoApi.getServoStatus()
            if (response.isSuccessful && response.body() != null) {
                _servoState.value = response.body() // Update the state flow
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error fetching status: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setServoAngle(request: ServoSetRequest): Result<ServoAck> {
        // Prefer socket if connected
        if (socket.connected()) {
            val json = JSONObject().apply {
                put("position", request.position)
            }
            socket.emit("control_servo", json)
            // Return an optimistic pending result
            return Result.success(ServoAck("PENDING", request.id, request.position, "Sent via socket"))
        } else {
            // Fallback to REST API
            return try {
                val response = servoApi.setServoAngle(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error setting angle: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override fun startRealtime() {
        if (isRealtimeStarted) return
        isRealtimeStarted = true

        socket.on("servo_position_update") { args ->
            // TODO: Parse the incoming data and emit to flows
        }

        socket.on(Socket.EVENT_CONNECT) {
            // TODO: Resync data on reconnect
        }

        socket.connect()
    }

    override fun stopRealtime() {
        if (!isRealtimeStarted) return
        isRealtimeStarted = false
        socket.disconnect()
    }
}

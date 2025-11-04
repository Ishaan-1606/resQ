package com.resq.app.data.network

import com.resq.app.data.model.ServoAck
import com.resq.app.data.model.ServoSetRequest
import com.resq.app.data.model.ServoStatus
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * NOTE: To switch to a production environment, the baseUrl in the network client
 * builder should be changed from the development placeholder.
 */
interface ServoApi {

    /**
     * Fetches the current status of all servos.
     */
    @GET("api/servo/status")
    suspend fun getServoStatus(): Response<ServoStatus>

    /**
     * Sends a command to set the position of a specific servo.
     */
    @POST("api/servo/set")
    suspend fun setServoAngle(@Body request: ServoSetRequest): Response<ServoAck>

}

package com.resq.app.data.model

import kotlinx.serialization.Serializable

/**
 * NOTE: To switch to a production environment, the baseUrl in the network client
 * builder should be changed from the development placeholder.
 */

/**
 * Represents the response from GET /api/servo/status
 */
@Serializable
data class ServoStatus(
    val timestamp: String? = null,
    val servos: Map<String, Int>
)

/**
 * Represents the request body for POST /api/servo/set
 */
@Serializable
data class ServoSetRequest(
    val id: String,
    val position: Int
)

/**
 * Represents the acknowledgement response from POST /api/servo/set
 */
@Serializable
data class ServoAck(
    val status: String,
    val id: String,
    val position: Int,
    val message: String? = null
)

/**
 * Represents the data from a `servo_position_update` socket event.
 */
@Serializable
data class ServoPositionUpdate(
    val id: String,
    val position: Int,
    val status: String? = null,
    val timestamp: String? = null
)

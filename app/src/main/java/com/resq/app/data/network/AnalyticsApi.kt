package com.resq.app.data.network

import com.resq.app.data.model.AnalyticsSnapshot
import retrofit2.Response
import retrofit2.http.GET

/**
 * Retrofit interface for the analytics endpoints.
 */
interface AnalyticsApi {

    /**
     * Fetches the latest analytics snapshot from the server.
     *
     * @return A Response containing the AnalyticsSnapshot. The response may be successful
     * with an empty body (204 No Content), so the body is nullable.
     */
    @GET("api/analytics")
    suspend fun getLatestAnalytics(): Response<AnalyticsSnapshot>
}

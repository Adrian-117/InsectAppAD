package com.example.randominsect.data.api

import java.io.IOException

/**
 * Base sealed class for all Insect API related exceptions.
 */
sealed class InsectApiException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {
    /**
     * Thrown when the user has exhausted their daily/hourly request quota.
     * @param remainingMs Time in milliseconds until the next recharge point.
     */
    class QuotaExhaustedException(
        val remainingMs: Long,
        message: String = "Quota limit reached. Please wait until the next recharge.",
    ) : InsectApiException(message)

    /**
     * Thrown when the network request fails (e.g., no internet connection).
     */
    class NetworkException(
        cause: Throwable,
        message: String = "Failed to connect to insect service.",
    ) : InsectApiException(message, cause)

    /**
     * Thrown when no insect could be found or parsed from the API response.
     */
    class InsectNotFoundException(
        message: String = "No insect data found for this request.",
    ) : InsectApiException(message)
}

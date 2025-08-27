package com.thmanyah.task.domain.model

import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

sealed class UserFriendlyError(
    val title: String,
    val message: String,
    val actionText: String? = null
) {
    data object NetworkUnavailable : UserFriendlyError(
        title = "No Internet Connection",
        message = "Please check your internet connection and try again.",
        actionText = "Retry"
    )
    
    data object ServerUnavailable : UserFriendlyError(
        title = "Service Unavailable",
        message = "Our servers are temporarily unavailable. Please try again later.",
        actionText = "Retry"
    )
    
    data object RequestTimeout : UserFriendlyError(
        title = "Request Timeout",
        message = "The request is taking too long. Please check your connection and try again.",
        actionText = "Retry"
    )
    
    data object AuthenticationError : UserFriendlyError(
        title = "Authentication Failed",
        message = "Please check your credentials and try again.",
        actionText = "Retry"
    )
    
    data object NotFound : UserFriendlyError(
        title = "Content Not Found",
        message = "The requested content could not be found.",
        actionText = null
    )
    
    data object RateLimitExceeded : UserFriendlyError(
        title = "Too Many Requests",
        message = "Please wait a moment before trying again.",
        actionText = "Wait and Retry"
    )
    
    data class Generic(
        val originalMessage: String?
    ) : UserFriendlyError(
        title = "Something Went Wrong",
        message = originalMessage?.takeIf { it.isNotBlank() } 
            ?: "An unexpected error occurred. Please try again.",
        actionText = "Retry"
    )
}

fun Throwable.toUserFriendlyError(): UserFriendlyError {
    return when (this) {
        is UnknownHostException -> UserFriendlyError.NetworkUnavailable
        is ConnectException -> UserFriendlyError.NetworkUnavailable
        is SocketTimeoutException -> UserFriendlyError.RequestTimeout
        is SSLException -> UserFriendlyError.NetworkUnavailable
        is IOException -> {
            when {
                message?.contains("timeout", ignoreCase = true) == true -> 
                    UserFriendlyError.RequestTimeout
                message?.contains("network", ignoreCase = true) == true -> 
                    UserFriendlyError.NetworkUnavailable
                else -> UserFriendlyError.Generic(message)
            }
        }
        else -> {
            val message = this.message
            when {
                message?.contains("401", ignoreCase = true) == true ||
                message?.contains("unauthorized", ignoreCase = true) == true -> 
                    UserFriendlyError.AuthenticationError
                    
                message?.contains("404", ignoreCase = true) == true ||
                message?.contains("not found", ignoreCase = true) == true -> 
                    UserFriendlyError.NotFound
                    
                message?.contains("429", ignoreCase = true) == true ||
                message?.contains("rate limit", ignoreCase = true) == true -> 
                    UserFriendlyError.RateLimitExceeded
                    
                message?.contains("500", ignoreCase = true) == true ||
                message?.contains("502", ignoreCase = true) == true ||
                message?.contains("503", ignoreCase = true) == true -> 
                    UserFriendlyError.ServerUnavailable
                    
                else -> UserFriendlyError.Generic(message)
            }
        }
    }
}

val Result.Error.userFriendlyError: UserFriendlyError
    get() = exception.toUserFriendlyError()

fun String?.toDisplayErrorMessage(): String {
    if (this.isNullOrBlank()) return "An unknown error occurred"
    
    return when {
        contains("network", ignoreCase = true) -> "Network error. Please check your connection."
        contains("timeout", ignoreCase = true) -> "Request timed out. Please try again."
        contains("server", ignoreCase = true) -> "Server error. Please try again later."
        contains("authentication", ignoreCase = true) -> "Authentication failed. Please try again."
        contains("not found", ignoreCase = true) -> "Content not found."
        length > 100 -> take(97) + "..."
        else -> this
    }
}
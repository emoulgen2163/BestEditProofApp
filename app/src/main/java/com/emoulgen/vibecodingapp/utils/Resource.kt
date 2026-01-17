package com.emoulgen.vibecodingapp.utils
/**
 * A generic wrapper class for handling different states of data from API or local storage
 * Represents Success, Error, Loading, and Idle states
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    /**
     * Represents a successful operation with data
     */
    class Success<T>(data: T) : Resource<T>(data)

    /**
     * Represents an error state with optional data and error message
     */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    /**
     * Represents a loading state
     */
    class Loading<T>(data: T? = null) : Resource<T>(data)

    /**
     * Represents idle/initial state
     */
    class Idle<T> : Resource<T>()
}

// Extension functions for easier state handling in Compose

/**
 * Returns true if the resource is in Success state
 */
fun <T> Resource<T>.isSuccess(): Boolean = this is Resource.Success

/**
 * Returns true if the resource is in Error state
 */
fun <T> Resource<T>.isError(): Boolean = this is Resource.Error

/**
 * Returns true if the resource is in Loading state
 */
fun <T> Resource<T>.isLoading(): Boolean = this is Resource.Loading

/**
 * Returns true if the resource is in Idle state
 */
fun <T> Resource<T>.isIdle(): Boolean = this is Resource.Idle

/**
 * Execute action when resource is in Success state
 */
inline fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> {
    if (this is Resource.Success && data != null) {
        action(data)
    }
    return this
}

/**
 * Execute action when resource is in Error state
 */
inline fun <T> Resource<T>.onError(action: (String) -> Unit): Resource<T> {
    if (this is Resource.Error) {
        action(message ?: "Unknown error")
    }
    return this
}

/**
 * Execute action when resource is in Loading state
 */
inline fun <T> Resource<T>.onLoading(action: () -> Unit): Resource<T> {
    if (this is Resource.Loading) {
        action()
    }
    return this
}
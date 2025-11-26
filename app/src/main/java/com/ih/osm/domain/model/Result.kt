package com.ih.osm.domain.model

/**
 * A sealed class representing the result of an operation
 *
 * This wrapper provides type-safe error handling and loading states for domain operations.
 * Use this for all UseCase return types to ensure consistent error handling across the app.
 *
 * @param T The type of data wrapped in the result
 */
sealed class Result<out T> {
    /**
     * Represents a successful operation with data
     *
     * @param data The successful result data
     */
    data class Success<T>(
        val data: T,
    ) : Result<T>()

    /**
     * Represents a failed operation with error information
     *
     * @param message User-friendly error message
     * @param throwable Optional throwable for debugging
     */
    data class Error(
        val message: String,
        val throwable: Throwable? = null,
    ) : Result<Nothing>()

    /**
     * Represents an operation in progress
     */
    data object Loading : Result<Nothing>()

    /**
     * Returns true if this is a Success result
     */
    fun isSuccess(): Boolean = this is Success

    /**
     * Returns true if this is an Error result
     */
    fun isError(): Boolean = this is Error

    /**
     * Returns true if this is a Loading result
     */
    fun isLoading(): Boolean = this is Loading

    /**
     * Returns the data if Success, null otherwise
     */
    fun getOrNull(): T? = if (this is Success) data else null

    /**
     * Returns the data if Success, or the default value if Error or Loading
     */
    fun getOrDefault(defaultValue: @UnsafeVariance T): T = if (this is Success) data else defaultValue

    /**
     * Maps the success value to another type using the given transform function
     */
    inline fun <R> map(transform: (T) -> R): Result<R> =
        when (this) {
            is Success -> Success(transform(data))
            is Error -> this
            is Loading -> this
        }

    /**
     * Executes the given action if this is a Success result
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Executes the given action if this is an Error result
     */
    inline fun onError(action: (String, Throwable?) -> Unit): Result<T> {
        if (this is Error) action(message, throwable)
        return this
    }

    /**
     * Executes the given action if this is a Loading result
     */
    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }
}

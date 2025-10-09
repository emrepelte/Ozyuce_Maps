package com.ozyuce.maps.core.common.result

sealed class OzyuceResult<out T> {
    data class Success<T>(val data: T) : OzyuceResult<T>()
    data class Error(val exception: Throwable) : OzyuceResult<Nothing>()
    data object Loading : OzyuceResult<Nothing>()

    val isSuccess get() = this is Success
    val isError get() = this is Error
    val isLoading get() = this is Loading

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        is Loading -> throw IllegalStateException("Result is Loading")
    }

    suspend fun <R> fold(
        onSuccess: suspend (T) -> R,
        onError: suspend (Throwable) -> R,
        onLoading: suspend () -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Error -> onError(exception)
        is Loading -> onLoading()
    }

    companion object {
        fun <T> success(data: T) = Success(data)
        fun error(exception: Throwable) = Error(exception)
        fun loading() = Loading
    }
}

inline fun <T> OzyuceResult<T>.onSuccess(action: (T) -> Unit): OzyuceResult<T> {
    if (this is OzyuceResult.Success) action(data)
    return this
}

inline fun <T> OzyuceResult<T>.onError(action: (Throwable) -> Unit): OzyuceResult<T> {
    if (this is OzyuceResult.Error) action(exception)
    return this
}

inline fun <T> OzyuceResult<T>.onLoading(action: () -> Unit): OzyuceResult<T> {
    if (this is OzyuceResult.Loading) action()
    return this
}

fun <T> OzyuceResult<T>.getOrDefault(defaultValue: T): T = when (this) {
    is OzyuceResult.Success -> data
    else -> defaultValue
}

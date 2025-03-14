package com.utsman.composeremote.router

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow

sealed class ResultLayout<out T> {
    data object Loading : ResultLayout<Nothing>()

    data class Success<T>(val data: T) : ResultLayout<T>()

    data class Failure(
        val error: Throwable,
        val message: String = error.message
            ?: "Unknown error",
    ) : ResultLayout<Nothing>()

    val isLoading: Boolean get() = this is Loading

    val isSuccess: Boolean get() = this is Success

    val isFailure: Boolean get() = this is Failure

    fun getOrNull(): T? = if (this is Success) data else null

    fun <R> map(transform: (T) -> R): ResultLayout<R> = when (this) {
        is Loading -> Loading
        is Success -> Success(transform(data))
        is Failure -> this
    }

    fun <R> fold(
        onLoading: () -> R,
        onSuccess: (T) -> R,
        onFailure: (Throwable, String) -> R,
    ): R = when (this) {
        is Loading -> onLoading()
        is Success -> onSuccess(data)
        is Failure -> onFailure(error, message)
    }

    @Composable
    fun <R> foldCompose(
        onLoading: @Composable () -> R,
        onSuccess: @Composable (T) -> R,
        onFailure: @Composable (Throwable, String) -> R,
    ): R = when (this) {
        is Loading -> onLoading()
        is Success -> onSuccess(data)
        is Failure -> onFailure(error, message)
    }

    companion object {
        fun <T> success(data: T): ResultLayout<T> = Success(data)

        fun failure(
            error: Throwable,
            message: String = error.message
                ?: "Unknown error",
        ): ResultLayout<Nothing> = Failure(error, message)

        fun loading(): ResultLayout<Nothing> = Loading

        fun <T> flow(block: suspend () -> T): Flow<ResultLayout<T>> = kotlinx.coroutines.flow.flow {
            emit(Loading)
            try {
                emit(Success(block()))
            } catch (e: Exception) {
                emit(Failure(e))
            }
        }
    }
}

fun <T> Result<T>.toResultLayout(): ResultLayout<T> = fold(
    onSuccess = { ResultLayout.success(it) },
    onFailure = { ResultLayout.failure(it) },
)

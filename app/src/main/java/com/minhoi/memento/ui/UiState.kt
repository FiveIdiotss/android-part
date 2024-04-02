package com.minhoi.memento.ui

sealed class UiState<out T> {
    data class Success<T>(val data: T): UiState<T>()
    data class Error(val error: Throwable?): UiState<Nothing>()
    object Empty: UiState<Nothing>()
    object Loading: UiState<Nothing>()
}

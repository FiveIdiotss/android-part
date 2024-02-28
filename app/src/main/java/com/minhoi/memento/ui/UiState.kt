package com.minhoi.memento.ui

sealed class UiState {
    data class Success<T>(val data: T): UiState()
    data class Error(val error: Throwable?): UiState()
    object Empty: UiState()
    object Loading: UiState()
}

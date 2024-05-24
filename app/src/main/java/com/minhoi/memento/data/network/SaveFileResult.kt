package com.minhoi.memento.data.network

sealed class SaveFileResult {
    object Success : SaveFileResult()
    data class Failure(val error: Throwable) : SaveFileResult()
}
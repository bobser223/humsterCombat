package com.example.cryptoscam.domain.model

sealed interface SyncResult {

    data object Success : SyncResult

    data object NetworkError : SyncResult

    data object ServerError : SyncResult

    data object RateLimited : SyncResult

    data object AuthError : SyncResult

    data object InvalidResponse : SyncResult
}
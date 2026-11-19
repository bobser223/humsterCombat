package com.example.cryptoscam.domain.model

sealed interface SyncStatus {
    data object Idle : SyncStatus
    data object Waiting : SyncStatus
    data object Updating : SyncStatus
    data class UpdateSuccess(
        val time: String,
        val message: String?
    ) : SyncStatus
    data class UpdateError(
        val message: String?
    ) : SyncStatus
}

fun SyncStatus.toMessage(): String {
    return when (val s = this) {
        SyncStatus.Idle ->
            "Оновіть, щоб побачити курси"
        SyncStatus.Waiting ->
            "Очікування мережі"
        SyncStatus.Updating ->
            "Оновлення"
        is SyncStatus.UpdateError ->
            "Помилка: ${s.message}"
        is SyncStatus.UpdateSuccess ->
            "Оновлено (${s.time})"
    }
}
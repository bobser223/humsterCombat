package com.example.cryptoscam.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.WorkInfo
import com.example.cryptoscam.CryptoApp
import com.example.cryptoscam.domain.model.CryptoAsset
import com.example.cryptoscam.domain.model.SyncStatus
import com.example.cryptoscam.domain.usecase.ObserveCryptoAssetsUseCase
import com.example.cryptoscam.domain.usecase.ObserveLastUpdatedUseCase
import com.example.cryptoscam.worker.SyncScheduler
import com.example.cryptoscam.worker.SyncWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CryptoListViewModel(
    observeCryptoAssets: ObserveCryptoAssetsUseCase,
    observeLastUpdated: ObserveLastUpdatedUseCase,
    private val syncScheduler: SyncScheduler
) : ViewModel() {

    init {
        refresh()
    }

    val assets: StateFlow<List<CryptoAsset>> =
        observeCryptoAssets()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    val lastUpdated: StateFlow<Long?> =
        observeLastUpdated()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )

    // Стан ручного оновлення, взятий з WorkManager
    val syncStatus: StateFlow<SyncStatus> =
        combine(syncScheduler.observeManualSync(), observeLastUpdated()) { workInfos, lastUpdated ->
            workInfos.firstOrNull().toSyncStatus(lastUpdated)
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                SyncStatus.Idle
            )

    fun refresh() = syncScheduler.syncNow()

    private fun WorkInfo?.toSyncStatus(lastUpdated: Long?): SyncStatus {
        if (this == null) return SyncStatus.Idle
        return when (state) {
            // ENQUEUED: чекає на мережу або на свою чергу
            WorkInfo.State.ENQUEUED,
            WorkInfo.State.BLOCKED -> SyncStatus.Waiting

            WorkInfo.State.RUNNING -> SyncStatus.Updating

            WorkInfo.State.SUCCEEDED -> SyncStatus.UpdateSuccess(
                time = lastUpdated?.let { formatTime(it) } ?: "",
                message = null
            )

            WorkInfo.State.FAILED -> SyncStatus.UpdateError(
                message = outputData.getString(SyncWorker.KEY_ERROR)
            )

            WorkInfo.State.CANCELLED -> SyncStatus.Idle
        }
    }

    private fun formatTime(millis: Long): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(millis))

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as CryptoApp).container
                CryptoListViewModel(
                    container.observeCryptoAssets,
                    container.observeLastUpdated,
                    container.syncScheduler
                )
            }
        }
    }
}

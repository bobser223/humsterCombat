package com.example.cryptoscam.domain.usecase

import com.example.cryptoscam.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class ObserveLastUpdatedUseCase(
    private val repository: CryptoRepository
) {
    operator fun invoke(): Flow<Long?> = repository.observeLastUpdated()
}
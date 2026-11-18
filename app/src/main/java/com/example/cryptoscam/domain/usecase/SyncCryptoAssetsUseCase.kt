package com.example.cryptoscam.domain.usecase
import com.example.cryptoscam.domain.model.SyncResult
import com.example.cryptoscam.domain.repository.CryptoRepository

class SyncCryptoAssetsUseCase(
    private val repository: CryptoRepository
) {
    suspend operator fun invoke(): SyncResult = repository.sync()
}
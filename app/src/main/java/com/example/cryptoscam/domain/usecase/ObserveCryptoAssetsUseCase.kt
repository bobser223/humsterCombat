package com.example.cryptoscam.domain.usecase

import com.example.cryptoscam.domain.model.CryptoAsset
import com.example.cryptoscam.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class ObserveCryptoAssetsUseCase(
    private val repository: CryptoRepository
) {
    operator fun invoke(): Flow<List<CryptoAsset>> = repository.observeAssets()
}
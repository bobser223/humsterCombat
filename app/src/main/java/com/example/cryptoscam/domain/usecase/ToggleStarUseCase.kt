package com.example.cryptoscam.domain.usecase

import com.example.cryptoscam.domain.repository.CryptoRepository

class ToggleStarUseCase(
    private val repository: CryptoRepository
) {
    suspend operator fun invoke(id: String) = repository.toggleStar(id)
}

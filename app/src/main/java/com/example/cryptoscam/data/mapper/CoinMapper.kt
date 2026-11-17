package com.example.cryptoscam.data.mapper

import com.example.cryptoscam.data.local.CryptoExchangeEntity
import com.example.cryptoscam.data.remote.dto.CoinDto


fun CoinDto.toEntity(rank: Int, now: Long): CryptoExchangeEntity? {
    val safeId = id?.takeIf { it.isNotBlank() } ?: return null
    val safeSymbol = (currencyAbbreviation?.takeIf { it.isNotBlank() } ?: safeId)
        .uppercase()
    val safeName = currency?.takeIf { it.isNotBlank() } ?: safeSymbol
    val safeLogoUrl = logoUrl?.takeIf { it.isNotBlank() }
    return CryptoExchangeEntity(
            id = safeId,
            currency = safeName,
            currencyAbbreviation = safeSymbol,
            logoUrl = safeLogoUrl,
            priceUsd = priceUsd,
            rank = rank,
            updatedAt = now
    )
}

fun List<CoinDto>.toEntities(now: Long): List<CryptoExchangeEntity> =
    mapIndexedNotNull { index, dto ->
        dto.toEntity(rank = index + 1, now = now)
    }
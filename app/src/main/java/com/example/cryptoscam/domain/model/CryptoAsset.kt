package com.example.cryptoscam.domain.model


// class for UI
data class CryptoAsset(
    val id: String,
    val currency: String,
    val currencyAbbreviation: String,
    val logoUrl: String?, // null -> placeholder
    val priceUsd: Double?, // null -> "Немає даних"
    val rank: Int,
    val updatedAt: Long,
    val marketCapUsd: Double?,
    val athUsd: Double?,
    val athDate: Long?, // epoch millis
    val isStarred: Boolean
)
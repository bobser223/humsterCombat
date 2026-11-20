package com.example.cryptoscam.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "crypto_assets")
data class CryptoExchangeEntity(
    @PrimaryKey val id: String,
    val currency: String, // name in the API
    val currencyAbbreviation: String, // symbol in  API
    val logoUrl: String?,
    val priceUsd: Double?,
    val rank: Int,
    val updatedAt: Long,
    val marketCapUsd: Double?,
    val athUsd: Double?, // all-time high price
    val athDate: Long? // epoch millis, parsed from ISO 8601 in the API
)
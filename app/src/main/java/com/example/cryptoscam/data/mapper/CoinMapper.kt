package com.example.cryptoscam.data.mapper

import com.example.cryptoscam.data.local.CryptoExchangeEntity
import com.example.cryptoscam.data.remote.dto.CoinDto
import com.example.cryptoscam.domain.model.CryptoAsset
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

private val ISO_PATTERNS = listOf("yyyy-MM-dd'T'HH:mm:ss.SSSX", "yyyy-MM-dd'T'HH:mm:ssX")

private fun parseIsoMillis(value: String?): Long? {
    if (value.isNullOrBlank()) return null
    return ISO_PATTERNS.firstNotNullOfOrNull { pattern ->
        try {
            SimpleDateFormat(pattern, Locale.US).parse(value)?.time
        } catch (e: ParseException) {
            null
        }
    }
}

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
            updatedAt = now,
            marketCapUsd = marketCapUsd,
            athUsd = athUsd,
            athDate = parseIsoMillis(athDate)
    )
}



fun CryptoExchangeEntity.toDomain(isStarred: Boolean = false): CryptoAsset = CryptoAsset(
    id = id,
    currency = currency,
    currencyAbbreviation = currencyAbbreviation,
    logoUrl = logoUrl,
    priceUsd = priceUsd,
    rank = rank,
    updatedAt = updatedAt,
    marketCapUsd = marketCapUsd,
    athUsd = athUsd,
    athDate = athDate,
    isStarred = isStarred,
)

fun List<CoinDto>.toEntities(now: Long): List<CryptoExchangeEntity> =
    mapIndexedNotNull { index, dto ->
        dto.toEntity(rank = index + 1, now = now)
    }
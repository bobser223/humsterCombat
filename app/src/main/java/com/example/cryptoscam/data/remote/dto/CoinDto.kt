package com.example.cryptoscam.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CoinDto(
    @SerializedName("id") val id: String?,
    @SerializedName("symbol") val currencyAbbreviation: String?,
    @SerializedName("name") val currency: String?,
    @SerializedName("image") val logoUrl: String?,
    @SerializedName("current_price") val priceUsd: Double?,
    @SerializedName("market_cap_rank") val rank: Int?
)
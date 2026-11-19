package com.example.cryptoscam.presentation.format

import java.math.BigDecimal
import java.math.MathContext
import java.util.Locale

private const val SIGNIFICANT_DIGITS = 4

fun formatPriceUsd(price: Double): String {
    val rounded = BigDecimal(price)
        .round(MathContext(SIGNIFICANT_DIGITS))
        .stripTrailingZeros()
    if (rounded.signum() == 0 || rounded.abs() >= BigDecimal.ONE) {
        return "$" + String.format(Locale.US, "%,.2f", price)
    }
    return "$" + String.format(Locale.US, "%,.${rounded.scale()}f", rounded)
}

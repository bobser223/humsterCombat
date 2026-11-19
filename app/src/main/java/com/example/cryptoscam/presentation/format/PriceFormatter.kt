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

fun formatCapitalization(price: Double): String {
    if (price > 1e12) {
        val trillions = price / 1e12
        return String.format(Locale.US ,"$%.2fT", trillions)
    } else if (price > 1e9) {
        val billions = price / 1e9
        return String.format(Locale.US, "$%.2fB", billions)
    } else if (price > 1e6) {
        val millions = price / 1e6
        return String.format(Locale.US, "$%.2fM", millions)
    } else if (price > 1e3) {
        val thousands = price / 1e3
        return String.format(Locale.US, "$%.2fK", thousands)
    } else {
        return String.format(Locale.US, "$%.2f", price)
    }
}

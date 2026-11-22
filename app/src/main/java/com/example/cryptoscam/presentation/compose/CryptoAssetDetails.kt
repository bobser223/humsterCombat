package com.example.cryptoscam.presentation.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cryptoscam.domain.model.CryptoAsset
import com.example.cryptoscam.presentation.format.formatCapitalization
import com.example.cryptoscam.presentation.format.formatPriceUsd
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CryptoAssetDetails(asset: CryptoAsset) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),

        modifier = Modifier.fillMaxWidth().padding(start = 56.dp, end = 16.dp, bottom = 12.dp)
    ) {
        val athDateText = if (asset.athDate != null)
            formatDate(asset.athDate)
        else "Немає даних"
        val athPrice = if (asset.athUsd != null)
            formatPriceUsd(asset.athUsd)
        else "Немає даних"
        val capitalizationText = if (asset.marketCapUsd != null)
            formatCapitalization(asset.marketCapUsd)
        else "Немає даних"
        DetailLine("Назва", asset.currency)
        DetailLine("Капіталізація", capitalizationText)
        DetailLine("Найвища ціна", "$athPrice, $athDateText")
        DetailLine("Оновлено", formatDateTime(asset.updatedAt))
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.weight(1f))
        Text(value)
    }
}

private fun formatDateTime(millis: Long): String =
    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(millis))

private fun formatDate(millis: Long): String =
    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(millis))


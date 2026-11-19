package com.example.cryptoscam.presentation.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.cryptoscam.domain.model.CryptoAsset
import com.example.cryptoscam.presentation.format.formatPriceUsd

@Composable
fun CryptoAssetList(
    assets: List<CryptoAsset>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        items(assets, key = { it.id }) { asset ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text("${asset.rank}", modifier = Modifier.width(28.dp))
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(asset.logoUrl)
                        .crossfade(true)
                        .build(),
                    placeholder = rememberVectorPainter(Icons.Filled.MonetizationOn),
                    error = rememberVectorPainter(Icons.Filled.MonetizationOn),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                    )
                Text(asset.currencyAbbreviation)
                Spacer(Modifier.weight(1f))
                Text(asset.priceUsd?.let { formatPriceUsd(it) } ?: "Немає даних")
            }
            HorizontalDivider()
        }
    }
}
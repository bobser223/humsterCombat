package com.example.cryptoscam.presentation.compose

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import com.example.cryptoscam.domain.model.CryptoAsset

@Composable
fun CryptoAssetList(
    assets: List<CryptoAsset>,
    onStarClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        items(assets, key = { it.id }) { asset ->
            CryptoAssetRow(
                asset = asset,
                onStarClick = { onStarClick(asset.id) }
            )
            HorizontalDivider()
        }
    }
}
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
    expandedIds: Set<String>,
    onToggleExpand: (String) -> Unit,
    onStarClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        items(assets, key = { it.id }) { asset ->
            CryptoAssetRow(
                asset = asset,
                expanded = asset.id in expandedIds,
                onToggleExpand = { onToggleExpand(asset.id) },
                onStarClick = { onStarClick(asset.id) }
            )
            HorizontalDivider()
        }
    }
}
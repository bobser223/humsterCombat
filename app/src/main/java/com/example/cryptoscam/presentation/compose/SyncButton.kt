package com.example.cryptoscam.presentation.compose

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cryptoscam.domain.model.SyncStatus

@Composable
fun SyncButton(
    status: SyncStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val syncing = status is SyncStatus.Waiting || status is SyncStatus.Updating

    IconButton(
        onClick = onClick,
        enabled = !syncing,
        modifier = modifier
    ) {
        if (syncing) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Sync,
                contentDescription = "Оновити"
            )
        }
    }
}

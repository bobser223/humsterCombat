package com.example.cryptoscam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cryptoscam.domain.model.SyncStatus
import com.example.cryptoscam.domain.model.toMessage
import com.example.cryptoscam.presentation.compose.AssetSearchField
import com.example.cryptoscam.presentation.compose.CryptoAssetList
import com.example.cryptoscam.presentation.compose.StarredFilter
import com.example.cryptoscam.presentation.compose.StatusMessage
import com.example.cryptoscam.presentation.compose.SyncButton
import com.example.cryptoscam.presentation.viewmodel.CryptoListViewModel
import com.example.cryptoscam.ui.theme.CryptoSCAMTheme
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalFocusManager

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoSCAMTheme {
                val viewModel: CryptoListViewModel =
                    viewModel(factory = CryptoListViewModel.Factory)
                val assets by viewModel.assets.collectAsStateWithLifecycle()
                val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row {
                                    Text("CryptoSCAM")
                                }
                            },
                            actions = {
                                Text(
                                    text = syncStatus.toMessage(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (syncStatus is SyncStatus.UpdateError)
                                        MaterialTheme.colorScheme.error
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                SyncButton(
                                    status = syncStatus,
                                    onClick = { viewModel.refresh() }
                                )
                            }
                        )
                    }
                ) { innerPadding ->
                    val query by viewModel.query.collectAsStateWithLifecycle()
                    val starredOnly by viewModel.starredOnly.collectAsStateWithLifecycle()
                    val expandedIds by viewModel.expandedIds.collectAsStateWithLifecycle()
                    val focusManager = LocalFocusManager.current

                    val hasVisibleExpanded = assets.any { it.id in expandedIds }
                    val hasFilters = query.isNotEmpty() || starredOnly

                    BackHandler(enabled = hasVisibleExpanded || hasFilters) {
                        if (hasVisibleExpanded) {
                            viewModel.collapseAll()          // 1-й пріоритет: картки
                        } else {
                            viewModel.resetFilters()         // 2-й пріоритет: пошук і фільтр
                            focusManager.clearFocus()
                        }
                    }


                    Column(Modifier.padding(innerPadding)) {
                        AssetSearchField(query, viewModel::onQueryChanged)
                        StarredFilter(starredOnly, viewModel::onStarredOnlyChanged)

                        if (assets.isEmpty() && (query.isNotBlank() || starredOnly)) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                StatusMessage(
                                    text = if (query.isNotBlank())
                                        "За запитом «$query» нічого не знайдено"
                                    else
                                        "Ще немає обраних монет",
                                    modifier = Modifier
                                )
                            }
                        } else {
                            CryptoAssetList(
                                assets = assets,
                                expandedIds = expandedIds,
                                onToggleExpand = viewModel::onToggleExpanded,
                                onStarClick = viewModel::onStarClicked,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

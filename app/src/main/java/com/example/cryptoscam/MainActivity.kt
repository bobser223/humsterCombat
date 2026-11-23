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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.launch

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
                    val lastUpdated by viewModel.lastUpdated.collectAsStateWithLifecycle()
                    val focusManager = LocalFocusManager.current

                    val hasVisibleExpanded = assets.any { it.id in expandedIds }
                    val hasFilters = query.isNotEmpty() || starredOnly

                    val listState = rememberLazyListState()
                    val scope = rememberCoroutineScope()

                    val isScrolled by remember {
                        derivedStateOf {
                            listState.firstVisibleItemIndex > 0 ||
                                    listState.firstVisibleItemScrollOffset > 0
                        }
                    }


                    BackHandler(enabled = hasVisibleExpanded || hasFilters || isScrolled) {
                        when {
                            hasVisibleExpanded -> viewModel.collapseAll() // перший пріоритет - закрити розгорнуті
                            hasFilters -> { // другий пріоритет - скинути фільтр
                                viewModel.resetFilters()
                                focusManager.clearFocus()
                            }
                            else -> scope.launch { listState.animateScrollToItem(0) } // третій пріоритет - повернутися до гори списку
                        }
                    }


                    Column(Modifier.padding(innerPadding)) {
                        AssetSearchField(query, viewModel::onQueryChanged)
                        StarredFilter(starredOnly, viewModel::onStarredOnlyChanged)

                        // Порожній стан залежить від наявності даних у Room, а не від статусу WorkManager
                        if (lastUpdated == null) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                StatusMessage(
                                    text = "Ще немає збережених даних"
                                )
                            }
                        } else if (assets.isEmpty() && (query.isNotBlank() || starredOnly)) {
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
                                listState = listState,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

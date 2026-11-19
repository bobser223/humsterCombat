package com.example.cryptoscam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cryptoscam.domain.model.SyncStatus
import com.example.cryptoscam.domain.model.toMessage
import com.example.cryptoscam.presentation.compose.CryptoAssetList
import com.example.cryptoscam.presentation.compose.SyncButton
import com.example.cryptoscam.presentation.viewmodel.CryptoListViewModel
import com.example.cryptoscam.ui.theme.CryptoSCAMTheme

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
                    CryptoAssetList(
                        assets = assets,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

package com.coinoverlay.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coinoverlay.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    hasOverlayPermission: Boolean,
    onRequestOverlayPermission: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToOverlaySettings: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val isOverlayActive by viewModel.isOverlayActive.collectAsState()
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "CoinOverlay") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!hasOverlayPermission) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Overlay permission is required to show floating coin prices over other apps.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(
                            onClick = onRequestOverlayPermission,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Grant Overlay Permission")
                        }
                    }
                }
            }

            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Overlay Status", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = if (isOverlayActive) "Running" else "Stopped",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Tracked coins: ${settings.selectedCoins.joinToString()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    HorizontalDivider()
                    if (isOverlayActive) {
                        OutlinedButton(
                            onClick = { viewModel.stopOverlay() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Stop Overlay")
                        }
                    } else {
                        Button(
                            onClick = {
                                if (hasOverlayPermission) {
                                    viewModel.startOverlay()
                                } else {
                                    onRequestOverlayPermission()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Start Overlay")
                        }
                    }
                }
            }

            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Navigation", style = MaterialTheme.typography.titleLarge)

                    Button(
                        onClick = onNavigateToSearch,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Manage Coins")
                    }

                    OutlinedButton(
                        onClick = onNavigateToOverlaySettings,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Overlay Settings")
                    }

                    OutlinedButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("App Settings")
                    }
                }
            }
        }
    }
}
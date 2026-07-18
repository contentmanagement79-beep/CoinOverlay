package com.coinoverlay.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coinoverlay.ui.viewmodel.CoinSearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinSearchScreen(
    onBack: () -> Unit,
    viewModel: CoinSearchViewModel = viewModel()
) {
    val query by viewModel.query.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedCoins by viewModel.selectedCoins.collectAsState()
    val favoriteCoins by viewModel.favoriteCoins.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Coin") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onQueryChanged,
                label = { Text("Search by symbol") },
                placeholder = { Text("BTC, ETH, SOL") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            Text(
                text = "Selected: ${selectedCoins.joinToString()}",
                style = MaterialTheme.typography.bodyMedium
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(searchResults, key = { it.symbol }) { coin ->
                    Card {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = coin.symbol,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "${coin.quoteAsset} • ${coin.status}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.toggleFavorite(coin.symbol) }
                                ) {
                                    if (coin.symbol in favoriteCoins) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = "Unfavorite"
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Outlined.StarBorder,
                                            contentDescription = "Favorite"
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                            if (coin.symbol in selectedCoins) {
                                OutlinedButton(
                                    onClick = { viewModel.removeCoin(coin.symbol) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Remove")
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.addCoin(coin.symbol) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Add")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
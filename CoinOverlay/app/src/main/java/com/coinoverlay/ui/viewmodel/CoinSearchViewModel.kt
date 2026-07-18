package com.coinoverlay.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.coinoverlay.CoinOverlayApp
import com.coinoverlay.data.model.BinanceExchangeSymbol
import com.coinoverlay.data.model.OverlaySettings
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CoinSearchViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as CoinOverlayApp

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _searchResults = MutableStateFlow<List<BinanceExchangeSymbol>>(emptyList())
    val searchResults: StateFlow<List<BinanceExchangeSymbol>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val settings: StateFlow<OverlaySettings> = app.settingsRepository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OverlaySettings())

    val selectedCoins: StateFlow<List<String>> = app.settingsRepository.settingsFlow
        .map { it.selectedCoins }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteCoins: StateFlow<List<String>> = app.settingsRepository.settingsFlow
        .map { it.favoriteCoins }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var searchJob: Job? = null

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
        searchJob?.cancel()
        if (newQuery.isBlank()) {
            _searchResults.value = emptyList()
            _isLoading.value = false
            return
        }

        searchJob = viewModelScope.launch {
            delay(300)
            _isLoading.value = true
            _searchResults.value = app.coinRepository.searchSymbols(newQuery)
            _isLoading.value = false
        }
    }

    fun addCoin(symbol: String) {
        viewModelScope.launch {
            val current = selectedCoins.value
            if (symbol !in current) {
                val updated = current + symbol
                app.settingsRepository.updateSelectedCoins(updated)
                app.coinRepository.updateTrackedSymbols(updated)
            }
        }
    }

    fun removeCoin(symbol: String) {
        viewModelScope.launch {
            val updated = selectedCoins.value - symbol
            app.settingsRepository.updateSelectedCoins(updated)
            app.coinRepository.updateTrackedSymbols(updated)
        }
    }

    fun toggleFavorite(symbol: String) {
        viewModelScope.launch {
            val current = favoriteCoins.value
            val updated = if (symbol in current) current - symbol else current + symbol
            app.settingsRepository.updateFavoriteCoins(updated)
        }
    }
}
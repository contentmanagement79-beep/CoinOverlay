package com.coinoverlay.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coinoverlay.ui.viewmodel.SettingsViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverlaySettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val settings by viewModel.settings.collectAsState()

    val manualX = remember { mutableStateOf(settings.overlayX.toString()) }
    val manualY = remember { mutableStateOf(settings.overlayY.toString()) }
    val fontColorHex = remember { mutableStateOf(settings.fontColor.toString(16).uppercase()) }

    LaunchedEffect(settings.overlayX, settings.overlayY, settings.fontColor) {
        manualX.value = settings.overlayX.toString()
        manualY.value = settings.overlayY.toString()
        fontColorHex.value = settings.fontColor.toString(16).uppercase()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Overlay Settings") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Text", style = MaterialTheme.typography.titleLarge)

                    Text("Font size: ${settings.fontSizeSp.roundToInt()}sp")
                    Slider(
                        value = settings.fontSizeSp,
                        onValueChange = { viewModel.updateFontSize(it) },
                        valueRange = 8f..32f
                    )

                    Text("Opacity: ${(settings.opacity * 100).roundToInt()}%")
                    Slider(
                        value = settings.opacity,
                        onValueChange = { viewModel.updateOpacity(it) },
                        valueRange = 0.1f..1f
                    )

                    OutlinedTextField(
                        value = fontColorHex.value,
                        onValueChange = {
                            val sanitized = it.filter { ch -> ch.isDigit() || ch.uppercaseChar() in 'A'..'F' }
                            fontColorHex.value = sanitized.take(8)
                            sanitized.toLongOrNull(16)?.let(viewModel::updateFontColor)
                        },
                        label = { Text("Font color ARGB hex") },
                        placeholder = { Text("FFFFFFFF") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    SettingSwitchRow(
                        title = "Bold text",
                        checked = settings.isBold,
                        onCheckedChange = viewModel::updateBold
                    )

                    SettingSwitchRow(
                        title = "Text shadow",
                        checked = settings.hasShadow,
                        onCheckedChange = viewModel::updateShadow
                    )
                }
            }

            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Behavior", style = MaterialTheme.typography.titleLarge)

                    SettingSwitchRow(
                        title = "Lock position",
                        checked = settings.isPositionLocked,
                        onCheckedChange = viewModel::updatePositionLocked
                    )

                    SettingSwitchRow(
                        title = "Touch through",
                        checked = settings.isTouchThroughEnabled,
                        onCheckedChange = viewModel::updateTouchThrough
                    )

                    HorizontalDivider()

                    OutlinedTextField(
                        value = manualX.value,
                        onValueChange = { manualX.value = it.filter(Char::isDigit) },
                        label = { Text("Manual X") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = manualY.value,
                        onValueChange = { manualY.value = it.filter(Char::isDigit) },
                        label = { Text("Manual Y") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val x = manualX.value.toIntOrNull() ?: settings.overlayX
                            val y = manualY.value.toIntOrNull() ?: settings.overlayY
                            viewModel.updateManualPosition(x, y)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Apply Position")
                    }
                }
            }
        }
    }
}
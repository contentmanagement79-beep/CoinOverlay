package com.coinoverlay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.coinoverlay.overlay.OverlayPermission
import com.coinoverlay.ui.screens.CoinSearchScreen
import com.coinoverlay.ui.screens.HomeScreen
import com.coinoverlay.ui.screens.OverlaySettingsScreen
import com.coinoverlay.ui.screens.SettingsScreen
import com.coinoverlay.ui.theme.CoinOverlayTheme

class MainActivity : ComponentActivity() {

    private var hasOverlayPermission by mutableStateOf(false)

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        hasOverlayPermission = OverlayPermission.hasOverlayPermission(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hasOverlayPermission = OverlayPermission.hasOverlayPermission(this)

        setContent {
            CoinOverlayTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            hasOverlayPermission = hasOverlayPermission,
                            onRequestOverlayPermission = {
                                overlayPermissionLauncher.launch(
                                    OverlayPermission.buildPermissionIntent(this@MainActivity)
                                )
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToSettings = { navController.navigate("settings") },
                            onNavigateToOverlaySettings = { navController.navigate("overlay_settings") }
                        )
                    }
                    composable("search") {
                        CoinSearchScreen(onBack = { navController.popBackStack() })
                    }
                    composable("settings") {
                        SettingsScreen(onBack = { navController.popBackStack() })
                    }
                    composable("overlay_settings") {
                        OverlaySettingsScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hasOverlayPermission = OverlayPermission.hasOverlayPermission(this)
    }
}
import androidx.compose.runtime.*
import network.ApiClient
import theme.AppTheme
import ui.DashboardScreen
import ui.LoginScreen
import ui.ScanScreen

enum class Screen {
    Login, Dashboard, ScanInbound, ScanOutbound
}

@Composable
fun App() {
    AppTheme {
        // The ApiClient has a default baseUrl that can be modified on the LoginScreen
        val apiClient = remember { ApiClient() }
        var currentScreen by remember { mutableStateOf(Screen.Login) }

        when (currentScreen) {
            Screen.Login -> LoginScreen(
                apiClient = apiClient,
                onLoginSuccess = { currentScreen = Screen.Dashboard }
            )
            Screen.Dashboard -> DashboardScreen(
                onNavigateInbound = { currentScreen = Screen.ScanInbound },
                onNavigateOutbound = { currentScreen = Screen.ScanOutbound },
                onLogout = {
                    apiClient.token = null
                    currentScreen = Screen.Login
                }
            )
            Screen.ScanInbound -> ScanScreen(
                apiClient = apiClient,
                movementType = "INBOUND",
                onBack = { currentScreen = Screen.Dashboard }
            )
            Screen.ScanOutbound -> ScanScreen(
                apiClient = apiClient,
                movementType = "OUTBOUND",
                onBack = { currentScreen = Screen.Dashboard }
            )
        }
    }
}
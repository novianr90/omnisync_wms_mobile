import androidx.compose.runtime.*
import network.ApiClient
import theme.AppTheme
import ui.DashboardScreen
import ui.LoginScreen

sealed class Screen {
    object Login : Screen()
    object Dashboard : Screen()
    data class List(val type: String) : Screen()
    data class Verification(val movementId: String) : Screen()
}

@Composable
fun App() {
    AppTheme {
        val apiClient = remember { ApiClient() }
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

        when (val screen = currentScreen) {
            is Screen.Login -> LoginScreen(
                apiClient = apiClient,
                onLoginSuccess = { currentScreen = Screen.Dashboard }
            )
            is Screen.Dashboard -> DashboardScreen(
                onNavigateInbound = { currentScreen = Screen.List("INBOUND") },
                onNavigateOutbound = { currentScreen = Screen.List("OUTBOUND") },
                onLogout = {
                    apiClient.token = null
                    currentScreen = Screen.Login
                }
            )
            is Screen.List -> ui.MovementListScreen(
                apiClient = apiClient,
                movementType = screen.type,
                onBack = { currentScreen = Screen.Dashboard },
                onNavigateToVerification = { currentScreen = Screen.Verification(it) }
            )
            is Screen.Verification -> ui.MovementVerificationScreen(
                apiClient = apiClient,
                movementId = screen.movementId,
                onBack = { currentScreen = Screen.Dashboard },
                onComplete = { currentScreen = Screen.Dashboard }
            )
        }
    }
}
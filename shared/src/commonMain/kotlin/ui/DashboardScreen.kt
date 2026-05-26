package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(onNavigateInbound: () -> Unit, onNavigateOutbound: () -> Unit, onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("WMS Dashboard", style = MaterialTheme.typography.h5, color = MaterialTheme.colors.onSurface)
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onNavigateInbound,
            modifier = Modifier.fillMaxWidth().height(64.dp)
        ) {
            Text("INBOUND SCAN", style = MaterialTheme.typography.button)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onNavigateOutbound,
            modifier = Modifier.fillMaxWidth().height(64.dp)
        ) {
            Text("OUTBOUND SCAN", style = MaterialTheme.typography.button)
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("LOGOUT", color = MaterialTheme.colors.error)
        }
    }
}

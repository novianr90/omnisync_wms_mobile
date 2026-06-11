package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import network.ApiClient
import network.models.MovementHeader

@Composable
fun MovementListScreen(apiClient: ApiClient, movementType: String, onBack: () -> Unit, onNavigateToVerification: (String) -> Unit) {
    var movements by remember { mutableStateOf<List<MovementHeader>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(movementType) {
        fetchMovements(apiClient, movementType) { result, err ->
            movements = result
            errorMessage = err
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("$movementType Tasks") },
            navigationIcon = {
                IconButton(onClick = onBack) { Text("<") }
            }
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $errorMessage", color = MaterialTheme.colors.error)
            }
        } else if (movements.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No active $movementType tasks.")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                items(movements) { movement ->
                    MovementCard(movement, apiClient, coroutineScope) {
                        onNavigateToVerification(movement.id)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun MovementCard(movement: MovementHeader, apiClient: ApiClient, coroutineScope: kotlinx.coroutines.CoroutineScope, onOpen: () -> Unit) {
    var status by remember { mutableStateOf(movement.status) }
    var isClaiming by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth().clickable {
        if (status != "OPEN") onOpen()
    }, elevation = 4.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(movement.document_no, style = MaterialTheme.typography.h6)
            Text("Status: $status")
            Text("Remarks: ${movement.remarks ?: "-"}")
            Spacer(modifier = Modifier.height(8.dp))

            if (status == "OPEN") {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isClaiming = true
                            try {
                                val res = apiClient.claimMovement(movement.id)
                                status = res.status ?: "IN_PROGRESS"
                                onOpen()
                            } catch (e: Exception) {
                                // Ignore or show toast in a real app
                            } finally {
                                isClaiming = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isClaiming
                ) {
                    if (isClaiming) CircularProgressIndicator(Modifier.size(24.dp))
                    else Text("Claim Task")
                }
            } else {
                Button(onClick = onOpen, modifier = Modifier.fillMaxWidth()) {
                    Text("Resume Task")
                }
            }
        }
    }
}

private suspend fun fetchMovements(apiClient: ApiClient, type: String, onComplete: (List<MovementHeader>, String?) -> Unit) {
    try {
        val list = apiClient.getMovements(type = type)
        onComplete(list.filter { it.status == "OPEN" || it.status == "IN_PROGRESS" }, null)
    } catch (e: Exception) {
        onComplete(emptyList(), e.message)
    }
}

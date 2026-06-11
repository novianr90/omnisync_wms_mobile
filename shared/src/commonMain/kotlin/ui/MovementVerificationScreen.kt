package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import network.ApiClient
import network.models.MovementHeader
import network.models.MovementLine
import network.models.ScanVerifyRequest

@Composable
fun MovementVerificationScreen(apiClient: ApiClient, movementId: String, onBack: () -> Unit, onComplete: () -> Unit) {
    var movement by remember { mutableStateOf<MovementHeader?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    var scannedLocator by remember { mutableStateOf("") }
    var scannedSku by remember { mutableStateOf("") }
    var scannedQty by remember { mutableStateOf("") }
    var scanMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    fun refreshData() {
        coroutineScope.launch {
            try {
                movement = apiClient.getMovementDetails(movementId)
            } catch (e: Exception) {
                scanMessage = "Error loading details: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(movementId) {
        refreshData()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(movement?.document_no ?: "Loading...") },
            navigationIcon = {
                IconButton(onClick = onBack) { Text("<") }
            }
        )

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Column
        }

        movement?.let { mov ->
            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                Text("Type: ${mov.movement_type}")
                Text("Remarks: ${mov.remarks ?: "-"}")
                Spacer(modifier = Modifier.height(8.dp))
                Divider()

                // Lines Checklist
                LazyColumn(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {
                    items(mov.lines ?: emptyList()) { line ->
                        LineItemView(line, mov.movement_type)
                    }
                }
                
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                // Scanner form
                OutlinedTextField(
                    value = scannedLocator,
                    onValueChange = { scannedLocator = it },
                    label = { Text("1. Scan Locator") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = scannedSku,
                    onValueChange = { scannedSku = it },
                    label = { Text("2. Scan SKU") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = scannedQty,
                    onValueChange = { scannedQty = it },
                    label = { Text("3. Quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                if (scanMessage != null) {
                    Text(scanMessage!!, color = MaterialTheme.colors.error)
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val qty = scannedQty.toIntOrNull() ?: 1
                                apiClient.scanVerify(movementId, ScanVerifyRequest(scannedSku, scannedLocator, qty))
                                scanMessage = null
                                scannedSku = ""
                                scannedLocator = ""
                                scannedQty = ""
                                refreshData()
                            } catch (e: Exception) {
                                scanMessage = "Scan failed: ${e.message}"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = scannedLocator.isNotBlank() && scannedSku.isNotBlank()
                ) {
                    Text("Verify Scan")
                }

                Spacer(modifier = Modifier.height(8.dp))

                val allDone = mov.lines?.all { it.actual_quantity >= it.requested_quantity } == true
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isSubmitting = true
                            try {
                                apiClient.submitMovement(movementId)
                                onComplete()
                            } catch (e: Exception) {
                                scanMessage = "Submit failed: ${e.message}"
                            } finally {
                                isSubmitting = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = allDone && !isSubmitting
                ) {
                    if (isSubmitting) CircularProgressIndicator(Modifier.size(24.dp))
                    else Text("Submit Movement")
                }
            }
        }
    }
}

@Composable
fun LineItemView(line: MovementLine, type: String) {
    val progressColor = when {
        line.actual_quantity >= line.requested_quantity -> Color(0xFF4CAF50) // Green
        line.actual_quantity > 0 -> Color(0xFFFFC107) // Yellow
        else -> Color.Transparent
    }
    
    val locCode = if (type == "INBOUND") line.to_locator?.code else line.from_locator?.code
    val instruction = if (type == "INBOUND") "Taruh ${line.product?.name} di $locCode" else "Ambil ${line.product?.name} di $locCode"
    
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = 2.dp) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.width(8.dp).fillMaxHeight().background(progressColor))
            Column(modifier = Modifier.padding(8.dp)) {
                Text(instruction, style = MaterialTheme.typography.subtitle2)
                Text("SKU: ${line.product?.sku}")
                Text("Progress: ${line.actual_quantity} / ${line.requested_quantity}")
            }
        }
    }
}

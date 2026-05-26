package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import network.ApiClient
import network.Locator
import network.MovementRequest
import network.Product

@Composable
fun ScanScreen(apiClient: ApiClient, movementType: String, onBack: () -> Unit) {
    var scannedSku by remember { mutableStateOf("") }
    var scannedLocatorCode by remember { mutableStateOf("") }
    var quantityStr by remember { mutableStateOf("") }
    
    var product by remember { mutableStateOf<Product?>(null) }
    var locator by remember { mutableStateOf<Locator?>(null) }
    
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Text("<")
            }
            Text("$movementType SCAN", style = MaterialTheme.typography.h6, modifier = Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Product Scan
        OutlinedTextField(
            value = scannedSku,
            onValueChange = { scannedSku = it },
            label = { Text("Scan Product SKU") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        product = apiClient.getProduct(scannedSku)
                        message = "Product found: ${product?.name}"
                    } catch (e: Exception) {
                        message = "Product not found"
                        product = null
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("LOOKUP PRODUCT")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Locator Scan
        OutlinedTextField(
            value = scannedLocatorCode,
            onValueChange = { scannedLocatorCode = it },
            label = { Text("Scan Locator Code") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        locator = apiClient.getLocator(scannedLocatorCode)
                        message = "Locator found: ${locator?.name}"
                    } catch (e: Exception) {
                        message = "Locator not found"
                        locator = null
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("LOOKUP LOCATOR")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Quantity
        OutlinedTextField(
            value = quantityStr,
            onValueChange = { quantityStr = it },
            label = { Text("Quantity") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        if (message != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(message!!, color = if (message!!.contains("found")) MaterialTheme.colors.primary else MaterialTheme.colors.error)
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Submit
        Button(
            onClick = {
                coroutineScope.launch {
                    isLoading = true
                    try {
                        val req = MovementRequest(
                            movement_type = movementType,
                            product_id = product!!.id,
                            locator_id = locator!!.id,
                            quantity = quantityStr.toIntOrNull() ?: 1
                        )
                        val res = apiClient.createMovement(req)
                        message = "Success: ${res.document_no}"
                        // Reset form
                        scannedSku = ""
                        scannedLocatorCode = ""
                        quantityStr = ""
                        product = null
                        locator = null
                    } catch (e: Exception) {
                        message = "Error: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(64.dp),
            enabled = product != null && locator != null && quantityStr.toIntOrNull() != null && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colors.onPrimary)
            } else {
                Text("SUBMIT MOVEMENT", style = MaterialTheme.typography.button)
            }
        }
    }
}

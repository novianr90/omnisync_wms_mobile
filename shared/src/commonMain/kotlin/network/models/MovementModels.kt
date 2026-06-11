package network.models

import kotlinx.serialization.Serializable
import network.Locator
import network.Product

@Serializable
data class MovementHeader(
    val id: String,
    val document_no: String,
    val movement_type: String,
    val status: String,
    val assigned_operator_id: String? = null,
    val remarks: String? = null,
    val created_at: String? = null,
    val lines: List<MovementLine>? = null
)

@Serializable
data class MovementLine(
    val id: String,
    val product_id: String,
    val product: Product? = null,
    val from_locator_id: String? = null,
    val to_locator_id: String? = null,
    val from_locator: Locator? = null,
    val to_locator: Locator? = null,
    val requested_quantity: Int,
    val actual_quantity: Int
)

@Serializable
data class ScanVerifyRequest(
    val sku: String,
    val locator_code: String,
    val quantity: Int
)

@Serializable
data class GenericResponse(
    val message: String,
    val status: String? = null,
    val document_no: String? = null,
    val assigned_operator_id: String? = null,
    val line_id: String? = null,
    val actual_quantity: Int? = null,
    val requested_quantity: Int? = null
)

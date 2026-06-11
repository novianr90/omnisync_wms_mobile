package network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Data Models
@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoginResponse(val token: String, val user_id: String)

@Serializable
data class UoM(val id: String, val code: String, val name: String)

@Serializable
data class Product(val id: String, val sku: String, val name: String, val uom_id: String, val uom: UoM? = null)

@Serializable
data class Locator(val id: String, val code: String, val name: String, val warehouse_id: String)

@Serializable
data class MovementRequest(
    val movement_type: String,
    val product_id: String,
    val locator_id: String,
    val quantity: Int,
    val uom_id: String = "",
    val remarks: String = ""
)

@Serializable
data class MovementResponse(
    val message: String,
    val document_no: String
)

class ApiClient {
    var baseUrl: String = "http://10.0.2.2:9901/api/v1/"
    var token: String? = null

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
            token?.let { bearerAuth(it) }
        }
    }

    suspend fun login(request: LoginRequest): LoginResponse {
        return client.post("${baseUrl}auth/login") {
            setBody(request)
        }.body()
    }

    suspend fun getProduct(sku: String): Product {
        return client.get("${baseUrl}products/scan/$sku").body()
    }

    suspend fun getLocator(code: String): Locator {
        return client.get("${baseUrl}locators/scan/$code").body()
    }

    suspend fun getMovements(type: String? = null, status: String? = null): List<network.models.MovementHeader> {
        return client.get("${baseUrl}movements") {
            type?.let { io.ktor.client.request.parameter("type", it) }
            status?.let { io.ktor.client.request.parameter("status", it) }
        }.body()
    }

    suspend fun getMovementDetails(id: String): network.models.MovementHeader {
        return client.get("${baseUrl}movements/$id").body()
    }

    suspend fun claimMovement(id: String): network.models.GenericResponse {
        return client.post("${baseUrl}movements/$id/claim").body()
    }

    suspend fun scanVerify(id: String, request: network.models.ScanVerifyRequest): network.models.GenericResponse {
        return client.post("${baseUrl}movements/$id/scan-verify") {
            setBody(request)
        }.body()
    }

    suspend fun submitMovement(id: String): network.models.GenericResponse {
        return client.post("${baseUrl}movements/$id/submit").body()
    }
}

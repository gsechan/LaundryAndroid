package com.gabesechan.laundrydemo.orders

import com.gabesechan.laundrydemo.models.Order
import com.gabesechan.laundrydemo.network.NetworkResponse
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

@Serializable
data class PostOrderRequest(
    val order: PostOrder
)

@Serializable
data class PostOrder(
    val lines: List<PostOrderLine>,
    val scheduledPickup: Long,
    val scheduledDropoff: Long,
    val pickupAddress: String,
    val dropoffAddress: String,
)

@Serializable
data class PostOrderLine(
    val itemId: String,
    val quantity: String?,
)

@Serializable
data class PostOrderResponse(val order: Order)

interface OrdersServer {

    @Headers("Content-Type: application/json")
    @POST("orders")
    suspend fun postOrder(@Body request: PostOrderRequest): NetworkResponse<PostOrderResponse>

    @Headers("Content-Type: application/json")
    @GET("orders")
    suspend fun getAll(): NetworkResponse<List<Order>>


}
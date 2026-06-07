package com.gabesechan.laundrydemo.orders

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

@Serializable
data class PostOrderRequest(
    val lines: List<PostOrderLine>,
    val scheduledPickup: Long,
    val scheduledDropoff: Long,

)

@Serializable
data class PostOrderLine(
    val itemId: String,
    val quantity: String?,
    val itemType: String,
)

@Serializable
data class PostOrderResponse(val success: Boolean, val orderId: String)

interface OrdersServer {

    @Headers("Content-Type: application/json")
    @POST("orders")
    suspend fun postOrder(@Body request: PostOrderRequest): PostOrderResponse

}
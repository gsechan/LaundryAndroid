package com.gabesechan.laundrydemo.orders

import com.gabesechan.laundrydemo.network.NetworkResponse
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
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

@Serializable
data class GetOrderResponse(val orders:List<GetOrder>)

@Serializable
data class GetOrder(
    val id: String,
    val state: String,
    val completed: Long?,
    val lastChange: Long,
    val submitted: Long,
    val scheduledPickup: Long,
    val scheduledDropoff: Long,
    val lines: List<GetOrderLine>
)

@Serializable
data class GetOrderLine(
    val id: String,
    val itemType: String,
    val name: String,
    val price_per_unit: String,
    val quantity: String?,
    val total_cost: String?,
)

interface OrdersServer {

    @Headers("Content-Type: application/json")
    @POST("orders")
    suspend fun postOrder(@Body request: PostOrderRequest): NetworkResponse<PostOrderResponse>

    @Headers("Content-Type: application/json")
    @GET("orders")
    suspend fun getAll(): NetworkResponse<GetOrderResponse>


}
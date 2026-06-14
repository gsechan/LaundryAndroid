package com.gabesechan.laundrydemo.user

import com.gabesechan.laundrydemo.models.Address
import com.gabesechan.laundrydemo.network.NetworkResponse
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


@Serializable data class PostAddressRequest(val address: Address)
@Serializable data class PostAddressResponse(val address: Address)


interface UserServer {

    @Headers("Content-Type: application/json")
    @POST("addresses")
    suspend fun addAddress(@Body request: PostAddressRequest): NetworkResponse<PostAddressResponse>
}
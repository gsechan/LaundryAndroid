package com.gabesechan.laundrydemo.user

import com.gabesechan.laundrydemo.models.Address
import com.gabesechan.laundrydemo.network.NetworkResponse
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

@Serializable data class PatchAddressRequest(val address: Address)
@Serializable data class PostAddressRequest(val address: Address)
@Serializable data class PostAddressResponse(val address: Address)


interface UserServer {

    @Headers("Content-Type: application/json")
    @POST("addresses")
    suspend fun addAddress(@Body request: PostAddressRequest): NetworkResponse<PostAddressResponse>

    @Headers("Content-Type: application/json")
    @DELETE("addresses/{id}")
    suspend fun deleteAddress(@Path("id") id: String): NetworkResponse<Unit>

    @Headers("Content-Type: application/json")
    @PATCH("addresses/{id}")
    suspend fun updateAddress(@Path("id") id: String, @Body request: PatchAddressRequest): NetworkResponse<PostAddressResponse>
}
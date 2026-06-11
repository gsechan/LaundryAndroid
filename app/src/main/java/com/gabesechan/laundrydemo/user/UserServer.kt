package com.gabesechan.laundrydemo.user

import com.gabesechan.laundrydemo.login.LoginAddress
import com.gabesechan.laundrydemo.login.LoginUser
import com.gabesechan.laundrydemo.network.NetworkResponse
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


@Serializable data class PostAddressRequest(val address: LoginAddress)
@Serializable data class PostAddressResponse(val user: LoginUser)


interface UserServer {

    @Headers("Content-Type: application/json")
    @POST("addresses")
    suspend fun addAddress(@Body request: PostAddressRequest): NetworkResponse<PostAddressResponse>
}
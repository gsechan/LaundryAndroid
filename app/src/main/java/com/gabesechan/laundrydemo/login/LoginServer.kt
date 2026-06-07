package com.gabesechan.laundrydemo.login

import com.gabesechan.laundrydemo.network.NetworkResponse
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

@Serializable
data class LoginRequest(val phone: String, val password: String, val organization: String)

@Serializable
data class LoginResponse(
    val session: String,
    val user: LoginUser
)
@Serializable
data class LoginUser(
    val name: String,
    val email: String?,
    val phone: String,
    val addresses: List<LoginAddress>,
)

@Serializable
data class LoginAddress(
    val id: String,
    val street1: String,
    val street2: String?,
    val city: String,
    val state: String,
    val country: String,
    val postcode: String
)

@Serializable
data class CheckAuthRequest(val token: String)



interface LoginServer {


    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun login(@Body request: LoginRequest): NetworkResponse<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("checkAuth")
    suspend fun checkAuth(@Body request: CheckAuthRequest): NetworkResponse<LoginUser>

}
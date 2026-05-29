package com.gabesechan.laundrydemo.login

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

@Serializable
data class LoginRequest(val username: String, val password: String)

data class LoginResponse(
    val success: Boolean,
    val user: LoginUser
)

data class LoginUser(
    val id: String,
    val name: String,
    val email: String?,
    val phone: String?
)

interface LoginServer {


    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
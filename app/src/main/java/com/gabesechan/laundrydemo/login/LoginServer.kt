package com.gabesechan.laundrydemo.login

import com.gabesechan.laundrydemo.network.NetworkResponse
import com.gabesechan.laundrydemo.models.User
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

@Serializable
data class LoginRequest(val phone: String, val password: String, val organization: String)

@Serializable
data class LoginResponse(
    val session: String,
    val user: User
)

@Serializable
data class CheckAuthRequest(val token: String)

@Serializable
data class CreateUserRequest(
    val user: User,
    val password: String,
    val org: String,
)

@Serializable
data class CreateUserResponse(
    val session: String,
    val user: User
)


interface LoginServer {


    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun login(@Body request: LoginRequest): NetworkResponse<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("checkAuth")
    suspend fun checkAuth(@Body request: CheckAuthRequest): NetworkResponse<User>

    @Headers("Content-Type: application/json")
    @GET("logout")
    suspend fun logout(): NetworkResponse<Unit>

    @Headers("Content-Type: application/json")
    @POST("users")
    suspend fun createAccount(@Body request: CreateUserRequest): NetworkResponse<CreateUserResponse>

}
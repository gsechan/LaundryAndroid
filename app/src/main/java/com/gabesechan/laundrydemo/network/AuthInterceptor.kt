package com.gabesechan.laundrydemo.network

import com.gabesechan.laundrydemo.user.UserRepository
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val userRepository: UserRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = userRepository.authToken
        val request = if (userRepository.authToken.isNotEmpty()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        return chain.proceed(request)
    }
}

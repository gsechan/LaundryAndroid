package com.gabesechan.laundrydemo.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class HttpErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code in 400..599) {
            response.close()
            throw IOException("HTTP error response: ${response.code}")
        }

        return response
    }
}

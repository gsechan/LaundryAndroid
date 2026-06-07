package com.gabesechan.laundrydemo.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class LocaleInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val locale = context.resources.configuration.locales[0].toLanguageTag()

        val request =
            chain.request().newBuilder()
                .addHeader("Accept-Language", "$locale")
                .build()

        return chain.proceed(request)
    }
}

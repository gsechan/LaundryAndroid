package com.gabesechan.laundrydemo.network

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.LocaleList
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Test
import java.util.Locale

class LocaleInterceptorTest {

    private fun contextWithLocale(locale: Locale): Context {
        val localeList = mockk<LocaleList> {
            every { get(0) } returns locale
        }
        val configuration = mockk<Configuration> {
            every { locales } returns localeList
        }
        val resources = mockk<Resources> {
            every { configuration } returns configuration
        }
        return mockk<Context> {
            every { resources } returns resources
        }
    }

    private fun chainFor(request: Request): Interceptor.Chain {
        val response = mockk<Response>()
        return mockk<Interceptor.Chain> {
            every { request() } returns request
            every { proceed(any()) } returns response
        }
    }

    @Test
    fun testAddsAcceptLanguageHeaderForLocale() {
        val context = contextWithLocale(Locale.US)
        val originalRequest = Request.Builder().url("https://example.com").build()
        val chain = chainFor(originalRequest)

        LocaleInterceptor(context).intercept(chain)

        verify(exactly = 1) {
            chain.proceed(match { it.header("Accept-Language") == "en-US" })
        }
    }

    @Test
    fun testAddsAcceptLanguageHeaderForDifferentLocale() {
        val context = contextWithLocale(Locale.FRANCE)
        val originalRequest = Request.Builder().url("https://example.com").build()
        val chain = chainFor(originalRequest)

        LocaleInterceptor(context).intercept(chain)

        verify(exactly = 1) {
            chain.proceed(match { it.header("Accept-Language") == "fr-FR" })
        }
    }
}

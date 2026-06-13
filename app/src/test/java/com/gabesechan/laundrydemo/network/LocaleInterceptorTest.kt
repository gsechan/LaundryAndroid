package com.gabesechan.laundrydemo.network

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class LocaleInterceptorTest {

    private fun contextWithLocale(locale: Locale): Context {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocales(LocaleList(locale))
        return context.createConfigurationContext(configuration)
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

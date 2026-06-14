package com.gabesechan.laundrydemo.network

import com.gabesechan.laundrydemo.login.TokenStorage
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.*
import okhttp3.Request
import okhttp3.Response
import okhttp3.Interceptor
import org.junit.Test

class AuthInterceptorTest {

    private fun chainFor(request: Request): Interceptor.Chain {
        val response = mockk<Response>()
        return mockk<Interceptor.Chain> {
            every { request() } returns request
            every { proceed(any()) } returns response
        }
    }

    @Test
    fun testAddsAuthorizationHeaderWhenTokenPresent() {
        val tokenStorage = mockk<TokenStorage> {
            every { authToken } returns "my-token"
        }
        val originalRequest = Request.Builder().url("https://example.com").build()
        val chain = chainFor(originalRequest)

        AuthInterceptor(tokenStorage).intercept(chain)

        verify(exactly = 1) {
            chain.proceed(match { it.header("Authorization") == "Bearer my-token" })
        }
    }

    @Test
    fun testDoesNotAddAuthorizationHeaderWhenTokenEmpty() {
        val tokenStorage = mockk<TokenStorage> {
            every { authToken } returns ""
        }
        val originalRequest = Request.Builder().url("https://example.com").build()
        val chain = chainFor(originalRequest)

        AuthInterceptor(tokenStorage).intercept(chain)

        verify(exactly = 1) {
            chain.proceed(match { it.header("Authorization") == null && it === originalRequest })
        }
    }
}

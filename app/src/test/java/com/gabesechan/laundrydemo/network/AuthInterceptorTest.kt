package com.gabesechan.laundrydemo.network

import com.gabesechan.laundrydemo.user.UserRepository
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
        val userRepository = mockk<UserRepository> {
            every { authToken } returns "my-token"
        }
        val originalRequest = Request.Builder().url("https://example.com").build()
        val chain = chainFor(originalRequest)

        AuthInterceptor(userRepository).intercept(chain)

        verify(exactly = 1) {
            chain.proceed(match { it.header("Authorization") == "Bearer my-token" })
        }
    }

    @Test
    fun testDoesNotAddAuthorizationHeaderWhenTokenEmpty() {
        val userRepository = mockk<UserRepository> {
            every { authToken } returns ""
        }
        val originalRequest = Request.Builder().url("https://example.com").build()
        val chain = chainFor(originalRequest)

        AuthInterceptor(userRepository).intercept(chain)

        verify(exactly = 1) {
            chain.proceed(match { it.header("Authorization") == null && it === originalRequest })
        }
    }
}

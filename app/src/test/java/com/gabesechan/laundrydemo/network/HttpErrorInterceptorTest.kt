package com.gabesechan.laundrydemo.network

import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.*
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import org.junit.Test

class HttpErrorInterceptorTest {

    private fun responseWithCode(code: Int): Response {
        val request = Request.Builder().url("https://example.com").build()
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message("")
            .body("".toResponseBody(null))
            .build()
    }

    private fun chainReturning(response: Response): Interceptor.Chain {
        val request = Request.Builder().url("https://example.com").build()
        return mockk<Interceptor.Chain> {
            every { request() } returns request
            every { proceed(any()) } returns response
        }
    }

    @Test
    fun testThrowsIOExceptionOn4xxResponse() {
        val chain = chainReturning(responseWithCode(404))

        try {
            HttpErrorInterceptor().intercept(chain)
            fail("Expected IOException")
        } catch (_: IOException) {
        }
    }

    @Test
    fun testThrowsIOExceptionOn5xxResponse() {
        val chain = chainReturning(responseWithCode(500))

        try {
            HttpErrorInterceptor().intercept(chain)
            fail("Expected IOException")
        } catch (_: IOException) {
        }
    }

    @Test
    fun testDoesNotThrowOn2xxResponse() {
        val chain = chainReturning(responseWithCode(200))

        val response = HttpErrorInterceptor().intercept(chain)

        assertEquals(200, response.code)
    }
}

package com.gabesechan.laundrydemo.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class BigDecimalSerializerTest {

    @Serializable
    private data class Wrapper(
        @Serializable(with = BigDecimalSerializer::class)
        val value: BigDecimal
    )

    @Test
    fun testSerializeEncodesAsString() {
        val json = Json.encodeToString(Wrapper.serializer(), Wrapper(BigDecimal("10.00")))

        assertEquals("{\"value\":\"10.00\"}", json)
    }

    @Test
    fun testDeserializeDecodesFromString() {
        val wrapper = Json.decodeFromString(Wrapper.serializer(), "{\"value\":\"5.50\"}")

        assertEquals(BigDecimal("5.50"), wrapper.value)
    }

    @Test
    fun testRoundTripPreservesValue() {
        val value = BigDecimal("123.456")

        val json = Json.encodeToString(Wrapper.serializer(), Wrapper(value))
        val decoded = Json.decodeFromString(Wrapper.serializer(), json)

        assertEquals(value, decoded.value)
    }
}

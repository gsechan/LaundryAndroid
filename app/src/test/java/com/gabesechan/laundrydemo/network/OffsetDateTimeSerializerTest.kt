package com.gabesechan.laundrydemo.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class OffsetDateTimeSerializerTest {

    @Serializable
    private data class Wrapper(
        @Serializable(with = OffsetDateTimeSerializer::class)
        val value: OffsetDateTime
    )

    @Test
    fun testSerializeEncodesEpochMillis() {
        val time = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1_700_000_000_000L), ZoneOffset.UTC)

        val json = Json.encodeToString(Wrapper.serializer(), Wrapper(time))

        assertEquals("{\"value\":1700000000000}", json)
    }

    @Test
    fun testDeserializeDecodesEpochMillis() {
        val wrapper = Json.decodeFromString(Wrapper.serializer(), "{\"value\":1700000000000}")

        val expected = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1_700_000_000_000L), ZoneOffset.UTC)
        assertEquals(expected, wrapper.value)
    }

    @Test
    fun testRoundTripPreservesInstant() {
        val time = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1_234_567_891_011L), ZoneOffset.UTC)

        val json = Json.encodeToString(Wrapper.serializer(), Wrapper(time))
        val decoded = Json.decodeFromString(Wrapper.serializer(), json)

        assertEquals(time.toInstant(), decoded.value.toInstant())
    }
}

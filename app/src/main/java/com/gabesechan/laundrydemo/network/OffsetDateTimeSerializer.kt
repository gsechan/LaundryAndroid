package com.gabesechan.laundrydemo.network

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

object OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("OffsetDateTime", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeLong(value.toInstant().toEpochMilli())
    }

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(decoder.decodeLong()), ZoneOffset.UTC)
    }
}

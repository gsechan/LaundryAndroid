package com.gabesechan.laundrydemo.models

import com.gabesechan.laundrydemo.network.OffsetDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class Order(
    val id: String,
    //Enum on server, but we'd need to serialize in a way to deal with additions
    val state: String,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val completed: OffsetDateTime?,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val lastChange: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val submitted: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val scheduledPickup: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val scheduledDropoff: OffsetDateTime,
    val pickupAddressId: String,
    val dropoffAddressId: String,
    val lines: List<OrderLine>
)

@Serializable
data class OrderLine(
    val itemType: String,
    val name: String,
    val pricePerUnit: String,
    val quantity: String?,
    val totalCost: String?,
)
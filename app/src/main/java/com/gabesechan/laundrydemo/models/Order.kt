package com.gabesechan.laundrydemo.models

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String,
    val state: String,  //Enum on server, but we'd
    val completed: Long?,
    val lastChange: Long,
    val submitted: Long,
    val scheduledPickup: Long,
    val scheduledDropoff: Long,
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
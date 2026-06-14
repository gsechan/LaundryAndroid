package com.gabesechan.laundrydemo.models

import com.gabesechan.laundrydemo.network.BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class Item(
    val id: String,
    val name: String,
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,
    val itemType: String
)
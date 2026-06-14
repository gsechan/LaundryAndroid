package com.gabesechan.laundrydemo.models

import kotlinx.serialization.Serializable


@Serializable
data class Address(
    val id: String,
    val street1: String,
    val street2: String?,
    val city: String,
    val state: String,
    val country: String,
    val postcode: String
)

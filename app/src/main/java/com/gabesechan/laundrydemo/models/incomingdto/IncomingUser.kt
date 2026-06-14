package com.gabesechan.laundrydemo.models.incomingdto

import com.gabesechan.laundrydemo.models.Address
import com.gabesechan.laundrydemo.models.User
import kotlinx.serialization.Serializable

@Serializable
data class IncomingUser(
    val name: String,
    val email: String?,
    val phone: String,
    val addresses: List<Address>,
) {
    fun toModel() : User {
        return User(
            name,
            email,
            phone,
            addresses
        )
    }
}
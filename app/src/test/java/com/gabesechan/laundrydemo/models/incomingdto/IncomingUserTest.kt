package com.gabesechan.laundrydemo.models.incomingdto

import com.gabesechan.laundrydemo.models.Address
import com.gabesechan.laundrydemo.models.User
import junit.framework.TestCase.assertEquals
import org.junit.Test

class IncomingUserTest {
    @Test
    fun testToUserConvertsLoginUserToUser() {
        val address = Address(
            "1",
            "123 Main St",
            "Apt 4",
            "Springfield",
            "IL",
            "USA",
            "62701"
        )
        val incomingUser = IncomingUser(
            "gabe",
            "gabe@example.com",
            "1234567890",
            listOf(address)
        )

        val user = incomingUser.toModel()

        assertEquals(
            User(
                "gabe",
                "gabe@example.com",
                "1234567890",
                listOf(Address("1", "123 Main St", "Apt 4", "Springfield", "IL", "USA", "62701"))
            ),
            user
        )
    }

    @Test
    fun testToUserWithNullEmailAndNoAddresses() {
        val incomingUser = IncomingUser(
            "gabe",
            null,
            "1234567890",
            emptyList()
        )

        val user = incomingUser.toModel()

        assertEquals(User("gabe", null, "1234567890", emptyList()), user)
    }

}
package com.gabesechan.laundrydemo.login

import com.gabesechan.laundrydemo.user.Address
import com.gabesechan.laundrydemo.user.User
import junit.framework.TestCase.assertEquals
import org.junit.Test

class LoginServerTest {

    @Test
    fun testToUserConvertsLoginUserToUser() {
        val loginAddress = LoginAddress(
            "1",
            "123 Main St",
            "Apt 4",
            "Springfield",
            "IL",
            "USA",
            "62701"
        )
        val loginUser = LoginUser(
            "gabe",
            "gabe@example.com",
            "1234567890",
            listOf(loginAddress)
        )

        val user = loginUser.toModel()

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
        val loginUser = LoginUser(
            "gabe",
            null,
            "1234567890",
            emptyList()
        )

        val user = loginUser.toModel()

        assertEquals(User("gabe", null, "1234567890", emptyList()), user)
    }

    @Test
    fun testLoginAddressToModelConvertsToAddress() {
        val loginAddress = LoginAddress(
            "1",
            "123 Main St",
            "Apt 4",
            "Springfield",
            "IL",
            "USA",
            "62701"
        )

        val address = loginAddress.toModel()

        assertEquals(
            Address("1", "123 Main St", "Apt 4", "Springfield", "IL", "USA", "62701"),
            address
        )
    }

    @Test
    fun testLoginAddressToModelWithNullStreet2() {
        val loginAddress = LoginAddress(
            "1",
            "123 Main St",
            null,
            "Springfield",
            "IL",
            "USA",
            "62701"
        )

        val address = loginAddress.toModel()

        assertEquals(
            Address("1", "123 Main St", null, "Springfield", "IL", "USA", "62701"),
            address
        )
    }
}

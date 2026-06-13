package com.gabesechan.laundrydemo.user

import junit.framework.TestCase.*
import org.junit.Test

class UserTest {

    @Test
    fun testNoUserIsNotLoggedIn() {
        assertFalse(User.NoUser.isLoggedIn())
    }

    @Test
    fun testUserWithDataIsLoggedIn() {
        val user = User("gabe", "gabe@example.com", "1234567890", emptyList())
        assertTrue(user.isLoggedIn())
    }

    @Test
    fun testUserMatchingNoUserFieldsIsNotLoggedIn() {
        val user = User("", "", "", emptyList())
        assertFalse(user.isLoggedIn())
    }
}

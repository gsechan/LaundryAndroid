package com.gabesechan.laundrydemo.account

import org.junit.Test
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue

class UserTest {
    @Test
    fun NoUserIsNotLoggedIn() {
        val user = User.NoUser
        assertFalse(user.isLoggedIn())
    }

    @Test
    fun RealUserIsLoggedIn() {
        val user = User.RealUser("1","Gabe","","")
        assertTrue(user.isLoggedIn())
    }

}
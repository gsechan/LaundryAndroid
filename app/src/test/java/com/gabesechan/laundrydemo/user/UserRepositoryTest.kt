package com.gabesechan.laundrydemo.user

import com.gabesechan.laundrydemo.models.User
import junit.framework.TestCase.*
import org.junit.Test

class UserRepositoryTest {

    private val user = User("gabe", "gabe@example.com", "1234567890", emptyList())

    @Test
    fun testSetUserUpdatesCurrentUser() {
        val repo = UserRepository()

        repo.setUser(user)

        assertEquals(user, repo.current.value)
    }

    @Test
    fun testSetUserWithNoUserThrowsAndDoesNotChangeState() {
        val repo = UserRepository()

        try {
            repo.setUser(User.NoUser)
            fail("Expected RuntimeException")
        } catch (ex: RuntimeException) {
            assertEquals("Cannot use login to logout", ex.message)
        }

        assertEquals(User.NoUser, repo.current.value)
    }

    @Test
    fun testClearUserResetsCurrentUserToNoUser() {
        val repo = UserRepository()
        repo.setUser(user)

        repo.clearUser()

        assertEquals(User.NoUser, repo.current.value)
    }
}

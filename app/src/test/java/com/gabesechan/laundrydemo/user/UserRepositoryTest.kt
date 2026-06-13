package com.gabesechan.laundrydemo.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UserRepositoryTest {

    private val tokenKey = stringPreferencesKey("token")
    private val user = User("gabe", "gabe@example.com", "1234567890", emptyList())

    @Test
    fun testInitFromDiskReturnsStoredToken() = runTest {
        val datastore = mockk<DataStore<Preferences>> {
            every { data } returns flowOf(preferencesOf(tokenKey to "stored-token"))
        }
        val repo = UserRepository(datastore)

        assertEquals("stored-token", repo.initFromDisk())
    }

    @Test
    fun testInitFromDiskReturnsNullWhenNoTokenStored() = runTest {
        val datastore = mockk<DataStore<Preferences>> {
            every { data } returns flowOf(emptyPreferences())
        }
        val repo = UserRepository(datastore)

        assertNull(repo.initFromDisk())
    }

    @Test
    fun testSetUserUpdatesCurrentUserAndAuthToken() = runTest {
        val datastore = mockk<DataStore<Preferences>> {
            coEvery { updateData(any()) } returns emptyPreferences()
        }
        val repo = UserRepository(datastore)

        repo.setUser(user, "session-token")

        assertEquals(user, repo.current.value)
        assertEquals("session-token", repo.authToken)
        coVerify(exactly = 1) { datastore.updateData(any()) }
    }

    @Test
    fun testSetUserWithNoUserThrowsAndDoesNotChangeState() = runTest {
        val datastore = mockk<DataStore<Preferences>>()
        val repo = UserRepository(datastore)

        try {
            repo.setUser(User.NoUser, "session-token")
            fail("Expected RuntimeException")
        } catch (ex: RuntimeException) {
            assertEquals("Cannot use login to logout", ex.message)
        }

        assertEquals(User.NoUser, repo.current.value)
        assertEquals("", repo.authToken)
    }

    @Test
    fun testClearUserResetsCurrentUserToNoUser() = runTest {
        val datastore = mockk<DataStore<Preferences>> {
            coEvery { updateData(any()) } returns emptyPreferences()
        }
        val repo = UserRepository(datastore)
        repo.setUser(user, "session-token")

        repo.clearUser()

        assertEquals(User.NoUser, repo.current.value)
        coVerify(exactly = 2) { datastore.updateData(any()) }
    }
}

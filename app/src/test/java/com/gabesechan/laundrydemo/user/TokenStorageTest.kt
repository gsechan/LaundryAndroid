package com.gabesechan.laundrydemo.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gabesechan.laundrydemo.login.TokenStorage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TokenStorageTest {

    private val tokenKey = stringPreferencesKey("token")

    @Test
    fun testInitFromDiskReturnsStoredToken() = runTest {
        val datastore = mockk<DataStore<Preferences>> {
            every { data } returns flowOf(preferencesOf(tokenKey to "stored-token"))
        }
        val storage = TokenStorage(datastore)

        assertEquals("stored-token", storage.initFromDisk())
    }

    @Test
    fun testInitFromDiskReturnsNullWhenNoTokenStored() = runTest {
        val datastore = mockk<DataStore<Preferences>> {
            every { data } returns flowOf(emptyPreferences())
        }
        val storage = TokenStorage(datastore)

        assertNull(storage.initFromDisk())
    }

    @Test
    fun testSetTokenUpdatesAuthTokenAndPersists() = runTest {
        val datastore = mockk<DataStore<Preferences>> {
            coEvery { updateData(any()) } returns emptyPreferences()
        }
        val storage = TokenStorage(datastore)

        storage.setToken("session-token")

        assertEquals("session-token", storage.authToken)
        coVerify(exactly = 1) { datastore.updateData(any()) }
    }

    @Test
    fun testClearTokenResetsAuthTokenAndPersists() = runTest {
        val datastore = mockk<DataStore<Preferences>> {
            coEvery { updateData(any()) } returns emptyPreferences()
        }
        val storage = TokenStorage(datastore)
        storage.setToken("session-token")

        storage.clearToken()

        assertEquals("", storage.authToken)
        coVerify(exactly = 2) { datastore.updateData(any()) }
    }
}

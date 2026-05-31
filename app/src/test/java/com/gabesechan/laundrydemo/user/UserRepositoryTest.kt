package com.gabesechan.laundrydemo.user

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import junit.framework.TestCase.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class UserRepositoryTest {
    @JvmField
    @Rule
    val temporaryFolder = TemporaryFolder.builder().assureDeletion().build()
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val jsonPreferenceKeys = stringPreferencesKey("json")

    // Build the test DataStore instance
    private val testDataStore = PreferenceDataStoreFactory.create(
        scope = testScope,
        produceFile = { temporaryFolder.newFile("test_user_prefs.preferences_pb") }
    )


    @Test
    fun testInitialUserIsNone() {
        val repo = UserRepository(testDataStore)
        assertEquals(User.NoUser, repo.current.value)
    }

    @Test
    fun settingUserSetsCurrent() = testScope.runTest {
        val repo = UserRepository(testDataStore)
        val user = User("1", "Gabe", "a", "b", emptyList())
        repo.setUser(user)
        assertEquals(user, repo.current.value)

        val updatedPrefs = testDataStore.data.first()
        assertEquals(Json.encodeToString(user), updatedPrefs[jsonPreferenceKeys])

    }

    @Test
    fun clearingUserSetsNoUser() = testScope.runTest {
        val user = User("1", "Gabe", "", "", emptyList())
        val repo = UserRepository(testDataStore)
        repo.setUser(user)
        repo.clearUser()
        assertEquals(User.NoUser, repo.current.value)

        val updatedPrefs = testDataStore.data.first()
        assertNull( updatedPrefs[jsonPreferenceKeys])
    }

    @Test
    fun settingNoUserThrowsException() = testScope.runTest{
        val repo = UserRepository(testDataStore)
        try {
            repo.setUser(User.NoUser)
            fail("Did not catch exception setting no user")
        }
        catch(ex: RuntimeException){}
    }

    @Test
    fun initFromDiskReadsDatastore() = testScope.runTest {
        val user = User("2", "Fake", "email", "phone", emptyList())
        val userJson = Json.encodeToString(user)
        val repo = UserRepository(testDataStore)
        testDataStore.edit {
            it[jsonPreferenceKeys] = userJson
        }
        repo.initFromDisk()
        assertEquals(user.name, repo.current.value.name)
        assertEquals(user.id, repo.current.value.id)
        assertEquals(user.phone, repo.current.value.phone)
        assertEquals(user.email, repo.current.value.email)

        val updatedPrefs = testDataStore.data.first()
        assertEquals(userJson, updatedPrefs[jsonPreferenceKeys])

    }

}
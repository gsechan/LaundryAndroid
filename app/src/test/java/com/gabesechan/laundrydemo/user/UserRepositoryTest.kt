package com.gabesechan.laundrydemo.user

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import junit.framework.TestCase.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class UserRepositoryTest {
    @JvmField
    @Rule
    val temporaryFolder = TemporaryFolder.builder().assureDeletion().build()
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val namePreferenceKey = stringPreferencesKey("Name")
    private val idPrefenceKey = stringPreferencesKey("id")
    private val emailPreferkeceKey = stringPreferencesKey("email")
    private val phonePreferenceKey = stringPreferencesKey("phone")

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
        val user = User.RealUser("1", "Gabe", "a", "b")
        repo.setUser(user)
        assertEquals(user, repo.current.value)

        val updatedPrefs = testDataStore.data.first()
        assertEquals("Gabe", updatedPrefs[namePreferenceKey])
        assertEquals("1", updatedPrefs[idPrefenceKey])
        assertEquals("a", updatedPrefs[emailPreferkeceKey])
        assertEquals("b", updatedPrefs[phonePreferenceKey])

    }

    @Test
    fun clearingUserSetsNoUser() = testScope.runTest {
        testDataStore.edit {
            it[namePreferenceKey] = "Fake"
            it[idPrefenceKey] = "2"
            it.writeOrRemove(emailPreferkeceKey, "email")
            it.writeOrRemove(phonePreferenceKey, "phone")

        }
        val repo = UserRepository(testDataStore)
        val user = User.RealUser("1", "Gabe", "", "")
        repo.setUser(user)
        repo.clearUser()
        assertEquals(User.NoUser, repo.current.value)

        val updatedPrefs = testDataStore.data.first()
        assertNull( updatedPrefs[namePreferenceKey])
        assertNull(updatedPrefs[idPrefenceKey])
        assertNull(updatedPrefs[emailPreferkeceKey])
        assertNull(updatedPrefs[phonePreferenceKey])

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
        val user = User.RealUser("2", "Fake", "email", "phone")
        val repo = UserRepository(testDataStore)
        testDataStore.edit {
            it[namePreferenceKey] = user.name
            it[idPrefenceKey] = user.id
            it.writeOrRemove(emailPreferkeceKey, user.email)
            it.writeOrRemove(phonePreferenceKey, user.phone)

        }
        repo.initFromDisk()
        assertEquals(user.name, repo.current.value.name)
        assertEquals(user.id, repo.current.value.id)
        assertEquals(user.phone, repo.current.value.phone)
        assertEquals(user.email, repo.current.value.email)

        val updatedPrefs = testDataStore.data.first()
        assertEquals(user.name, updatedPrefs[namePreferenceKey])
        assertEquals(user.id, updatedPrefs[idPrefenceKey])
        assertEquals(user.email, updatedPrefs[emailPreferkeceKey])
        assertEquals(user.phone, updatedPrefs[phonePreferenceKey])

    }

}
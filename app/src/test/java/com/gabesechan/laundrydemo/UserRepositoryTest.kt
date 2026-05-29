package com.gabesechan.laundrydemo

import com.gabesechan.laundrydemo.account.User
import com.gabesechan.laundrydemo.account.UserRepository
import org.junit.Test
import junit.framework.TestCase.*

class UserRepositoryTest {

    @Test
    fun testInitialUserIsNone() {
        val repo = UserRepository()
        assertEquals( User.NoUser, repo.current.value)
    }

    @Test
    fun settingUserSetsCurrent() {
        val repo = UserRepository()
        val user = User.RealUser("1", "Gabe", "", "")
        repo.setUser(user)
        assertEquals( user, repo.current.value)
    }

    @Test
    fun clearingUserSetsNoUser() {
        val repo = UserRepository()
        val user = User.RealUser("1", "Gabe", "", "")
        repo.setUser(user)
        repo.clearUser()
        assertEquals( User.NoUser, repo.current.value)
    }

    @Test
    fun settingNoUserThrowsException() {
        val repo = UserRepository()
        try {
            repo.setUser(User.NoUser)
            fail("Did not catch exception setting no user")
        }
        catch(ex: RuntimeException){}
    }


}
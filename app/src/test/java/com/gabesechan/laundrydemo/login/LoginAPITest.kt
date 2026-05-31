package com.gabesechan.laundrydemo.login

import com.gabesechan.laundrydemo.user.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import okio.IOException
import org.junit.Test
import kotlinx.coroutines.test.runTest
import junit.framework.TestCase.*

class LoginAPITest {

    @Test
    fun testLogoutClearsRepo() = runTest {
        val userRepo = mockk<UserRepository> {
            coEvery { clearUser() } returns Unit
        }
        val loginServer = mockk<LoginServer>()
        val api = LoginAPI(userRepo, loginServer)
        api.logout()
        coVerify(exactly=1) { userRepo.clearUser() }
    }

    @Test
    fun testLoginNetworkErrorReturnsError()= runTest {
        val userRepo = mockk<UserRepository>()
        val loginServer = mockk<LoginServer>{
            coEvery { login(any()) } throws IOException()
        }
        val api = LoginAPI(userRepo, loginServer)
        val result = api.login("","")
        assertEquals(LoginAPI.LoginResult.NetworkError, result)
    }

    @Test
    fun testLoginBadInfoReturnsFailure()= runTest {
        val userRepo = mockk<UserRepository>()
        val loginServer = mockk<LoginServer>{
            coEvery { login(any()) } returns LoginResponse(false, null)
        }
        val api = LoginAPI(userRepo, loginServer)
        val result = api.login("","")
        assertEquals(LoginAPI.LoginResult.LoginFailed, result)
    }

    @Test
    fun testLoginNoServerUserReturnsFailure()= runTest {
        val userRepo = mockk<UserRepository>()
        val loginServer = mockk<LoginServer>{
            coEvery { login(any()) } returns LoginResponse(true, null)
        }
        val api = LoginAPI(userRepo, loginServer)
        val result = api.login("","")
        assertEquals(LoginAPI.LoginResult.LoginFailed, result)
    }

    @Test
    fun testLoginSuccessSetsUserAndReturnsSuccess()= runTest {
        val userRepo = mockk<UserRepository>() {
            coEvery { setUser(any()) } returns Unit
        }
        val loginServer = mockk<LoginServer>{
            coEvery { login(any()) } returns
                    LoginResponse(true, LoginUser("1", "gabe","","", emptyList()))
        }
        val api = LoginAPI(userRepo, loginServer)
        val result = api.login("","")
        assertTrue(result is LoginAPI.LoginResult.LoginSuccess)
        coVerify {
            userRepo.setUser(
                match {
                    it.phone == "" && it.email == "" && it.id == "1" && it.name == "gabe"
                },
            )
        }
    }

}
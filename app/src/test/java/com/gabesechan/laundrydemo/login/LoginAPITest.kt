package com.gabesechan.laundrydemo.login

import com.gabesechan.laundrydemo.network.NetworkResponse
import com.gabesechan.laundrydemo.user.User
import com.gabesechan.laundrydemo.user.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.*
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.Test

class LoginAPITest {

    private val loginUser = LoginUser(
        "gabe",
        "gabe@example.com",
        "1234567890",
        emptyList()
    )

    @Test
    fun testLogoutCallsServerAndClearsRepo() = runTest {
        val userRepo = mockk<UserRepository> {
            coEvery { clearUser() } returns Unit
        }
        val loginServer = mockk<LoginServer> {
            coEvery { logout() } returns NetworkResponse(true, null, emptyList(), Unit)
        }
        val api = LoginAPI(userRepo, loginServer)
        api.logout()
        coVerify(exactly = 1) { loginServer.logout() }
        coVerify(exactly = 1) { userRepo.clearUser() }
    }

    @Test
    fun testLogoutWithoutServerLogoutSkipsServerCall() = runTest {
        val userRepo = mockk<UserRepository> {
            coEvery { clearUser() } returns Unit
        }
        val loginServer = mockk<LoginServer>()
        val api = LoginAPI(userRepo, loginServer)
        api.logout(serverLogout = false)
        coVerify(exactly = 0) { loginServer.logout() }
        coVerify(exactly = 1) { userRepo.clearUser() }
    }

    @Test
    fun testLogoutServerErrorStillClearsRepo() = runTest {
        val userRepo = mockk<UserRepository> {
            coEvery { clearUser() } returns Unit
        }
        val loginServer = mockk<LoginServer> {
            coEvery { logout() } throws IOException()
        }
        val api = LoginAPI(userRepo, loginServer)
        api.logout()
        coVerify(exactly = 1) { userRepo.clearUser() }
    }

    @Test
    fun testLoginNetworkErrorReturnsError() = runTest {
        val userRepo = mockk<UserRepository>()
        val loginServer = mockk<LoginServer> {
            coEvery { login(any()) } throws IOException()
        }
        val api = LoginAPI(userRepo, loginServer)
        val result = api.login("", "")
        assertEquals(LoginAPI.LoginResult.NetworkError, result)
    }

    @Test
    fun testLoginBadInfoReturnsFailure() = runTest {
        val userRepo = mockk<UserRepository>()
        val loginServer = mockk<LoginServer> {
            coEvery { login(any()) } returns NetworkResponse(false, "BAD_AUTH", emptyList(), null)
        }
        val api = LoginAPI(userRepo, loginServer)
        val result = api.login("", "")
        assertEquals(LoginAPI.LoginResult.LoginFailed, result)
    }

    @Test
    fun testLoginUnknownErrorReturnsFailure() = runTest {
        val userRepo = mockk<UserRepository>()
        val loginServer = mockk<LoginServer> {
            coEvery { login(any()) } returns NetworkResponse(false, "SOME_OTHER_ERROR", listOf("oops"), null)
        }
        val api = LoginAPI(userRepo, loginServer)
        val result = api.login("", "")
        assertEquals(LoginAPI.LoginResult.LoginFailed, result)
    }

    @Test
    fun testLoginUnexpectedExceptionRethrows() = runTest {
        val userRepo = mockk<UserRepository>()
        val loginServer = mockk<LoginServer> {
            coEvery { login(any()) } throws RuntimeException("boom")
        }
        val api = LoginAPI(userRepo, loginServer)
        try {
            api.login("", "")
            fail("Expected RuntimeException to be rethrown")
        } catch (ex: RuntimeException) {
            assertEquals("boom", ex.message)
        }
    }

    @Test
    fun testLoginSuccessSetsUserAndReturnsSuccess() = runTest {
        val userRepo = mockk<UserRepository> {
            coEvery { setUser(any(), any()) } returns Unit
        }
        val loginServer = mockk<LoginServer> {
            coEvery { login(any()) } returns NetworkResponse(
                true, null, emptyList(),
                LoginResponse("session-token", loginUser)
            )
        }
        val api = LoginAPI(userRepo, loginServer)
        val result = api.login("phone", "password")
        assertTrue(result is LoginAPI.LoginResult.LoginSuccess)
        val user = (result as LoginAPI.LoginResult.LoginSuccess).user
        assertEquals("gabe", user.name)
        assertEquals("gabe@example.com", user.email)
        assertEquals("1234567890", user.phone)
        coVerify(exactly = 1) {
            userRepo.setUser(
                match { it.name == "gabe" && it.email == "gabe@example.com" && it.phone == "1234567890" },
                "session-token"
            )
        }
    }

    @Test
    fun testCheckAuthSuccessSetsUserAndReturnsUser() = runTest {
        val userRepo = mockk<UserRepository> {
            coEvery { setUser(any(), any()) } returns Unit
        }
        val loginServer = mockk<LoginServer> {
            coEvery { checkAuth(any()) } returns NetworkResponse(true, null, emptyList(), loginUser)
        }
        val api = LoginAPI(userRepo, loginServer)
        val user = api.checkAuth("token")
        assertEquals("gabe", user.name)
        assertEquals("gabe@example.com", user.email)
        assertEquals("1234567890", user.phone)
        coVerify(exactly = 1) {
            userRepo.setUser(
                match { it.name == "gabe" },
                "token"
            )
        }
    }

    @Test
    fun testCheckAuthFailureLogsOutAndReturnsNoUser() = runTest {
        val userRepo = mockk<UserRepository> {
            coEvery { clearUser() } returns Unit
        }
        val loginServer = mockk<LoginServer> {
            coEvery { checkAuth(any()) } returns NetworkResponse(false, "BAD_AUTH", emptyList(), null)
        }
        val api = LoginAPI(userRepo, loginServer)
        val user = api.checkAuth("token")
        assertEquals(User.NoUser, user)
        coVerify(exactly = 0) { loginServer.logout() }
        coVerify(exactly = 1) { userRepo.clearUser() }
    }

    @Test
    fun testCreateAccountSuccessSetsUserAndReturnsSuccess() = runTest {
        val userRepo = mockk<UserRepository> {
            coEvery { setUser(any(), any()) } returns Unit
        }
        val loginServer = mockk<LoginServer> {
            coEvery { createAccount(any()) } returns NetworkResponse(
                true, null, emptyList(),
                CreateUserResponse("session-token", loginUser)
            )
        }
        val api = LoginAPI(userRepo, loginServer)
        val result = api.createAccount("gabe", "password", "1234567890", "gabe@example.com")
        assertTrue(result is LoginAPI.LoginResult.LoginSuccess)
        val user = (result as LoginAPI.LoginResult.LoginSuccess).user
        assertEquals("gabe", user.name)
        coVerify(exactly = 1) {
            userRepo.setUser(
                match { it.name == "gabe" },
                "session-token"
            )
        }
    }

    @Test
    fun testCreateAccountNetworkErrorReturnsNetworkError() = runTest {
        val userRepo = mockk<UserRepository>()
        val loginServer = mockk<LoginServer> {
            coEvery { createAccount(any()) } throws IOException()
        }
        val api = LoginAPI(userRepo, loginServer)
        val result = api.createAccount("gabe", "password", "1234567890", "gabe@example.com")
        assertEquals(LoginAPI.LoginResult.NetworkError, result)
    }

    @Test
    fun testUseSavedLoginWithTokenChecksAuthAndSetsUser() = runTest {
        val userRepo = mockk<UserRepository> {
            coEvery { initFromDisk() } returns "token"
            coEvery { setUser(any(), any()) } returns Unit
        }
        val loginServer = mockk<LoginServer> {
            coEvery { checkAuth(any()) } returns NetworkResponse(true, null, emptyList(), loginUser)
        }
        val api = LoginAPI(userRepo, loginServer)
        api.useSavedLogin()
        coVerify(exactly = 1) { loginServer.checkAuth(CheckAuthRequest("token")) }
        coVerify(exactly = 1) {
            userRepo.setUser(
                match { it.name == "gabe" },
                "token"
            )
        }
    }

    @Test
    fun testUseSavedLoginWithNoTokenLogsOutWithoutServerCall() = runTest {
        val userRepo = mockk<UserRepository> {
            coEvery { initFromDisk() } returns null
            coEvery { clearUser() } returns Unit
        }
        val loginServer = mockk<LoginServer>()
        val api = LoginAPI(userRepo, loginServer)
        api.useSavedLogin()
        coVerify(exactly = 0) { loginServer.checkAuth(any()) }
        coVerify(exactly = 0) { loginServer.logout() }
        coVerify(exactly = 1) { userRepo.clearUser() }
    }

    @Test
    fun testUseSavedLoginWithTokenButCheckAuthFailsLogsOut() = runTest {
        val userRepo = mockk<UserRepository> {
            coEvery { initFromDisk() } returns "token"
            coEvery { clearUser() } returns Unit
        }
        val loginServer = mockk<LoginServer> {
            coEvery { checkAuth(any()) } returns NetworkResponse(false, "BAD_AUTH", emptyList(), null)
        }
        val api = LoginAPI(userRepo, loginServer)
        api.useSavedLogin()
        coVerify(exactly = 1) { userRepo.clearUser() }
    }

    @Test
    fun testCreateAccountBadAuthReturnsNetworkError() = runTest {
        val userRepo = mockk<UserRepository>()
        val loginServer = mockk<LoginServer> {
            coEvery { createAccount(any()) } returns NetworkResponse(false, "BAD_AUTH", emptyList(), null)
        }
        val api = LoginAPI(userRepo, loginServer)
        val result = api.createAccount("gabe", "password", "1234567890", "gabe@example.com")
        assertEquals(LoginAPI.LoginResult.NetworkError, result)
    }
}

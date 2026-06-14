package com.gabesechan.laundrydemo.login

import com.gabesechan.laundrydemo.network.BadAuthException
import com.gabesechan.laundrydemo.user.Address
import com.gabesechan.laundrydemo.user.User
import com.gabesechan.laundrydemo.user.UserRepository
import okio.IOException
import javax.inject.Inject

class LoginAPI @Inject constructor(
    private var userRepository: UserRepository,
    private var loginServer: LoginServer
) {
    private val org = "eaf6aefc-33ef-4245-8ef9-fd87827f0000"

    suspend fun logout(serverLogout: Boolean = true) {
        if(serverLogout) {
            try {
                loginServer.logout()
            }
            catch(ex: Exception) {
                ex.printStackTrace()
            }
        }
        userRepository.clearUser()
    }

    sealed class LoginResult {
        class LoginSuccess(val user: User): LoginResult()
        object  LoginFailed: LoginResult()
        object NetworkError: LoginResult()
    }

    suspend fun login(username: String, password: String): LoginResult {
        try {
            val response = loginServer.login(
                LoginRequest(
                    username,
                    password,
                    org
                )
            ).process()
            val user = response.user.toModel()
            userRepository.setUser(user, response.session)
            return LoginResult.LoginSuccess(user)
        }
        catch(ex: Exception){
            ex.printStackTrace()
            when(ex) {
                is IOException -> return LoginResult.NetworkError
                is IllegalArgumentException, is BadAuthException -> return LoginResult.LoginFailed
            }
            throw ex
        }
    }

    suspend fun checkAuth(token: String): User {
        try {
            val response = loginServer.checkAuth(CheckAuthRequest(token)).process()
            val user = response.toModel()
            userRepository.setUser(user, token)
            return user
        }
        catch (ex: Exception) {
            ex.printStackTrace()
            logout(false)
            return User.NoUser
        }
    }

    suspend fun useSavedLogin() {
        val token = userRepository.initFromDisk()
        //If we're logged in, check the auth with the server for expiry issues
        if(token != null) {
            checkAuth(token)
        }
        else {
            logout(false)
        }

    }

    suspend fun createAccount(
        name: String,
        password: String,
        phone: String,
        email: String,
    ): LoginResult {
        val request = CreateUserRequest(
            LoginUser(
                name,
                email,
                phone,
                emptyList()
            ),
            password,
            org
        )
        try {
            val response = loginServer.createAccount(request).process()
            val user = response.user.toModel()
            userRepository.setUser(user, response.session)
            return LoginResult.LoginSuccess(user)
        }
        catch (ex: IOException) {
            return LoginResult.NetworkError
        }
        catch (ex: BadAuthException) {
            return LoginResult.NetworkError
        }
    }
}

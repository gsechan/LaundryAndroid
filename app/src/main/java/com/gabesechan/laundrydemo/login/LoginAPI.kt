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
    suspend fun logout() {
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
                    "eaf6aefc-33ef-4245-8ef9-fd87827f0000"
                )
            ).process()
            val user = User(
                response.user.name,
                response.user.email,
                response.user.phone,
                response.user.addresses.toAddress()
            )
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
            val user = User(
                response.name,
                response.email,
                response.phone,
                response.addresses.toAddress()
            )
            userRepository.setUser(user, token)
            return user
        }
        catch (ex: Exception) {
            ex.printStackTrace()
            logout()
            return User.NoUser
        }
    }
}

private fun List<LoginAddress>.toAddress(): List<Address> {
    return map {
        Address(
            it.id,
            it.street1,
            it.street2,
            it.city,
            it.state,
            it.country,
            it.postcode
        )
    }
}
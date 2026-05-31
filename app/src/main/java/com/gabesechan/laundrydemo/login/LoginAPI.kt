package com.gabesechan.laundrydemo.login

import com.gabesechan.laundrydemo.account.User
import com.gabesechan.laundrydemo.account.UserRepository
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

        fun isSuccess(): Boolean = this is LoginSuccess
    }

    suspend fun login(username: String, password: String): LoginResult {
        try {
            val response = loginServer.login(LoginRequest(username, password))
            if(response.success && response.user!= null) {
                val user = User.RealUser(
                    response.user.id,
                    response.user.name,
                    response.user.email,
                    response.user.phone
                )
                userRepository.setUser(user)
                return LoginResult.LoginSuccess(user)
            }
            return LoginResult.LoginFailed
        }
        catch(ex: IOException){
            ex.printStackTrace()
            return LoginResult.NetworkError
        }
    }
}
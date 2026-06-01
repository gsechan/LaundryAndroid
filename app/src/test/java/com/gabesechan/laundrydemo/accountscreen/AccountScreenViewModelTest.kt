package com.gabesechan.laundrydemo.accountscreen

import androidx.lifecycle.viewModelScope
import com.gabesechan.laundrydemo.login.LoginAPI
import com.gabesechan.laundrydemo.user.User
import com.gabesechan.laundrydemo.user.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.junit.Test

class AccountScreenViewModelTest {

    @Test
    fun clickingLogoutCallsLoginAPI() {
        val loginAPI = mockk<LoginAPI>() {
            coEvery { logout() } returns Unit
        }
        val userRepository = mockk<UserRepository>() {
            every { current } returns MutableStateFlow<User>(User.NoUser)
        }
        val viewModel = AccountScreenViewModel(loginAPI, userRepository)

        viewModel.onLogoutClicked()

        coVerify { loginAPI.logout() }
    }

}
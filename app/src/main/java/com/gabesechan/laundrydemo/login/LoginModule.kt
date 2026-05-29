package com.gabesechan.laundrydemo.login

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

    @Provides
    @Singleton
    fun provideLoginService(retrofit: Retrofit): LoginServer {
        return retrofit.create(LoginServer::class.java)
    }

}

package com.gabesechan.laundrydemo.laundromatinfo

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LaundromatInfoModule {

    @Provides
    @Singleton
    fun provideAvailableTimesServer(retrofit: Retrofit): LaundromatInfoServer {
        return retrofit.create(LaundromatInfoServer::class.java)
    }

}
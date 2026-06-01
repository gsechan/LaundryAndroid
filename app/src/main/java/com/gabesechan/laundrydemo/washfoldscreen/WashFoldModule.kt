package com.gabesechan.laundrydemo.washfoldscreen

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WashFoldModule {

    @Provides
    @Singleton
    fun provideAvailableTimesServer(retrofit: Retrofit): AvailableTimesServer {
        return retrofit.create(AvailableTimesServer::class.java)
    }

}

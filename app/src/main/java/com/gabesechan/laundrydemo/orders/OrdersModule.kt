package com.gabesechan.laundrydemo.orders

import com.gabesechan.laundrydemo.laundromatinfo.LaundromatInfoServer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OrdersModule {

    @Provides
    @Singleton
    fun provideOrdersServer(retrofit: Retrofit): OrdersServer {
        return retrofit.create(OrdersServer::class.java)
    }

}
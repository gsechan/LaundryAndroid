package com.gabesechan.laundrydemo

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val networkJson = Json { ignoreUnknownKeys = true }
        return  Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080")
            .addConverterFactory(networkJson.asConverterFactory("application/json; charset=utf-8".toMediaType())).build()
    }
}
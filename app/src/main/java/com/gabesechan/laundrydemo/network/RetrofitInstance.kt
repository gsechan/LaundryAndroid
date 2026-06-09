package com.gabesechan.laundrydemo.network

import android.content.Context
import com.gabesechan.laundrydemo.user.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(userRepo: UserRepository,
                        @ApplicationContext context: Context): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(userRepo))
            .addInterceptor(LocaleInterceptor(context))
            .build()
        val networkJson = Json { ignoreUnknownKeys = true }
        return  Retrofit.Builder()
            .baseUrl("http://18.188.76.18:8080")
            .client(okHttpClient)
            .addConverterFactory(networkJson.asConverterFactory("application/json; charset=utf-8".toMediaType())).build()
    }
}
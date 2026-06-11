package com.gabesechan.laundrydemo.login

import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.apache.commons.validator.routines.EmailValidator
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

    @Provides
    @Singleton
    fun providesLibPhoneNumber(): PhoneNumberUtil {
        return PhoneNumberUtil.getInstance()
    }

    @Provides
    @Singleton
    fun providesEmailValidator(): EmailValidator {
        return EmailValidator.getInstance()
    }
}

package com.artmine15.svod.modules

import com.artmine15.svod.repositories.firebase.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthRepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository() : AuthRepository{
        return AuthRepository()
    }
}
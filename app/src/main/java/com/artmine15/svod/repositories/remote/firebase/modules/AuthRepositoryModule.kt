package com.artmine15.svod.repositories.remote.firebase.modules

import com.artmine15.svod.repositories.remote.firebase.AuthRepository
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
package com.artmine15.svod.repositories.datastore.modules

import android.content.Context
import com.artmine15.svod.repositories.datastore.LocalUserDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalUserDataRepositoryModule {
    @Provides
    @Singleton
    fun provideLocalUserDataRepository(@ApplicationContext context: Context) : LocalUserDataRepository{
        return LocalUserDataRepository(context)
    }
}
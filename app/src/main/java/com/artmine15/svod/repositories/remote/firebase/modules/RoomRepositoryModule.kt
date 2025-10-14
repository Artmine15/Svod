package com.artmine15.svod.repositories.remote.firebase.modules

import com.artmine15.svod.repositories.remote.firebase.RoomRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomRepositoryModule {
    @Provides
    @Singleton
    fun provideRoomRepository() : RoomRepository{
        return RoomRepository()
    }
}
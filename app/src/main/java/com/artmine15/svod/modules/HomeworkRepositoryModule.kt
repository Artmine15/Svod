package com.artmine15.svod.modules

import com.artmine15.svod.repositories.firebase.HomeworkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeworkRepositoryModule {
    @Provides
    @Singleton
    fun provideHomeworkRepository() : HomeworkRepository{
        return HomeworkRepository()
    }
}
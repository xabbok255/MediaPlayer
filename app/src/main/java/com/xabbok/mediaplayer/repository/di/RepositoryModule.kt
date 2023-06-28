package com.xabbok.mediaplayer.repository.di

import com.xabbok.mediaplayer.repository.AlbumRepository
import com.xabbok.mediaplayer.repository.AlbumRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {
    @Singleton
    @Binds
    fun bindsAlbumRepository(impl: AlbumRepositoryImpl) : AlbumRepository
}
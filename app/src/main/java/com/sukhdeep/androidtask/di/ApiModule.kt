package com.sukhdeep.androidtask.di

import com.sukhdeep.androidtask.api.RestaurantService
import com.sukhdeep.androidtask.api.local.ImpLocalDataSource
import com.sukhdeep.androidtask.api.local.LocalDataSource
import com.sukhdeep.androidtask.api.remote.ImpRemoteDataSource
import com.sukhdeep.androidtask.api.remote.RemoteDataSource
import com.sukhdeep.androidtask.db.dao.RestaurantDao
import com.sukhdeep.androidtask.repository.ImpRestaurantServiceRepository
import com.sukhdeep.androidtask.repository.RestaurantServiceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {
    @Singleton
    @Provides
    fun provideRemoteDataSource(service: RestaurantService): RemoteDataSource =
        ImpRemoteDataSource(service)

    @Singleton
    @Provides
    fun provideLocalDataSource(dao: RestaurantDao): LocalDataSource = ImpLocalDataSource(dao)

    @Singleton
    @Provides
    fun provideRestaurantRepository(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource
    ): RestaurantServiceRepository =
        ImpRestaurantServiceRepository(remoteDataSource, localDataSource)

}
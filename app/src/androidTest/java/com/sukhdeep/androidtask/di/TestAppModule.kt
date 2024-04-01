package com.sukhdeep.androidtask.di

import android.content.Context
import androidx.room.Room
import com.sukhdeep.androidtask.db.RestaurantDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Named("test_db")
    fun provideRestaurantDB(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(context, RestaurantDatabase::class.java)
            .allowMainThreadQueries().build()
}
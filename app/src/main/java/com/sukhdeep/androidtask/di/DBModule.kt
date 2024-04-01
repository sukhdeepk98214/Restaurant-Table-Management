package com.sukhdeep.androidtask.di

import android.content.Context
import androidx.room.Room
import com.sukhdeep.androidtask.db.RestaurantDatabase
import com.sukhdeep.androidtask.utils.Constants.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {
    @Singleton
    @Provides
    fun provideYourDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        RestaurantDatabase::class.java,
        DB_NAME
    ).fallbackToDestructiveMigration()
        .allowMainThreadQueries().build()

    @Singleton
    @Provides
    fun provideRestaurantDao(db: RestaurantDatabase) = db.restaurantDao()

}
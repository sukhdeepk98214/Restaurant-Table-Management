package com.sukhdeep.androidtask.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sukhdeep.androidtask.db.dao.RestaurantDao
import com.sukhdeep.androidtask.db.entity.Customer
import com.sukhdeep.androidtask.db.entity.TableInfo

@Database(version = 1, entities = [TableInfo::class, Customer::class], exportSchema = false)
abstract class RestaurantDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao
}
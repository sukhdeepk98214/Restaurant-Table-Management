package com.sukhdeep.androidtask.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sukhdeep.androidtask.db.entity.Customer
import com.sukhdeep.androidtask.db.entity.TableInfo

@Dao
interface RestaurantDao {
    @Query("SELECT * FROM tableInfo")
    fun getTableList(): List<TableInfo>

    @Query("SELECT * FROM customer")
    fun getCustomerList(): List<Customer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTableList(tables: List<TableInfo>)

    @Query("UPDATE tableInfo SET customer_id = :customerId WHERE tableId = :tableId")
    suspend fun updateReservationList(tableId: Long, customerId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCustomerInfo(customers: List<Customer>)

    @Query("SELECT * FROM customer WHERE customerId =:customerId")
    suspend fun getCustomerData(customerId: Int): Customer
}
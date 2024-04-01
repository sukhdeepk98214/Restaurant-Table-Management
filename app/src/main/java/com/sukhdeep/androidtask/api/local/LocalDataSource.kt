package com.sukhdeep.androidtask.api.local

import com.sukhdeep.androidtask.db.entity.Customer
import com.sukhdeep.androidtask.db.entity.TableInfo

interface LocalDataSource {
    suspend fun getAllTableList(): List<TableInfo>
    suspend fun insertAllTableListInDb(restaurantTableInfo: List<TableInfo>)
    suspend fun updateReservationData(tableId: Long, customerId: Int)
    suspend fun getCustomerInfoList(): List<Customer>
    suspend fun insertAllCustomerIntoInDb(customerInfo: List<Customer>)
    suspend fun getCustomerData(customerId: Int): Customer


}
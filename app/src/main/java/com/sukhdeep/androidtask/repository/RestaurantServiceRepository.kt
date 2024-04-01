package com.sukhdeep.androidtask.repository

import com.sukhdeep.androidtask.api.ApiResult
import com.sukhdeep.androidtask.db.entity.Customer
import com.sukhdeep.androidtask.db.entity.TableInfo

interface RestaurantServiceRepository {
    suspend fun getCustomersList(): ApiResult<List<Customer>, String>
    suspend fun getReservationDataList(): ApiResult<List<TableInfo>, String>
    suspend fun getCustomerData(customerId: Int): Customer
    suspend fun updateReservationData(tableId: Long, customerId: Int)
}
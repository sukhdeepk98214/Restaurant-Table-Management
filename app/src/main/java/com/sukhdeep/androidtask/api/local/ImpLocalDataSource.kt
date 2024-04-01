package com.sukhdeep.androidtask.api.local

import com.sukhdeep.androidtask.db.dao.RestaurantDao
import com.sukhdeep.androidtask.db.entity.Customer
import com.sukhdeep.androidtask.db.entity.TableInfo
import javax.inject.Inject

class ImpLocalDataSource @Inject constructor(private val dao: RestaurantDao) : LocalDataSource {
    override suspend fun getAllTableList(): List<TableInfo> {
        return dao.getTableList()
    }

    override suspend fun insertAllTableListInDb(restaurantTableInfo: List<TableInfo>) {
        return dao.insertAllTableList(restaurantTableInfo)
    }

    override suspend fun updateReservationData(tableId: Long, customerId: Int) {
        return dao.updateReservationList(tableId, customerId)
    }

    override suspend fun getCustomerInfoList(): List<Customer> {
        return dao.getCustomerList()
    }

    override suspend fun insertAllCustomerIntoInDb(customerInfo: List<Customer>) {
        return dao.insertAllCustomerInfo(customerInfo)
    }

    override suspend fun getCustomerData(customerId: Int): Customer {
        return dao.getCustomerData(customerId)
    }
}
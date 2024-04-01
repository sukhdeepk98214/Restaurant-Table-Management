package com.sukhdeep.androidtask.api.remote

import com.sukhdeep.androidtask.model.CustomerResponse
import com.sukhdeep.androidtask.model.TableResponse

interface RemoteDataSource {
    suspend fun getCustomerList(): List<CustomerResponse>?
    suspend fun getTableList(): List<TableResponse>?
}
package com.sukhdeep.androidtask.repository

import com.sukhdeep.androidtask.api.ApiResult
import com.sukhdeep.androidtask.api.local.LocalDataSource
import com.sukhdeep.androidtask.api.remote.RemoteDataSource
import com.sukhdeep.androidtask.db.entity.Customer
import com.sukhdeep.androidtask.db.entity.TableInfo
import com.sukhdeep.androidtask.db.entity.toCustomerEntity
import com.sukhdeep.androidtask.db.entity.toTableEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class ImpRestaurantServiceRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : RestaurantServiceRepository {
    override suspend fun getCustomersList(): ApiResult<List<Customer>, String> =
        withContext(dispatcher) {
            val storedCustomerData = localDataSource.getCustomerInfoList()
            when {
                storedCustomerData.isEmpty() -> {
                    val customerList = mutableListOf<Customer>()
                    try {
                        remoteDataSource.getCustomerList()?.map {
                            customerList.add(it.toCustomerEntity())
                        }

                        localDataSource.insertAllCustomerIntoInDb(customerList)

                        ApiResult.OnSuccess(customerList)
                    } catch (e: Exception) {
                        when (e) {
                            is IOException -> ApiResult.NetworkError
                            else -> ApiResult.OnFailure(
                                e.message ?: "Something went wrong, please try again."
                            )
                        }
                    }
                }

                else -> ApiResult.OnSuccess(storedCustomerData)
            }
        }

    override suspend fun getReservationDataList(): ApiResult<List<TableInfo>, String> =
        withContext(dispatcher) {
            val storedTableList = localDataSource.getAllTableList()
            when {
                storedTableList.isEmpty() -> {
                    val tableInfoList = mutableListOf<TableInfo>()
                    try {
                        remoteDataSource.getTableList()?.map {
                            tableInfoList.add(it.toTableEntity())
                        }
                        localDataSource.insertAllTableListInDb(tableInfoList)

                        ApiResult.OnSuccess(tableInfoList)
                    } catch (e: Exception) {
                        when (e) {
                            is IOException -> ApiResult.NetworkError
                            else -> ApiResult.OnFailure(
                                e.message ?: "Something went wrong, please try again."
                            )
                        }
                    }
                }

                else -> ApiResult.OnSuccess(storedTableList)
            }
        }

    override suspend fun getCustomerData(customerId: Int): Customer =
        localDataSource.getCustomerData(customerId)

    override suspend fun updateReservationData(tableId: Long, customerId: Int) =
        localDataSource.updateReservationData(tableId, customerId)
}
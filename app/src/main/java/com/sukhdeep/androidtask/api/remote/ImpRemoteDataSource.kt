package com.sukhdeep.androidtask.api.remote

import com.sukhdeep.androidtask.api.RestaurantService
import com.sukhdeep.androidtask.model.CustomerResponse
import com.sukhdeep.androidtask.model.ReservationResponse
import com.sukhdeep.androidtask.model.TableResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


class ImpRemoteDataSource @Inject constructor(private val restaurantService: RestaurantService) :
    RemoteDataSource {

    override suspend fun getCustomerList(): List<CustomerResponse>? {
        try {
            val response = restaurantService.customers()
            if (response.isSuccessful) {
                return response.body()
            } else {
                throw Exception(response.message())
            }
        } catch (exception: Exception) {
            throw exception
        }
    }

    override suspend fun getTableList(): List<TableResponse> = coroutineScope {
        try {
            val reservations = async { restaurantService.reservations() }
            val tables = async { restaurantService.tables() }

            val reservationResponse = reservations.await()
            val tablesResponse = tables.await()

            val isSuccess = reservationResponse.isSuccessful && tablesResponse.isSuccessful
            if (isSuccess) {
                createReservationData(reservationResponse.body(), tablesResponse.body())
            } else {
                throw Exception("Something went wrong.")
            }
        } catch (exception: Exception) {
            throw exception
        }
    }

    private fun createReservationData(
        reservations: List<ReservationResponse>?,
        tables: List<TableResponse>?
    ): List<TableResponse> {
        tables?.map {
            it.customerId = -1
        }

        reservations?.map { reserveInfo ->
            tables?.find {
                it.id == reserveInfo.tableId
            }?.customerId = reserveInfo.userId
        }
        return tables ?: emptyList()
    }
}
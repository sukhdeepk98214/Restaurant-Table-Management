package com.sukhdeep.androidtask.api

import com.sukhdeep.androidtask.model.CustomerResponse
import com.sukhdeep.androidtask.model.ReservationResponse
import com.sukhdeep.androidtask.model.TableResponse
import retrofit2.Response
import retrofit2.http.GET

interface RestaurantService {
    @GET("/quandoo-assessment/customers.json")
    suspend fun customers(): Response<List<CustomerResponse>>


    @GET("/quandoo-assessment/reservations.json")
    suspend fun reservations(): Response<List<ReservationResponse>>

    @GET("/quandoo-assessment/tables.json")
    suspend fun tables(): Response<List<TableResponse>>

}
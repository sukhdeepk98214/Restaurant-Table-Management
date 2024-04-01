package com.sukhdeep.androidtask.model

import com.google.gson.annotations.SerializedName

data class ReservationResponse(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("table_id") val tableId: Long,
    @SerializedName("id") val id: Long
)
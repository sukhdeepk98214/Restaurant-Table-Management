package com.sukhdeep.androidtask.model

import com.google.gson.annotations.SerializedName

data class TableResponse(
    @SerializedName("shape") val shape: String,
    @SerializedName("id") val id: Long,
    var customerId: Int = -1
)
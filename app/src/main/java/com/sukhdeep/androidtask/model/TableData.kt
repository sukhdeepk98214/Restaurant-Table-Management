package com.sukhdeep.androidtask.model

data class TableData(
    val shape: String,
    val tableId: Long = 0,
    var customerId: Int = -1,
    val customerName: String,
    val imgUrl: String?,
)

package com.sukhdeep.androidtask.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sukhdeep.androidtask.model.TableResponse

@Entity
data class TableInfo(
    @PrimaryKey(autoGenerate = false) val tableId: Long,
    @ColumnInfo(name = "table_shape") val tableShape: String,
    @ColumnInfo(name = "customer_id") val customerId: Int
)

fun TableResponse.toTableEntity() = TableInfo(
    tableShape = shape,
    tableId = id,
    customerId = customerId
)
package com.sukhdeep.androidtask.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sukhdeep.androidtask.model.CustomerResponse

@Entity
data class Customer(
    @PrimaryKey(autoGenerate = false) val customerId: Int,
    @ColumnInfo(name = "customer_firstname") val customerFirstName: String,
    @ColumnInfo(name = "customer_lastname") val customerLastName: String,
    @ColumnInfo(name = "image_url") val imgUrl: String?
)

fun CustomerResponse.toCustomerEntity() = Customer(
    customerId = id,
    customerFirstName = firstName,
    customerLastName = lastName,
    imgUrl = imageUrl
)

fun Customer.getCustomerFullName() = "$customerFirstName $customerLastName"
package com.sukhdeep.androidtask.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sukhdeep.androidtask.api.ApiResult
import com.sukhdeep.androidtask.base.BaseViewModel
import com.sukhdeep.androidtask.db.entity.Customer
import com.sukhdeep.androidtask.db.entity.TableInfo
import com.sukhdeep.androidtask.db.entity.getCustomerFullName
import com.sukhdeep.androidtask.model.TableData
import com.sukhdeep.androidtask.repository.RestaurantServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class RestaurantViewModel @Inject constructor(private val repository: RestaurantServiceRepository) :
    BaseViewModel() {
    private val _tableMutableLiveData: MutableLiveData<List<TableData>> = MutableLiveData()
    val tableMutableList: LiveData<List<TableData>> = _tableMutableLiveData

    private val _customerMutableLiveData: MutableLiveData<List<Customer>?> = MutableLiveData()
    val customerMutableList: LiveData<List<Customer>?> = _customerMutableLiveData

    var selectedTableId: Long? = null
    private val mutex = Mutex()

    init {
        viewModelScope.launch {
            fetchCustomerData()
            fetchTableReservationData()
        }
    }

    private suspend fun fetchCustomerData() {
        when (val result = repository.getCustomersList()) {
            is ApiResult.NetworkError -> {
                stopLoader()
                networkError.postValue(true)
            }

            is ApiResult.OnSuccess -> {
                stopLoader()
                _customerMutableLiveData.value = result.response
            }

            is ApiResult.OnFailure -> {
                stopLoader()
                displayError.postValue(result.exception)
            }
        }
    }

    private suspend fun fetchTableReservationData() {
        when (val result = repository.getReservationDataList()) {
            is ApiResult.NetworkError -> {
                stopLoader()
                networkError.postValue(true)
            }

            is ApiResult.OnSuccess -> {
                stopLoader()
                getTableData(result.response)
            }

            is ApiResult.OnFailure -> {
                stopLoader()
                displayError.postValue(result.exception)
            }
        }
    }

    private suspend fun getTableData(tableList: List<TableInfo>) {
        val tableDataList = mutableListOf<TableData>()

        tableList.map { tableEntity ->
            val customerId = tableEntity.customerId
            if (customerId != -1) {
                val customerEntity = repository.getCustomerData(customerId)
                tableDataList.add(tableEntity.toTableData(customerEntity))
            } else {
                tableDataList.add(tableEntity.toTableData(null))
            }
            _tableMutableLiveData.postValue(tableDataList)
        }
    }

    fun updateTableReservation(customerId: Int) = viewModelScope.launch {
        mutex.withLock {
            selectedTableId?.let { tableId ->
                repository.updateReservationData(tableId, customerId)
                fetchTableReservationData()
            }
        }
    }

    fun hasUserReservedTable(customerId: Int) = tableMutableList.value?.find {
        it.customerId == customerId
    }

    private fun TableInfo.toTableData(customer: Customer?) = TableData(
        shape = tableShape,
        tableId = tableId,
        customerId = customerId,
        customerName = customer?.getCustomerFullName() ?: "",
        imgUrl = customer?.imgUrl
    )

    private fun stopLoader() = isLoading.postValue(false)

}
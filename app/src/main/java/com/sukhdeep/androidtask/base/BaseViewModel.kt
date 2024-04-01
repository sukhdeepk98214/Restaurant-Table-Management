package com.sukhdeep.androidtask.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    protected val isLoading = MutableLiveData<Boolean>(true)
    protected val displayError = MutableLiveData<String>()
    protected val networkError = MutableLiveData<Boolean>()

    fun getIsLoadingLiveData(): LiveData<Boolean> {
        return isLoading
    }

    fun getDisplayErrorLiveData(): LiveData<String> {
        return displayError
    }

    fun networkError(): LiveData<Boolean> {
        return networkError
    }
}
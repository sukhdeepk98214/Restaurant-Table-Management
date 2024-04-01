package com.sukhdeep.androidtask.api

sealed class ApiResult<out R, out E> {

    object NetworkError : ApiResult<Nothing, Nothing>()

    data class OnSuccess<out R>(val response: R) : ApiResult<R, Nothing>()

    data class OnFailure<out E>(val exception: E) : ApiResult<Nothing, E>()
}
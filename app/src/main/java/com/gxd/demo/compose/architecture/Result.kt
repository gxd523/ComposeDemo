package com.gxd.demo.compose.architecture

sealed class Result<out R> {
    object Loading : Result<Nothing>()

    class Success<T>(val data: T) : Result<T>()

    class Error(val exception: Throwable) : Result<Nothing>()
}
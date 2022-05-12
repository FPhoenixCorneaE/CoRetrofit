package com.fphoenixcorneae.coretrofit.model

import com.fphoenixcorneae.coretrofit.exception.ApiException
import com.fphoenixcorneae.coretrofit.exception.ExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @desc：自定义结果集封装类
 * @date：2021/4/4 15:21
 */
sealed class Result<out T> {
    companion object {
        fun <T> onSuccess(data: T?): Result<T> = Success(data)
        fun <T> onLoading(loadingMsg: String?): Result<T> = Loading(loadingMsg)
        fun <T> onError(exception: ApiException): Result<T> = Error(exception)
    }

    data class Success<out T>(val data: T?) : Result<T>()
    data class Loading(val loadingMsg: String?) : Result<Nothing>()
    data class Error(val exception: ApiException) : Result<Nothing>()
}


/**
 * 处理返回值
 * @param result 请求结果
 */
fun <T> MutableStateFlow<Result<T>?>.paresResult(result: BaseResponse<T>) {
    value = if (result.isSuccess()) {
        Result.onSuccess(result.getResponseData())
    } else {
        Result.onError(ApiException(result.getResponseCode(), result.getResponseMsg()))
    }
}

/**
 * 不处理返回值 直接返回请求结果
 * @param result 请求结果
 */
fun <T> MutableStateFlow<Result<T>?>.paresResult(result: T) {
    value = Result.onSuccess(result)
}

/**
 * 异常转换异常处理
 */
fun <T> MutableStateFlow<Result<T>?>.paresException(e: Throwable) {
    this.value = Result.onError(ExceptionHandler.handleException(e))
}


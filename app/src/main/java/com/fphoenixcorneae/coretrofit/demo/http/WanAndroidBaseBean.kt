package com.fphoenixcorneae.coretrofit.demo.http

import androidx.annotation.Keep
import com.fphoenixcorneae.coretrofit.model.BaseResponse
import java.io.Serializable

/**
 * 封装返回的数据
 * 成员视服务器返回格式而定
 */
@Keep
data class WanAndroidBaseBean<T>(
    val errorCode: Int,
    val errorMsg: String?,
    val data: T?
) : BaseResponse<T>(), Serializable {
    /**
     * WanAndroid网站返回的 错误码为 0 就代表请求成功
     */
    override fun isSuccess(): Boolean = errorCode == 0

    override fun getResponseData(): T? = data

    override fun getResponseCode(): Int = errorCode

    override fun getResponseMsg(): String? = errorMsg
}
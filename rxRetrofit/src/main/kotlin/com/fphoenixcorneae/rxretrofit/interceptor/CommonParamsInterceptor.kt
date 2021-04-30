package com.fphoenixcorneae.rxretrofit.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * @desc：公共请求参数拦截器，传入 commonParams
 * @date：2021/4/4 15:33
 */
class CommonParamsInterceptor(
    private val commonParams: Map<String, String?>? = null
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val builder = originalRequest.url().newBuilder()
        if (commonParams.isNullOrEmpty().not()) {
            val keys = commonParams!!.keys
            for (paramKey in keys) {
                commonParams[paramKey]?.let {
                    builder.addQueryParameter(paramKey, it).build()
                }
            }
        }
        val modifiedUrl = builder.build()
        val request = originalRequest.newBuilder().url(modifiedUrl).build()
        return chain.proceed(request)
    }
}
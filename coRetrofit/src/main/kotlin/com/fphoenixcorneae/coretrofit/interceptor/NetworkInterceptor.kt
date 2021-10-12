package com.fphoenixcorneae.coretrofit.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @desc：NetworkInterceptor
 * @date：2021/10/12 16:44
 */
class NetworkInterceptor(private val maxAge: Long = 60 * 60) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val cacheControl = originalResponse.header("Cache-Control")
        return if (cacheControl == null
            || cacheControl.contains("no-store")
            || cacheControl.contains("no-cache")
            || cacheControl.contains("must-revalidate")
            || cacheControl.contains("max-age=0")
        ) {
            originalResponse.newBuilder()
                .header("Cache-Control", "public, max-age=$maxAge")
                .build()
        } else {
            originalResponse
        }
    }
}
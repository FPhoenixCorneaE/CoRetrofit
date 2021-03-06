package com.fphoenixcorneae.coretrofit.interceptor

import com.fphoenixcorneae.common.ext.isNetworkConnected
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @desc：缓存拦截器
 * @date：2021/4/4 15:33
 * @param day 缓存天数 默认7天
 */
class CacheInterceptor(var day: Int = 7) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = if (isNetworkConnected) {
            request.newBuilder()
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build()
        } else {
            // tolerate 4-weeks stale
            val maxStale = 60 * 60 * 24 * day
            request.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                .build()
        }
        return chain.proceed(request)
    }
}
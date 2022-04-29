package com.fphoenixcorneae.coretrofit

import com.fphoenixcorneae.common.ext.applicationContext
import com.fphoenixcorneae.coretrofit.factory.CoroutinesCallAdapterFactory
import com.fphoenixcorneae.coretrofit.interceptor.*
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.GsonBuilder
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @desc：网络请求构建器基类
 * @date：2021/4/4 17:52
 */
object RetrofitFactory {

    private const val TIMEOUT_CONNECT = 10L
    private const val TIMEOUT_READ = 10L
    private const val TIMEOUT_WRITE = 10L
    private const val NET_CACHE_DIR = "netCache"
    private const val NET_CACHE_MAX_SIZE = 10 * 1024 * 1024L

    /**
     * 公共请求头
     */
    var headers: Map<String, String?>? = null

    /**
     * 公共请求参数
     */
    var commonParams: Map<String, String?>? = null

    /**
     * 获取Service
     */
    inline fun <reified T> createService(): T = mRetrofit.create(T::class.java)

    /**
     * 清空Cookies
     */
    fun clearCookies() {
        mCookieJar.clear()
    }

    /**
     * 在这里可以添加拦截器，可以对 OkHttpClient.Builder 做任意操作
     */
    private fun OkHttpClient.Builder.setHttpClientBuilder(): OkHttpClient.Builder = apply {
        // 添加公共请求头 headers, 注意要设置在日志拦截器之前，不然 Log 中会不显示 header 信息
        addInterceptor(HeaderInterceptor(headers))
        // 添加公共请求参数 commonParams
        addInterceptor(CommonParamsInterceptor(commonParams))
        // 添加网络拦截器
        addNetworkInterceptor(NetworkInterceptor())
        // 添加缓存拦截器 可传入缓存天数，不传默认7天
        addInterceptor(CacheInterceptor())
        // 日志拦截器
        addInterceptor(HttpLoggingInterceptor())
        // 连接池
        connectionPool(ConnectionPool(200, 5, TimeUnit.MINUTES))
        // 错误重连
        retryOnConnectionFailure(true)
        // 设置缓存配置 缓存最大 10M
        cache(Cache(File(applicationContext.cacheDir, NET_CACHE_DIR), NET_CACHE_MAX_SIZE))
        // 添加 Cookies 自动持久化
        cookieJar(mCookieJar)
        // 超时时间 连接、读、写
        connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
        readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
        writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)
    }

    /**
     * 在这里可以对 Retrofit.Builder 做任意操作，比如添加解析器：Gson、Protocol
     */
    private fun Retrofit.Builder.setRetrofitBuilder(): Retrofit.Builder = apply {
        addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        addCallAdapterFactory(CoroutinesCallAdapterFactory())
    }

    /**
     * Cookies 自动持久化
     */
    private val mCookieJar: PersistentCookieJar by lazy {
        PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(applicationContext))
    }

    /**
     * OkHttpClient
     */
    private val mOkHttpClient: OkHttpClient by lazy {
        RetrofitUrlManager.getInstance().with(OkHttpClient.Builder())
            .setHttpClientBuilder()
            .build()
    }

    /**
     * RetrofitBuilder
     */
    val mRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.baidu.com")
            .client(mOkHttpClient)
            .setRetrofitBuilder()
            .build()
    }
}




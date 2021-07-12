# **CoRetrofit**

## **基于 Retrofit2、OkHttp3、Coroutine 的网络请求框架**

### How to include it in your project:
**Step 1.** Add the JitPack repository to your build file

**groovy**
```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
**kotlin**
```kotlin
allprojects {
	repositories {
		...
		maven { setUrl("https://jitpack.io") }
	}
}
```

**Step 2.** Add the dependency

**groovy**
```groovy
dependencies {
	implementation("com.github.FPhoenixCorneaE:RxRetrofit:1.0.0")
}
```
**kotlin**
```kotlin
dependencies {
	implementation("com.github.FPhoenixCorneaE:RxRetrofit:1.0.0")
}
```


### 一、**异常处理封装**

- **网络连接超时**
    - **SocketTimeoutException**
    - **ConnectTimeoutException**
- **网络错误**
    - **SocketException**
    - **ConnectException**
- **Http 错误**
    - **HttpException**

- **数据解析错误**
    - **JsonParseException**
    - **JSONException**
    - **ParseException**
    - **MalformedJsonException**
    - **NumberFormatException**
- **服务器内部错误**
    - **ApiException**
- **参数错误**
    - **IllegalArgumentException**
- **证书错误**
    - **SSLException**
    - **SSLHandshakeException**
- **未知主机**
    - **UnknownHostException**
    - **UnknownServiceException**
- **未知错误**

### 二、**Retrofit 封装**
- **公共请求头拦截器：HeaderInterceptor**
```kotlin
/**
 * @desc：头部参数拦截器，传入 headers
 * @date：2021/4/4 15:33
 */
class HeaderInterceptor(
    private val headers: Map<String, String?>? = null
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        if (headers.isNullOrEmpty().not()) {
            val keys = headers!!.keys
            for (headerKey in keys) {
                headers[headerKey]?.let {
                    builder.addHeader(headerKey, it).build()
                }
            }
        }
        builder.addHeaders()
        // 请求信息
        return chain.proceed(builder.build())
    }

    private fun Request.Builder.addHeaders(): Request.Builder {
        return addHeader("Content_Type", "application/json")
            .addHeader("Accept", "application/json")
            .addHeader("charset", "UTF-8")
            .addHeader("Connection", "close")
    }
}
```
- **公共请求参数拦截器：CommonParamsInterceptor**
```kotlin
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
```
- **缓存拦截器：CacheInterceptor**

```kotlin
/**
 * @desc：缓存拦截器
 * @date：2021/4/4 15:33
 * @param day 缓存天数 默认7天
 */
class CacheInterceptor(var day: Int = 7) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (!NetworkUtil.isConnected) {
            request = request.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()
        }
        val response = chain.proceed(request)
        if (!NetworkUtil.isConnected) {
            val maxAge = 60 * 60
            response.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, max-age=$maxAge")
                .build()
        } else {
            // tolerate 4-weeks stale
            val maxStale = 60 * 60 * 24 * day
            response.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                .build()
        }
        return response
    }
}
```

- **日志拦截器：HttpLoggingInterceptor**
- **Cookies 自动持久化**

```kotlin
/**
 * Cookies 自动持久化
 */
private val mCookieJar: PersistentCookieJar by lazy {
    PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(ContextUtil.context))
}
```

- **添加公共请求头**
```
/**
 * 公共请求头
 */
var headers: Map<String, String?>? = null
```

- **添加公共请求参数**
```
/**
 * 公共请求参数
 */
var commonParams: Map<String, String?>? = null
```

- **获取 ServiceApi**
```kotlin
/**
 * 获取 ServiceApi
 */
fun <T> getApi(
    serviceClass: Class<T>,
    baseUrl: String
): T = mRetrofitBuilder.baseUrl(baseUrl).build().create(serviceClass)
```

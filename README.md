# **CoRetrofit**

## **基于 Coroutines + Retrofit2 + OkHttp3 的网络请求框架**

[![](https://jitpack.io/v/FPhoenixCorneaE/CoRetrofit.svg)](https://jitpack.io/#FPhoenixCorneaE/CoRetrofit)

### How to include it in your project:

**Step 1.** Add the JitPack repository to your build file

**groovy**

```groovy
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**kotlin**

```kotlin
allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

**Step 2.** Add the dependency

```kotlin
dependencies {
    implementation("com.github.FPhoenixCorneaE:CoRetrofit:$latest")
}
```

<br>

### 一、RetrofitFactory

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
        request = if (NetworkUtil.isConnected) {
            request.newBuilder()
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build()
        } else {
            request.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()
        }
        val response = chain.proceed(request)
        return if (NetworkUtil.isConnected) {
            val maxAge = 60 * 60
            response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", "public, max-age=$maxAge")
                .build()
        } else {
            // tolerate 4-weeks stale
            val maxStale = 60 * 60 * 24 * day
            response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                .build()
        }
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
    PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(applicationContext))
}
```

- **添加公共请求头**

```kotlin
/**
 * 公共请求头
 */
var headers: Map<String, String?>? = null
```

> 示例：
>
> ```kotlin
> // 设置公共请求头
> RetrofitFactory.headers = hashMapOf("platform" to "Android", "androidID" to androidID)
> ```

- **添加公共请求参数**

```kotlin
/**
 * 公共请求参数
 */
var commonParams: Map<String, String?>? = null
```

> 示例：
>
> ```kotlin
> // 设置公共请求参数
> RetrofitFactory.commonParams = hashMapOf("token" to androidID.md5())
> ```

- **获取Service**

```kotlin
/**
 * 获取Service
 */
inline fun <reified T> createService(): T = mRetrofit.create(T::class.java)
```

> 示例：
>
> ```kotlin
> /** 双重校验锁式-单例, 封装 Service, 方便直接快速调用接口 */
> val commonService by lazy {
>     RetrofitFactory.createService<CommonService>()
> }
> ```

* **清空Cookies**

```kotlin
/**
 * 清空Cookies
 */
fun clearCookies() {
    mCookieJar.clear()
}
```

<br>

### 二、解决Retrofit多BaseUrl及运行时动态改变BaseUrl：[RetrofitUrlManager](https://github.com/JessYanCoding/RetrofitUrlManager)

* ##### 1) 初始化所有的BaseUrl

```kotlin
RetrofitUrlManager.getInstance().setDebug(isDebuggable)
// 将每个 BaseUrl 进行初始化,运行时可以随时改变 DOMAIN_NAME 对应的值,从而达到切换 BaseUrl 的效果
RetrofitUrlManager.getInstance().putDomain(UrlConstant.DOMAIN_WAN_ANDROID, UrlConstant.BASE_URL_WAN_ANDROID)
RetrofitUrlManager.getInstance().putDomain(UrlConstant.DOMAIN_EYEPETIZER, UrlConstant.BASE_URL_EYEPETIZER)
```

* ##### 2) 设置全局的BaseUrl

```kotlin
// 设置全局的 BaseUrl
RetrofitUrlManager.getInstance().setGlobalDomain(UrlConstant.BASE_URL_WAN_ANDROID)
```

* ##### 3) 需要动态改变BaseUrl时，在网络接口上添加注解 @Headers

```kotlin
/**
 * 玩安卓-首页Banner
 */
@Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER.plus(UrlConstant.DOMAIN_WAN_ANDROID))
@GET("banner/json")
suspend fun getBannerList(): WanAndroidBaseBean<ArrayList<WanAndroidBannerBean>>

/**
 * 开眼-获取分类
 */
@Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER.plus(UrlConstant.DOMAIN_EYEPETIZER))
@GET("v4/categories")
suspend fun getCategory(): ArrayList<OpenEyesCategoryBean>
```

* 更多模式玩法与替换规则
    - [解决Retrofit多BaseUrl及运行时动态改变BaseUrl(一)](https://www.jianshu.com/p/2919bdb8d09a)
    - [解决Retrofit多BaseUrl及运行时动态改变BaseUrl(二)](https://www.jianshu.com/p/35a8959c2f86)

<br>

### 三、可自定义服务器返回数据的基类

如果你请求服务器返回的数据有基类（没有可忽略），例如：

```json
{
  "data": ...,
  "errorCode": 0,
  "errorMsg": ""
}
```

基类继承**BaseResponse<T>**，请求时框架可以帮你自动脱壳，自动判断是否请求成功。

```kotlin
/**
 * @desc： 服务器返回数据的基类
 * 如果你的项目中有基类，那美滋滋，可以继承BaseResponse，请求时框架可以帮你自动脱壳，自动判断是否请求成功，怎么做：
 * 1.继承 BaseResponse
 * 2.重写 isSuccess 方法，编写你的业务需求，根据自己的条件判断数据是否请求成功
 * 3.重写 getResponseCode、getResponseData、getResponseMsg方法，传入你的 code data msg
 * @date：2021/11/17 11:39
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
```

<br>

### 四、异常处理

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

> 示例：
>
> ```kotlin
> /**
>  * 异常转换异常处理
>  */
> fun <T> MutableStateFlow<Result<T>>.paresException(e: Throwable) {
>     this.value = Result.onError(ExceptionHandler.handleException(e))
> }
> ```

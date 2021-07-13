package com.fphoenixcorneae.coretrofit.exception

import com.fphoenixcorneae.ext.loggerE
import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.net.*
import java.text.ParseException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException

/**
 * @desc：异常处理类
 * @date：2021/4/4 14:13
 */
object ExceptionHandle {

    fun handleException(t: Throwable?): ApiException {
        val e = when (t) {
            is ApiException -> t
            is HttpException -> when (t.code()) {
                Error.HTTP_UNAUTHORIZED.getCode() -> ApiException(Error.HTTP_UNAUTHORIZED, t)
                Error.HTTP_FORBIDDEN.getCode() -> ApiException(Error.HTTP_FORBIDDEN, t)
                Error.HTTP_NOT_FOUND.getCode() -> ApiException(Error.HTTP_NOT_FOUND, t)
                Error.HTTP_REQUEST_TIMEOUT.getCode() -> ApiException(Error.HTTP_REQUEST_TIMEOUT, t)
                Error.HTTP_GATEWAY_TIMEOUT.getCode() -> ApiException(Error.HTTP_GATEWAY_TIMEOUT, t)
                Error.HTTP_INTERNAL_SERVER_ERROR.getCode() -> ApiException(Error.HTTP_INTERNAL_SERVER_ERROR, t)
                Error.HTTP_BAD_GATEWAY.getCode() -> ApiException(Error.HTTP_BAD_GATEWAY, t)
                Error.HTTP_SERVICE_UNAVAILABLE.getCode() -> ApiException(Error.HTTP_SERVICE_UNAVAILABLE, t)
                else -> ApiException(t.code(), "网络错误，请稍后重试！")
            }
            is JsonParseException,
            is JSONException,
            is ParseException,
            is MalformedJsonException,
            is NumberFormatException -> ApiException(Error.PARSE_ERROR, t)
            is SocketException,
            is ConnectException -> ApiException(Error.NETWORK_ERROR, t)
            is SocketTimeoutException,
            is ConnectTimeoutException -> ApiException(Error.TIMEOUT_ERROR, t)
            is UnknownHostException,
            is UnknownServiceException -> ApiException(Error.UNKNOWN_HOST, t)
            is IllegalArgumentException -> ApiException(Error.PARAMS_ERROR, t)
            is SSLException,
            is SSLHandshakeException -> ApiException(Error.SSL_ERROR, t)
            else -> ApiException(Error.UNKNOWN, t)
        }

        loggerE("errorCode:${e.errCode} errorMsg:${e.errorMsg}")
        return e
    }
}

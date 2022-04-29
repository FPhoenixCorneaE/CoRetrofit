package com.fphoenixcorneae.coretrofit.exception

/**
 * @desc：自定义错误信息异常
 * @date：2021/3/3 17:02
 */
class ApiException : Exception {

    /**
     * 错误消息
     */
    var errorMsg: String

    /**
     * 错误码
     */
    var errorCode: Int = 0

    constructor(errCode: Int, error: String?) : super(error) {
        this.errorCode = errCode
        this.errorMsg = error ?: "请求失败，请稍后重试！"
    }

    constructor(error: Error, e: Throwable?) {
        errorCode = error.getCode()
        errorMsg = error.getErrorMsg()
    }
}
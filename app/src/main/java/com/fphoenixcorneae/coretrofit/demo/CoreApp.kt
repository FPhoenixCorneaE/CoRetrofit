package com.fphoenixcorneae.coretrofit.demo

import android.app.Application
import com.fphoenixcorneae.common.ext.algorithm.md5
import com.fphoenixcorneae.common.ext.androidID
import com.fphoenixcorneae.common.ext.isDebuggable
import com.fphoenixcorneae.coretrofit.RetrofitFactory
import me.jessyan.retrofiturlmanager.RetrofitUrlManager

/**
 * @desc：CoreApp
 * @date：2022/04/29 13:55
 */
class CoreApp : Application() {

    override fun onCreate() {
        super.onCreate()

        RetrofitUrlManager.getInstance().setDebug(isDebuggable)
        // 将每个 BaseUrl 进行初始化,运行时可以随时改变 DOMAIN_NAME 对应的值,从而达到切换 BaseUrl 的效果
        RetrofitUrlManager.getInstance().putDomain(UrlConstant.DOMAIN_WAN_ANDROID, UrlConstant.BASE_URL_WAN_ANDROID)
        RetrofitUrlManager.getInstance().putDomain(UrlConstant.DOMAIN_EYEPETIZER, UrlConstant.BASE_URL_EYEPETIZER)
        // 设置全局的 BaseUrl
        RetrofitUrlManager.getInstance().setGlobalDomain(UrlConstant.BASE_URL_WAN_ANDROID)

        // 设置公共请求头
        RetrofitFactory.headers = hashMapOf("platform" to "Android", "androidID" to androidID)
        // 设置公共请求参数
        RetrofitFactory.commonParams = hashMapOf("token" to androidID.md5())
    }
}
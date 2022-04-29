package com.fphoenixcorneae.coretrofit.demo

import com.fphoenixcorneae.coretrofit.RetrofitFactory
import com.fphoenixcorneae.coretrofit.demo.http.OpenEyesCategoryBean
import com.fphoenixcorneae.coretrofit.demo.http.WanAndroidBannerBean
import com.fphoenixcorneae.coretrofit.demo.http.WanAndroidBaseBean
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import retrofit2.http.GET
import retrofit2.http.Headers

/** 双重校验锁式-单例, 封装 Service, 方便直接快速调用接口 */
val commonService by lazy {
    RetrofitFactory.createService<CommonService>()
}

/**
 * @desc：CommonService
 * @date：2022/04/29 13:55
 */
interface CommonService {

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
}
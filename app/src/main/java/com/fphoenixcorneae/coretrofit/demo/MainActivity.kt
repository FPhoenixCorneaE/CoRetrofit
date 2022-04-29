package com.fphoenixcorneae.coretrofit.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fphoenixcorneae.common.ext.loge
import com.fphoenixcorneae.coretrofit.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)
    }

    fun getBannerList(view: android.view.View) {
        lifecycleScope.launchWhenResumed {
            runCatching {
                commonService.getBannerList()
            }.onSuccess {

            }.onFailure {
                "getBannerList: $it".loge("CoRetrofit")
            }
        }
    }

    fun getCategory(view: android.view.View) {
        lifecycleScope.launchWhenResumed {
            runCatching {
                commonService.getCategory()
            }.onSuccess {

            }.onFailure {
                "getCategory: $it".loge("CoRetrofit")
            }
        }
    }
}
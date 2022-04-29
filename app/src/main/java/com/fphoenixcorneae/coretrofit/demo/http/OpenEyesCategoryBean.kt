package com.fphoenixcorneae.coretrofit.demo.http

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 * @desc 分类 Bean
 */
@Keep
@Parcelize
data class OpenEyesCategoryBean(
    val id: Long,
    val name: String,
    val description: String,
    val bgPicture: String,
    val bgColor: String,
    val headerImage: String
) : Serializable, Parcelable

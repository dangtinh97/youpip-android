package org.youpip.app.model

import com.google.gson.annotations.SerializedName

data class BaseApi <T>(
    @SerializedName("status") val status: Int,
    @SerializedName("content") val content:String,
    @SerializedName("data") val data:T?
        )
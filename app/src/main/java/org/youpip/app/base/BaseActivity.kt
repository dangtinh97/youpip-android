package org.youpip.app.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.youpip.app.network.ApiService

abstract class BaseActivity:AppCompatActivity() {

    val callApi: ApiService by lazy { ApiService.getClient() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setViewBinding()
        onCreateBase()
    }

    abstract fun setViewBinding()

    abstract fun onCreateBase()
}
package org.youpip.app.views.activity

import org.youpip.app.base.BaseActivity
import org.youpip.app.databinding.ActivitySplashBinding
import org.youpip.app.network.RequiresApi
import org.youpip.app.network.RequiresApi.Companion.callApi

class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun setViewBinding() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreateBase() {
        val home = callApi.home("")
        RequiresApi.callApi(this,home){
            if(it===null){
                println("====>${it}")
                return@callApi
            }
            println("====>${it}")
        }
    }

}
package org.youpip.app.views.activity

import android.content.Intent
import com.google.gson.internal.LinkedTreeMap
import org.youpip.app.MainActivity
import org.youpip.app.base.BaseActivity
import org.youpip.app.databinding.ActivitySplashBinding
import org.youpip.app.network.RequiresApi


class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun setViewBinding() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreateBase() {
        var username = mySharePre.getString("username")
        if(username==null){
            username = ""
        }
        val login = callApi.login(username.toString())
        RequiresApi.callApi(this, login) {
            if (it === null) {
                return@callApi
            }
            val data = it.data as LinkedTreeMap<*, *>
            val token = data.get("token").toString()
            mySharePre.saveString("token", "Bearer $token")
            mySharePre.saveString("username", data["username"].toString())
            mySharePre.saveString("short_username",data["short_username"].toString())
            val myIntent = Intent(this, MainActivity::class.java)
            startActivity(myIntent)
            finish()
        }
    }
}
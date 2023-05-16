package org.youpip.app.views.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.gson.internal.LinkedTreeMap
import org.youpip.app.BuildConfig
import org.youpip.app.MainActivity
import org.youpip.app.base.BaseActivity
import org.youpip.app.databinding.ActivitySplashBinding
import org.youpip.app.network.RequiresApi
import android.provider.Settings
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.AccessController.getContext
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

private const val KEY_ALIAS = "youpip.net"
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun setViewBinding() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreateBase() {
        println("====>unique:${generateKey()}")
        var username = mySharePre.getString("username")
        if(username==null){
            username = generateKey()
        }
        val login = callApi.login(username.toString())
        RequiresApi.callApi(this, login) {
            println("====>exLogin${it}")
            if (it === null) {
                return@callApi
            }
            val data = it.data as LinkedTreeMap<*, *>
            val token = data["token"].toString()
            mySharePre.saveBoolean("PASS_REVIEW",data["version_review"].toString() == BuildConfig.VERSION_NAME)
            mySharePre.saveString("token", "Bearer $token")
            mySharePre.saveString("username", data["username"].toString())
            mySharePre.saveString("short_username",data["short_username"].toString())
            mySharePre.saveString("full_name",data["full_name"].toString())
            val myIntent = Intent(this, MainActivity::class.java)
            val extras = intent.extras
            if (extras != null) {
                val value1 = extras.getString(Intent.EXTRA_TEXT)
                mySharePre.saveString("YOUTUBE",value1.toString())
                myIntent.putExtra("YOUTUBE",value1)
            }
            startActivity(myIntent)
            finish()
        }
    }

    private fun generateKey(): String {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) as String;
    }
}
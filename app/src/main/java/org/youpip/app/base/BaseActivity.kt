package org.youpip.app.base

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.youpip.app.model.Video
import org.youpip.app.network.ApiService
import org.youpip.app.utils.MySharePre
import java.util.Observable
import javax.security.auth.callback.Callback

abstract class BaseActivity:AppCompatActivity() {
    lateinit var mySharePre: MySharePre
    val callApi: ApiService by lazy { ApiService.getClient() }
    lateinit var token:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mySharePre = MySharePre(this)
        token = mySharePre.getString("token").toString()
        setViewBinding()
        onCreateBase()
    }

    abstract fun setViewBinding()

    abstract fun onCreateBase()

    fun delay(time:Int, callback: (Boolean) -> Unit){
        val handler = Handler()
        handler.postDelayed(Runnable { // Do something after 5s = 5000ms
            callback(true)
        }, (time*1000).toLong())
    }

    fun alert(str:String){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show()
    }
}
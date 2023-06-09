package org.youpip.app.base

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.youpip.app.BuildConfig
import org.youpip.app.dialog.MessageActivityNotification
import org.youpip.app.model.Video
import org.youpip.app.network.ApiService
import org.youpip.app.utils.MySharePre
import java.util.Observable
import javax.security.auth.callback.Callback
import kotlin.system.exitProcess

abstract class BaseActivity:AppCompatActivity() {
    lateinit var mySharePre: MySharePre
    val callApi: ApiService by lazy { ApiService.getClient() }
    lateinit var token:String
    private var time:Long = 0;
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

    override fun onBackPressed() {
        val timeMilli: Long = System.currentTimeMillis()
        if(timeMilli-time<1500){
            finishAndRemoveTask();
            exitProcess(0)
        }
        time = timeMilli
        alert("Nhấn lần nữa để thoát!")
        return
    }
}
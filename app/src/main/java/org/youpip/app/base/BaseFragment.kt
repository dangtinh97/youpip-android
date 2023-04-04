package org.youpip.app.base

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import org.youpip.app.MainActivity
import org.youpip.app.R
import org.youpip.app.interfacem.IOnBackEvent
import org.youpip.app.network.ApiService
import org.youpip.app.utils.MySharePre

abstract class BaseFragment:Fragment(),IOnBackEvent {
    lateinit var fragmentManager: FragmentTransaction
    private lateinit var fragmentView: View
    val mActivity: BaseActivity by lazy { activity as BaseActivity }
    val callApi: ApiService by lazy { ApiService.getClient() }
    lateinit var mySharePre: MySharePre
    lateinit var token:String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
        if (!this::fragmentView.isInitialized){
            fragmentView = view
            mySharePre = MySharePre(mActivity.baseContext)
            token = mySharePre.getString("token").toString()
            mySharePre.remove("SCREEN")
            onInitialized()
        }

        onViewCreateBase(view, savedInstanceState)
        super.onViewCreated(view, savedInstanceState)
    }

    abstract fun onViewCreateBase(view: View, savedInstanceState: Bundle?)
    abstract fun onInitialized()

    override fun onBackEvent(): Boolean {
        showBack()
        return true
    }

    fun showBack(){
        val countStack = parentFragmentManager.backStackEntryCount
        if (countStack>0){
            parentFragmentManager.popBackStack()
        } else {
            onFinish()
        }
    }

    private fun onFinish() {
//        onClickDoubleFinish()
    }

    fun showNextNoAddStack(fragment: Fragment){
        val idFragment = R.id.frame_container_fragment
        val isFragment = parentFragmentManager.findFragmentById(idFragment)?.javaClass?.name?.split(".")?.last() //Fragment đang hiển thị
        fragmentManager = if (isFragment == null ){
            childFragmentManager.beginTransaction()
        } else {
            parentFragmentManager.beginTransaction()
        }
        fragmentManager
            .replace(idFragment, fragment )
            .commit()
    }

    fun alert(text:String){
        Toast.makeText((mActivity as MainActivity).baseContext,text,Toast.LENGTH_SHORT).show()
    }
}
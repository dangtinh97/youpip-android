package org.youpip.app.views.fragment.social

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.internal.LinkedTreeMap
import org.youpip.app.MainActivity
import org.youpip.app.R
import org.youpip.app.base.BaseFragment
import org.youpip.app.databinding.FragmentListChatBinding
import org.youpip.app.databinding.FragmentSearchFriendBinding
import org.youpip.app.model.ListChatModel
import org.youpip.app.model.MessageModel
import org.youpip.app.network.RequiresApi
import java.util.*

class SearchFriendFragment : BaseFragment() {
    private lateinit var binding:FragmentSearchFriendBinding
    private lateinit var btnBack:ImageView
    private lateinit var ipSearch:TextInputEditText
    private lateinit var formResult:RelativeLayout
    private lateinit var fullName:TextView
    private var userOid:String? = null
    override fun onViewCreateBase(view: View, savedInstanceState: Bundle?) {
        btnBack.setOnClickListener {
            showNextNoAddStack(ListChatFragment())
        }

        formResult.visibility = View.INVISIBLE

        formResult.setOnClickListener {
            if(userOid==null){
                return@setOnClickListener
            }
            redirectChat(userOid.toString())
        }

        ipSearch.addTextChangedListener(object : TextWatcher {
            private var timer: Timer = Timer()
            private val DELAY: Long = 1000 // Milliseconds

            override fun afterTextChanged(s: Editable?) {
                userOid = null
                timer.cancel()
                timer = Timer()
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            (mActivity as MainActivity).runOnUiThread {
                                search(s.toString())
                            }

                        }
                    },
                    DELAY
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        });
    }

    private fun redirectChat(userOid:String)
    {
        val home = callApi.joinRoom(token,userOid)
        RequiresApi.callApi(mActivity.baseContext,home){
            if(it===null || it.status!=200){
                return@callApi
            }

            val data = it.data as LinkedTreeMap<*, *>
            (mActivity as MainActivity).showNavigationBottom(false)
            showNextNoAddStack(ChatFragment(
                ListChatModel(
                    roomOid = data["room_oid"].toString(),
                    fullName = data["full_name"].toString(),
                    message = "",
                    time = "",
                    userId = data["user_id"].toString().replace(".0","").toInt()
                )
            ))
        }
    }

    private fun search(value:String)
    {
        formResult.visibility = View.INVISIBLE
        userOid = null
        if(value.isEmpty()){

            return
        }
        (mActivity as MainActivity).progressBar(true)
        val search = callApi.searchUser(token, username = value.replace(" ",""))
        RequiresApi.callApi(mActivity.baseContext, search) {
            (mActivity as MainActivity).progressBar(false)
            if (it === null || it.status != 200) {
                return@callApi
            }
            formResult.visibility = View.VISIBLE
            val data = it.data as LinkedTreeMap<*, *>
            userOid = data["user_oid"].toString()
            fullName.text = data["full_name"].toString()
        }
    }

    override fun onInitialized() {
        btnBack = binding.back
        ipSearch = binding.ipSearch
        formResult = binding.formResult
        fullName = binding.fullName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::binding.isInitialized){
            binding = FragmentSearchFriendBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

}
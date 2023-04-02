package org.youpip.app.views.fragment.social

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.internal.LinkedTreeMap
import org.youpip.app.MainActivity
import org.youpip.app.R
import org.youpip.app.adapter.ChatAdapter
import org.youpip.app.adapter.ItemVideoMoreAdapter
import org.youpip.app.base.BaseFragment
import org.youpip.app.databinding.FragmentHomeSocialBinding
import org.youpip.app.databinding.FragmentListChatBinding
import org.youpip.app.model.CommentModel
import org.youpip.app.model.ListChatModel
import org.youpip.app.network.RequiresApi
import java.util.ArrayList

class ListChatFragment : BaseFragment() {
    private lateinit var binding:FragmentListChatBinding
    private lateinit var adapter:ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnBack:ImageView
    override fun onViewCreateBase(view: View, savedInstanceState: Bundle?) {

    }

    private fun loadData(){
        (mActivity as MainActivity).progressBar(true)
        val list = callApi.listChat(
            token = mySharePre.getString("token").toString(),
            id = ""
        )

        RequiresApi.callApi(requireContext(),list){
            if(it==null){
                return@callApi
            }
            (mActivity as MainActivity).progressBar(false)
            val data = it.data as LinkedTreeMap<*, *>
            val list = data["list"] as ArrayList<*>
            val listData = arrayListOf<ListChatModel>()
            list.forEach { item ->
                item as LinkedTreeMap<*, *>
                listData.add(
                    ListChatModel(
                        roomOid = item["room_oid"].toString(),
                        fullName = item["full_name"].toString(),
                        userId = item["user_id"].toString().replace(".0","").toInt(),
                        message = item["message"].toString(),
                        time = item["time"].toString()
                    )
                )
            }
            adapter.setData(listData)
        }
    }

    override fun onInitialized() {
        recyclerView = binding.listChat
        btnBack = binding.back
        btnBack.setOnClickListener {
            showNextNoAddStack(FeedFragment())

            return@setOnClickListener
        }
        customRecyclerView()
        loadData()
        onMessageSocket()
    }

    private fun customRecyclerView(){
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = ChatAdapter{
            (mActivity as MainActivity).showNavigationBottom(false)
            showNextNoAddStack(ChatFragment(it))
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized){
            binding = FragmentListChatBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    fun onMessageSocket()
    {
        (mActivity as MainActivity).socket.on("PUSH_ROOM"){
            println("====>onMessage")
        }
    }
}
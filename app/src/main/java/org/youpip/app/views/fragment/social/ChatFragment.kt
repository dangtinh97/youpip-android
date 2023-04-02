package org.youpip.app.views.fragment.social

import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.internal.LinkedTreeMap
import org.json.JSONObject
import org.youpip.app.ESocket
import org.youpip.app.MainActivity
import org.youpip.app.adapter.MessageAdapter
import org.youpip.app.base.BaseFragment
import org.youpip.app.databinding.FragmentChatBinding
import org.youpip.app.model.ListChatModel
import org.youpip.app.model.MessageModel
import org.youpip.app.network.RequiresApi

class ChatFragment(val chatModel:ListChatModel) : BaseFragment() {
    private lateinit var binding:FragmentChatBinding
    private lateinit var adapter: MessageAdapter
    private lateinit var recyclerView:RecyclerView
    private lateinit var btnBack:ImageView
    private lateinit var btnSubmit:ImageView
    private lateinit var ipChat:EditText
    private lateinit var fullName:TextView
    private lateinit var onlineIcon:ImageView

    override fun onViewCreateBase(view: View, savedInstanceState: Bundle?) {
        mySharePre.saveString("SCREEN","ChatFragment")
        activity?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }

    override fun onInitialized() {
        btnBack = binding.back
        btnSubmit = binding.submit
        ipChat = binding.ipChat
        recyclerView = binding.recyclerView
        onlineIcon = binding.online
        joinRoom()
        btnBack.setOnClickListener {
            (mActivity as MainActivity).showKeyboard(false)
            showNextNoAddStack(ListChatFragment())
            Handler().postDelayed({
                (mActivity as MainActivity).showNavigationBottom(true)
            }, 500)
        }
        fullName = binding.fullName
        fullName.text = chatModel.fullName

        btnSubmit.setOnClickListener {
            (mActivity as MainActivity).showKeyboard(true)
            sendMessage()
            return@setOnClickListener
        }
        customRecyclerView()
        loadData()
        onListenerSocket()
    }


    private fun onListenerSocket()
    {
        val socket = (mActivity as MainActivity).socket
        socket.on(ESocket.Message.value){
            val data = it[0] as JSONObject
            (mActivity as MainActivity).runOnUiThread {
                adapter.appendLast(
                    MessageModel(
                        data.getString("content"),
                        false,
                        null
                    )
                )
                recyclerView.scrollToPosition(adapter.itemCount - 1);
            }
        }

        socket.on(ESocket.JoinRoom.value){
            val data = it[0] as JSONObject
            if(data.getBoolean("otherOnline")){
                (mActivity as MainActivity).runOnUiThread {
                    onlineIcon.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun joinRoom()
    {
        val obj = JSONObject()
        obj.put("room_oid",chatModel.roomOid)
        println("====>socket:${chatModel.roomOid}")
        (mActivity as MainActivity).socket.emit(ESocket.JoinRoom.value,obj)
    }

    private fun sendMessage()
    {
        val content = ipChat.text.toString().trim()
        if(content==""){
            return
        }
        adapter.appendLast(
            MessageModel(
                content,
                true,
                null
            )
        )
        recyclerView.scrollToPosition(adapter.itemCount - 1);
        sendMessage(content)
        ipChat.setText("")
        val obj = JSONObject()
        obj.put("room_oid",chatModel.roomOid);
        obj.put("content",content);
        (mActivity as MainActivity).socket.emit(ESocket.Message.value,obj)
    }

    private fun customRecyclerView(){
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd = true;
        adapter = MessageAdapter()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::binding.isInitialized){
            binding = FragmentChatBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    private fun sendMessage(message: String) {
        val home = callApi.sendMessage(token, id = chatModel.roomOid, message = message)
        RequiresApi.callApi(mActivity.baseContext, home) {
            (mActivity as MainActivity).progressBar(false)
            if (it === null || it.status != 200) {
                return@callApi
            }

            val data = it.data as LinkedTreeMap<*, *>
            val mess = data["message"].toString()
            if (mess != "") {
                adapter.appendLast(
                    MessageModel(
                        mess,
                        false,
                        null
                    )
                )
                recyclerView.scrollToPosition(adapter.itemCount - 1);
            }
        }
    }

    private fun loadData()
    {
        (mActivity as MainActivity).progressBar(true)
        val home = callApi.message(token,id=chatModel.roomOid, lastOid = "")
        RequiresApi.callApi(mActivity.baseContext,home){
            (mActivity as MainActivity).progressBar(false)
            if(it===null || it.status!=200){
                return@callApi
            }

            val data = it.data as LinkedTreeMap<*, *>
            val list = data["list"] as ArrayList<*>
            val listChat = arrayListOf<MessageModel>()
            list.forEach { item->
                item as LinkedTreeMap<*, *>
                val model = MessageModel(
                    message = item["message"].toString(),
                    me = item["from_me"].toString().toBoolean(),
                    null
                )
                listChat.add(model)
            }
            adapter.setData(listChat)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (mActivity as MainActivity).socket.emit(ESocket.LeaveRoom.value,chatModel.roomOid)
    }
}
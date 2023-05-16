package org.youpip.app.views.fragment.social

import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.collect.ImmutableList
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
    private var isLoadMore:Boolean = false
    private lateinit var layoutManager:LinearLayoutManager
    private var lastOid:String? = null
    private lateinit var noImage:ImageView
    private lateinit var sending:TextView
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
        noImage = binding.noImage
        sending = binding.sending
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
        //        joinRoom()
//        onListenerSocket()

        initOnScroll()
    }

    private fun initOnScroll(){
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
            {
                super.onScrolled(recyclerView, dx, dy)

                if (!isLoadMore)
                {
                    //findLastCompletelyVisibleItemPostition() returns position of last fully visible view.
                    ////It checks, fully visible view is the last one.
                    if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0)
                    {
                        isLoadMore = true
                        loadData()
                    }
                }
            }
        })
    }

    private fun onListenerSocket()
    {
        val socket = (mActivity as MainActivity).socket
        socket.on(ESocket.Message.value){
            val data = it[0] as JSONObject
            (mActivity as MainActivity).runOnUiThread {
                adapter.appendLast(
                    MessageModel(
                        null,
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
        noImage.visibility = View.GONE
        val content = ipChat.text.toString().trim()
        if(content==""){
            return
        }
        adapter.appendLast(
            MessageModel(
                null,
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
//        (mActivity as MainActivity).socket.emit(ESocket.Message.value,obj)
    }

    private fun customRecyclerView(){
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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
        (mActivity as MainActivity).progressBar(false)
        sending.visibility = View.VISIBLE
        val home = callApi.sendMessage(token, id = chatModel.roomOid, message = message)
        RequiresApi.callApi(mActivity.baseContext, home) {
            sending.visibility = View.GONE
            if (it === null || it.status != 200) {
                return@callApi
            }

            val data = it.data as LinkedTreeMap<*, *>
            val mess = data["message"].toString()
            if (mess != "") {
                adapter.appendLast(
                    MessageModel(
                        null,
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
        val home = callApi.message(token,id=chatModel.roomOid, lastOid = lastOid.toString())
        RequiresApi.callApi(mActivity.baseContext,home){ it ->
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
                    lastOid = item["message_oid"].toString(),
                    message = item["message"].toString(),
                    me = item["from_me"].toString().toBoolean(),
                    null
                )
                listChat.add(model)
            }

            if(listChat.size>0){
                lastOid = listChat[0].lastOid.toString()
            }

            if(isLoadMore && listChat.size>0){
                val newList = ImmutableList.copyOf(listChat).reverse()
                newList.forEach { item->
                    adapter.appendFirst(item)
                }
            }

            if(!isLoadMore && listChat.size>0){
                adapter.setData(listChat)
            }
            isLoadMore = false
            if(adapter.itemCount == 0){
                noImage.visibility = View.VISIBLE
            }else{
                noImage.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        (mActivity as MainActivity).socket.emit(ESocket.LeaveRoom.value,chatModel.roomOid)
        mySharePre.remove("SCREEN")
    }
}
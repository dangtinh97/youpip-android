package org.youpip.app.views.fragment.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.gson.internal.LinkedTreeMap
import com.yuyakaido.android.cardstackview.*
import org.youpip.app.MainActivity
import org.youpip.app.adapter.DatingAdapter
import org.youpip.app.base.BaseFragment
import org.youpip.app.databinding.FragmentHomeSocialBinding
import org.youpip.app.model.ListChatModel
import org.youpip.app.model.PostModel
import org.youpip.app.network.RequiresApi


class FeedFragment : BaseFragment(), CardStackListener {
    private lateinit var binding:FragmentHomeSocialBinding
    private lateinit var cardStackView:CardStackView
    private lateinit var manager:CardStackLayoutManager
    private lateinit var adapter: DatingAdapter
    private lateinit var btnListChat:ImageView
    private lateinit var btnAddPost:ImageView
    private var lastPostOid:String? = null
    private lateinit var layoutRefresh:RelativeLayout
    private lateinit var btnRefresh:MaterialButton
    companion object {
        private var list = arrayListOf<PostModel>()
    }

    override fun onViewCreateBase(view: View, savedInstanceState: Bundle?) {
        btnListChat.setOnClickListener {
            showNextNoAddStack(ListChatFragment())
        }
        btnAddPost.setOnClickListener {
            val bottomSheet = UploadPostFragment(callApi,mySharePre)
            getFragmentManager()?.let { it1 -> bottomSheet.show(it1,bottomSheet.tag) }
        }
        layoutRefresh.visibility = View.INVISIBLE
        btnRefresh.setOnClickListener {
            (mActivity as MainActivity).progressBar(true)
            lastPostOid = null
            loadData()
        }
    }

    override fun onInitialized() {
        cardStackView = binding.cardStackView
        btnListChat = binding.listChat
        btnAddPost = binding.btnAddPost
        layoutRefresh = binding.layoutRefresh
        btnRefresh = binding.refresh
        init()
        loadData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized){
            binding = FragmentHomeSocialBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    private fun loadData(){
        val home = callApi.posts(token,lastPostOid)
        lastPostOid = null
        RequiresApi.callApi(mActivity.baseContext,home){
            (mActivity as MainActivity).progressBar(false)
            println("====>result${lastPostOid} === ${it}--${token}")
            if(it===null || it.status!=200){
                return@callApi
            }

            layoutRefresh.visibility = View.GONE

            val data = it.data as LinkedTreeMap<*, *>
            val list = data["list"] as ArrayList<*>
            list.forEach { item->
                item as LinkedTreeMap<*, *>
                val model = PostModel(
                    userOid = item["user_oid"].toString(),
                    item["full_name"].toString(),
                    item["image"].toString(),
                    item["content"].toString(),
                    item["post_oid"].toString(),
                    item["time"].toString(),
                    item["liked"].toString().toBoolean(),
                )
                lastPostOid = item["post_oid"].toString()
                adapter.setData(model)
            }
        }
    }

    private fun init(){
        adapter = DatingAdapter(requireContext(), list){
            val postOid = it[0]
            val action = it[1]
            if(action==="LIKE" || action==="DISLIKE"){
                reaction(postOid,action)
                return@DatingAdapter
            }
            if (action == "COMMENT") {
                val bottomSheet = CommentFragment(postOid, callApi, mySharePre)
                getFragmentManager()?.let { it1 -> bottomSheet.show(it1, bottomSheet.tag) }
                return@DatingAdapter
            }
            if (action == "CHAT") {
                redirectChat(postOid)
                return@DatingAdapter
            }
        }
        cardStackView.adapter = adapter

        manager = CardStackLayoutManager(requireContext(),this)
        cardStackView.layoutManager = manager
        manager.setVisibleCount(2)
        manager.setStackFrom(StackFrom.Top)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)

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

    private fun reaction(postOid:String,action:String){
        val reaction = callApi.reaction(token,id=postOid,action = action)
        lastPostOid = null
        RequiresApi.callApi(mActivity.baseContext,reaction){
            println("====>result=== ${it}--")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {

    }

    override fun onCardSwiped(direction: Direction?) {
        println("====>c:onCardSwiped-postition:${manager.topPosition}, total:${adapter.itemCount}")
        if (manager.topPosition == adapter.itemCount-3) {
            loadData()
        }
        if(manager.topPosition == adapter.itemCount){
            layoutRefresh.visibility = View.VISIBLE
        }

    }

    override fun onCardRewound() {

    }

    override fun onCardCanceled() {

    }

    override fun onCardAppeared(view: View?, position: Int) {

    }

    override fun onCardDisappeared(view: View?, position: Int) {

    }
}
package org.youpip.app.views.fragment.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.internal.LinkedTreeMap
import org.youpip.app.MainActivity
import org.youpip.app.R
import org.youpip.app.adapter.ItemSuggestAdapter
import org.youpip.app.adapter.ItemVideoMoreAdapter
import org.youpip.app.base.BaseFragment
import org.youpip.app.databinding.FragmentSuggestBinding
import org.youpip.app.databinding.FragmentVideoSearchBinding
import org.youpip.app.model.Video
import org.youpip.app.network.RequiresApi

class VideoSearchFragment(private val keyword:String) : BaseFragment() {
    private lateinit var binding:FragmentVideoSearchBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemVideoMoreAdapter
    override fun onViewCreateBase(view: View, savedInstanceState: Bundle?) {
        binding.tSearch.setText(keyword)
        binding.fSearch.setOnClickListener {
            showNextNoAddStack(SuggestFragment())
        }
    }

    override fun onInitialized() {
        recyclerView = binding.vSearch
        recyclerView.suppressLayout(true)
        customRecyclerView()
        fetchData()
    }

    private fun customRecyclerView(){
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = ItemVideoMoreAdapter{
            (mActivity as MainActivity).playVideo(it)
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::binding.isInitialized){
            binding = FragmentVideoSearchBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    private fun fetchData(){
        (mActivity as MainActivity).progressBar(true)
        if(keyword==""){
            return
        }
        val api = callApi.search(token,q=keyword)
        RequiresApi.callApi(mActivity.baseContext,api){
            (mActivity as MainActivity).progressBar(false)
            if(it==null || it.status !=200){
                return@callApi
            }
            val data = it.data as LinkedTreeMap<*, *>
            val list = data.get("list") as ArrayList<*>
            val dataItems = arrayListOf<Video>()
            list.forEach { item->
                item as LinkedTreeMap<*, *>
                dataItems.add(
                    Video(
                    item.get("video_id").toString(),
                    item.get("title").toString(),
                    item.get("thumbnail").toString(),
                    item.get("published_time").toString(),
                    item.get("view_count_text").toString(),
                    item.get("chanel_name").toString(),
                    item.get("chanel_url").toString(),
                    item.get("time_text").toString(),
                )
                )
            }
            adapter.setData(dataItems)
        }
    }
}
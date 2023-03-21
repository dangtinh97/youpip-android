package org.youpip.app.views.fragment.home

import android.content.Context.WINDOW_SERVICE
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.internal.LinkedTreeMap
import org.youpip.app.MainActivity
import org.youpip.app.adapter.ItemVideoHomeAdapter
import org.youpip.app.base.BaseFragment
import org.youpip.app.databinding.FragmentHomeBinding
import org.youpip.app.model.Video
import org.youpip.app.network.RequiresApi


class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemVideoHomeAdapter
    override fun onViewCreateBase(view: View, savedInstanceState: Bundle?) {
        loadData()
    }

    override fun onInitialized() {
        recyclerView = binding.listVideo
        recyclerView.suppressLayout(true)
        customRecyclerView()
    }

    fun customRecyclerView(){
        val displayMetrics = DisplayMetrics()
        val windowsManager = mActivity.baseContext.getSystemService(WINDOW_SERVICE) as WindowManager
        windowsManager.defaultDisplay.getMetrics(displayMetrics)
        val density = resources.displayMetrics.density
        println("====>dp${density}")
        val width = displayMetrics.widthPixels

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = ItemVideoHomeAdapter{
            if(it===null){
                return@ItemVideoHomeAdapter
            }
            (mActivity as MainActivity).playVideo(it)
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        adapter.setWidth(width-(8*density).toInt())
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized){
            binding = FragmentHomeBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    private fun loadData(){
        (mActivity as MainActivity).progressBar(true)
        val home = callApi.home(token)
        RequiresApi.callApi(mActivity.baseContext,home){
            (mActivity as MainActivity).progressBar(false)
            println("====>result${it}--${token}")
            if(it===null || !it.status.equals(200)){
                return@callApi
            }
            val data = it.data as LinkedTreeMap<*, *>
            val list = data.get("list") as ArrayList<*>
            val dataItems = arrayListOf<Video>()
            list.forEach { item->
                item as LinkedTreeMap<*, *>
                dataItems.add(Video(
                    item.get("video_id").toString(),
                    item.get("title").toString(),
                    item.get("thumbnail").toString(),
                    item.get("published_time").toString(),
                    item.get("view_count_text").toString(),
                    item.get("chanel_name").toString(),
                    item.get("chanel_url").toString(),
                    item.get("time_text").toString(),
                ))
            }
            adapter.setData(dataItems)
        }
    }

}
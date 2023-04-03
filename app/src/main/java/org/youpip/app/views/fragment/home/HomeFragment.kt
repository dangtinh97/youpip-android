package org.youpip.app.views.fragment.home

import android.content.Context.WINDOW_SERVICE
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.internal.LinkedTreeMap
import org.youpip.app.MainActivity
import org.youpip.app.R
import org.youpip.app.adapter.ItemVideoHomeAdapter
import org.youpip.app.base.BaseFragment
import org.youpip.app.databinding.FragmentHomeBinding
import org.youpip.app.model.Video
import org.youpip.app.network.RequiresApi


class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemVideoHomeAdapter
    private lateinit var recentlyView:TextView
    private lateinit var viewAll:TextView
    private lateinit var layoutManager:LinearLayoutManager
    private var isLoading:Boolean = false
    private var lastOid:String? = null
    private var typeView:String = "view_all"
    private var isLoadMore:Boolean = false
    override fun onViewCreateBase(view: View, savedInstanceState: Bundle?) {

    }

    override fun onInitialized() {
        recyclerView = binding.listVideo
        recentlyView = binding.recentlyView
        viewAll = binding.viewAll
        recyclerView.suppressLayout(true)
        customRecyclerView()
        loadData(typeView)
        onClickListener()
        initScrollScrollView()
    }

    private fun initScrollScrollView()
    {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
            {
                super.onScrolled(recyclerView, dx, dy)

                if (!isLoading && typeView=="recently_view")
                {
                    //findLastCompletelyVisibleItemPostition() returns position of last fully visible view.
                    ////It checks, fully visible view is the last one.
                    if (layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1)
                    {
                        isLoadMore = true
                        isLoading = true
                        loadData(typeView)
                    }
                }
            }
        })
    }

    private fun onClickListener()
    {
        recentlyView.setOnClickListener {
            isLoading = true
            recentlyView.setBackgroundResource(R.drawable.radius_30_white)
            recentlyView.setTextColor(ContextCompat.getColor((mActivity as MainActivity).baseContext, R.color.black))
            viewAll.setBackgroundResource(R.drawable.radius_30_black)
            viewAll.setTextColor(ContextCompat.getColor((mActivity as MainActivity).baseContext, R.color.white))
            typeView = "recently_view"
            lastOid = null
            loadData(typeView)
        }

        viewAll.setOnClickListener {
            isLoading = true
            lastOid = null
            recentlyView.setBackgroundResource(R.drawable.radius_30_black)
            recentlyView.setTextColor(ContextCompat.getColor((mActivity as MainActivity).baseContext, R.color.white))
            viewAll.setBackgroundResource(R.drawable.radius_30_white)
            viewAll.setTextColor(ContextCompat.getColor((mActivity as MainActivity).baseContext, R.color.black))
            typeView = "view_all"
            loadData(typeView)
        }
    }

    fun customRecyclerView(){
        val displayMetrics = DisplayMetrics()
        val windowsManager = mActivity.baseContext.getSystemService(WINDOW_SERVICE) as WindowManager
        windowsManager.defaultDisplay.getMetrics(displayMetrics)
        val density = resources.displayMetrics.density
        println("====>dp${density}")
        val width = displayMetrics.widthPixels

        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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

    private fun loadData(type:String){
        (mActivity as MainActivity).progressBar(true)
        val home = callApi.home(token,type,lastOid.toString())
        RequiresApi.callApi(mActivity.baseContext,home){
            (mActivity as MainActivity).progressBar(false)
            if(it===null || it.status != 200){
                return@callApi
            }
            val data = it.data as LinkedTreeMap<*, *>
            val list = data["list"] as ArrayList<*>
            val dataItems = arrayListOf<Video>()
            list.forEach { item->
                item as LinkedTreeMap<*, *>
                val model = Video(
                    item.get("video_id").toString(),
                    item.get("title").toString(),
                    item.get("thumbnail").toString(),
                    item.get("published_time").toString(),
                    item.get("view_count_text").toString(),
                    item.get("chanel_name").toString(),
                    item.get("chanel_url").toString(),
                    item.get("time_text").toString(),
                )
                lastOid = item["last_oid"].toString()
                if(!isLoadMore){
                    dataItems.add(model)
                }else{
                    adapter.appendData(model)
                }
            }
            if(!isLoadMore){
                adapter.setData(dataItems)
            }
            isLoadMore = false
            isLoading = false

        }
    }

}
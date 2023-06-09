package org.youpip.app.views.fragment.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.internal.LinkedTreeMap
import org.youpip.app.MainActivity
import org.youpip.app.adapter.ItemSuggestAdapter
import org.youpip.app.base.BaseFragment
import org.youpip.app.databinding.FragmentSuggestBinding
import org.youpip.app.network.RequiresApi
import java.util.*


class SuggestFragment : BaseFragment() {

    lateinit var binding:FragmentSuggestBinding
    lateinit var ipSearch:TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemSuggestAdapter
    private var selectText:Boolean = false
    override fun onViewCreateBase(view: View, savedInstanceState: Bundle?) {
        ipSearch = binding.ipSearch
        handlerListener()
    }

    private fun handlerListener(){
        ipSearch.setOnClickListener {
            search(ipSearch.text.toString())
            (mActivity as MainActivity).hideSoftKeyboard(mActivity)
        }
        ipSearch.addTextChangedListener(object : TextWatcher {
            private var timer: Timer = Timer()
            private val DELAY: Long = 1000 // Milliseconds

            override fun afterTextChanged(s: Editable?) {
                timer.cancel()
                timer = Timer()
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            search(s.toString())
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

        ipSearch.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event -> // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || (event.action == KeyEvent.ACTION_DOWN
                            && event.keyCode == KeyEvent.KEYCODE_ENTER)
                ) {
                    val content =ipSearch.text.toString().trim();
                    if(content!=""){
                        (mActivity as MainActivity).showKeyboard(false)
                        showNextNoAddStack(VideoSearchFragment(content))
                    }
                    return@OnEditorActionListener true
                }
                // Return true if you have consumed the action, else false.
                false
            })
    }

    override fun onInitialized() {
        recyclerView = binding.vSearch
        recyclerView.suppressLayout(true)
        customRecyclerView()
    }

    private fun customRecyclerView(){
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = ItemSuggestAdapter{
            ipSearch.setText(it)
            selectText = true
            (mActivity as MainActivity).hideSoftKeyboard(mActivity)
            showNextNoAddStack(VideoSearchFragment(it))
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::binding.isInitialized){
            binding = FragmentSuggestBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    private fun search(keyword:String){
        if(keyword == "" || selectText){
            selectText = false
            return
        }
        val api = callApi.suggest(token,keyword)
        RequiresApi.callApi(mActivity.baseContext,api){
            if(it===null || it.status != 200){
                return@callApi
            }
            val data = it.data as LinkedTreeMap<*, *>
            val list = data.get("list") as ArrayList<*>
            val listData = arrayListOf<String>()
            list.forEach { item->
                item as String
                listData.add(item)
            }
            adapter.setData(listData)
        }
    }
}
package org.youpip.app.views.fragment.social

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.youpip.app.MainActivity
import org.youpip.app.R
import org.youpip.app.adapter.ChatAdapter
import org.youpip.app.adapter.ItemVideoMoreAdapter
import org.youpip.app.base.BaseFragment
import org.youpip.app.databinding.FragmentHomeSocialBinding
import org.youpip.app.databinding.FragmentListChatBinding

class ListChatFragment : BaseFragment() {
    private lateinit var binding:FragmentListChatBinding
    private lateinit var adapter:ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnBack:ImageView
    override fun onViewCreateBase(view: View, savedInstanceState: Bundle?) {
        val list = arrayListOf<String>()
        list.add("1233")
        list.add("1233")
        list.add("1233")
        list.add("1233")
        list.add("1233")
        list.add("1233")
        list.add("1233")
        list.add("1233")
        list.add("1233")
        list.add("1233")
        adapter.setData(list)
    }

    override fun onInitialized() {
        recyclerView = binding.listChat
        btnBack = binding.back
        btnBack.setOnClickListener {
            showNextNoAddStack(FeedFragment())
            return@setOnClickListener
        }
        customRecyclerView()
    }

    private fun customRecyclerView(){
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = ChatAdapter{

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
}
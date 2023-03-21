package org.youpip.app.views.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.youpip.app.R
import org.youpip.app.base.BaseFragment
import org.youpip.app.databinding.FragmentBaseHomeBinding

class BaseHomeFragment : BaseFragment() {
    private lateinit var binding: FragmentBaseHomeBinding
    override fun onViewCreateBase(view: View, savedInstanceState: Bundle?) {
        println("====>onViewCreateBaseBaseHomeFragment")
    }

    override fun onInitialized() {
        showNextNoAddStack(HomeFragment())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized){
            binding = FragmentBaseHomeBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

}
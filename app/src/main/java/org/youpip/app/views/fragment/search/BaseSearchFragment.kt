package org.youpip.app.views.fragment.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.youpip.app.R
import org.youpip.app.base.BaseFragment
import org.youpip.app.databinding.FragmentBaseHomeBinding
import org.youpip.app.databinding.FragmentBaseSearchBinding

class BaseSearchFragment : BaseFragment() {

    lateinit var binding:FragmentBaseSearchBinding

    override fun onViewCreateBase(view: View, savedInstanceState: Bundle?) {

    }

    override fun onInitialized() {
        showNextNoAddStack(SuggestFragment())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized){
            binding = FragmentBaseSearchBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

}
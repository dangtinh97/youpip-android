package org.youpip.app.views.fragment.social

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import org.youpip.app.MainActivity
import org.youpip.app.R
import org.youpip.app.base.BaseFragment
import org.youpip.app.databinding.FragmentBaseHomeBinding
import org.youpip.app.databinding.FragmentBaseSocialBinding

class BaseSocialFragment : BaseFragment() {
    private lateinit var binding: FragmentBaseSocialBinding
    override fun onViewCreateBase(view: View, savedInstanceState: Bundle?) {
    }

    override fun onInitialized() {
        showNextNoAddStack(FeedFragment())
        (mActivity as MainActivity).showNavigationBottom(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::binding.isInitialized){
            binding = FragmentBaseSocialBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (mActivity as MainActivity).showNavigationBottom(false)
    }
}
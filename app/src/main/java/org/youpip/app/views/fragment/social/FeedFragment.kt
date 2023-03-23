package org.youpip.app.views.fragment.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.youpip.app.MainActivity
import org.youpip.app.R
import org.youpip.app.base.BaseFragment
import org.youpip.app.databinding.FragmentHomeSocialBinding

class FeedFragment : BaseFragment() {
    private lateinit var binding:FragmentHomeSocialBinding
    override fun onViewCreateBase(view: View, savedInstanceState: Bundle?) {
        binding.toolBar.setNavigationOnClickListener {
            (mActivity as MainActivity).showNavigationBottom(true)
            (mActivity as MainActivity).navigationTabBottom.selectedItemId = R.id.navigation_1

        }
    }

    override fun onInitialized() {
        val actionbar = (activity as AppCompatActivity?)!!
        actionbar.setSupportActionBar(binding.toolBar)
        actionbar.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        actionbar.supportActionBar?.setDisplayShowHomeEnabled(true)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Toast.makeText(mActivity.baseContext,"====>${item.itemId}",Toast.LENGTH_SHORT).show()
        when (item.itemId) {

        }
        return super.onOptionsItemSelected(item)
    }
}
package org.youpip.app.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.youpip.app.views.fragment.home.BaseHomeFragment
import org.youpip.app.views.fragment.search.BaseSearchFragment

private const val NUMBER_TAB = 4;
class ViewPagerTabAdapter(fragmentManager:FragmentManager, lifecycle: Lifecycle):
FragmentStateAdapter(fragmentManager,lifecycle){
    override fun getItemCount(): Int {
        return NUMBER_TAB;
    }

    override fun createFragment(position: Int): Fragment {
        println("====>createFragment")
        when(position){
            0 -> return BaseHomeFragment()
            1 -> return BaseSearchFragment()
        }
        return BaseHomeFragment()
    }

}
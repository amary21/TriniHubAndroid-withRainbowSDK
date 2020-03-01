package com.amary21.trinihub.activity.main.channel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ChannelTabAdapter(fragmentManager: FragmentManager)
    : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT ){

    private val mFragmentList : ArrayList<Fragment> = ArrayList()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment){
        mFragmentList.add(fragment)
    }
}
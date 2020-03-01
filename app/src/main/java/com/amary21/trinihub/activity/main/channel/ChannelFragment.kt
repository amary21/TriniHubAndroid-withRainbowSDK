package com.amary21.trinihub.activity.main.channel


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager

import com.amary21.trinihub.R
import com.amary21.trinihub.activity.main.channel.all.ChannelAllFragment
import com.amary21.trinihub.activity.main.channel.subscribe.ChannelSubFragment
import com.amary21.trinihub.activity.main.channel.owned.ChannelOwnedFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_channel.*

/**
 * A simple [Fragment] subclass.
 */
class ChannelFragment : Fragment() {

    companion object{
        fun newInstance() = ChannelFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_channel, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViewPager(vpChannel)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ChannelTabAdapter(childFragmentManager)

        val fragAll = ChannelAllFragment.newInstance()
        val fragSub = ChannelSubFragment.newInstance()
        val fragOwned = ChannelOwnedFragment.newInstance()

        adapter.addFragment(fragAll)
        adapter.addFragment(fragSub)
        adapter.addFragment(fragOwned)

        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabMenuChannel))
        tabMenuChannel.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                if (p0 != null){
                    viewPager.currentItem = p0.position
                }
            }

        })
    }


}

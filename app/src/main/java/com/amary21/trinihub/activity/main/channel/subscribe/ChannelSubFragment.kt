package com.amary21.trinihub.activity.main.channel.subscribe


import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ale.infra.list.IItemListChangeListener
import com.ale.infra.manager.channel.Channel
import com.ale.rainbowsdk.RainbowSdk

import com.amary21.trinihub.R
import com.amary21.trinihub.rainbow.RainbowConnection
import kotlinx.android.synthetic.main.fragment_channel_sub.*
import org.jetbrains.anko.runOnUiThread

/**
 * A simple [Fragment] subclass.
 */
class ChannelSubFragment : Fragment(), Channel.IChannelListener {

    private lateinit var channelSubAdapter: ChannelSubAdapter
    private var channel = mutableListOf<Channel>()
    private val channelListener = IItemListChangeListener{
        RainbowConnection.registerAllChannel(this)
    }

    companion object {
        fun newInstance() =
            ChannelSubFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_channel_sub, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spLoadingChannelSub.visibility = View.VISIBLE
        channelSubAdapter = ChannelSubAdapter(context as Activity)
        rvChannelSub.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = channelSubAdapter
        }

        RainbowConnection.registerChannelChangeListener(channelListener)
    }

    override fun onUpdate() {
        context?.runOnUiThread {
            channel = RainbowSdk.instance().channels().allSubscribedChannels
            if (channel.isNotEmpty()){
                channelSubAdapter.submitList(channel)
                spLoadingChannelSub.visibility = View.GONE
            }

        }
    }

    override fun onResume() {
        super.onResume()
        spLoadingChannelSub.visibility = View.VISIBLE
        RainbowConnection.registerChannelChangeListener(channelListener)
        context?.runOnUiThread {
            channel = RainbowSdk.instance().channels().allSubscribedChannels
            if (channel.isNotEmpty()){
                channelSubAdapter.submitList(channel)
                spLoadingChannelSub.visibility = View.GONE
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RainbowConnection.unregisterChannelChangeListener(channelListener)
        RainbowConnection.unregisterAllChannel(this)

    }


}

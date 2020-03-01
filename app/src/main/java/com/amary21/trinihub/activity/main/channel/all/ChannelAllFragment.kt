package com.amary21.trinihub.activity.main.channel.all


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ale.infra.application.RainbowContext
import com.ale.infra.list.IItemListChangeListener
import com.ale.infra.manager.channel.Channel
import com.amary21.trinihub.R
import com.amary21.trinihub.rainbow.RainbowConnection
import com.github.ybq.android.spinkit.SpinKitView
import kotlinx.android.synthetic.main.fragment_channel_all.*
import org.jetbrains.anko.runOnUiThread

/**
 * A simple [Fragment] subclass.
 */
class ChannelAllFragment : Fragment(), Channel.IChannelListener {

    private var spKitLoadingChannelAll: SpinKitView? = null
    private lateinit var channelAllAdapter: ChannelAllAdapter
    private var channel = mutableListOf<Channel>()
    private val channelListener = IItemListChangeListener {
        RainbowConnection.registerAllChannel(this)
    }

    companion object {
        fun newInstance() = ChannelAllFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_channel_all, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spKitLoadingChannelAll = view.findViewById(R.id.spLoadingChannelAll)
        spKitLoadingChannelAll?.visibility = View.VISIBLE
        channelAllAdapter = ChannelAllAdapter(context as Activity)
        rvChannelAll.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = channelAllAdapter
        }


        RainbowConnection.registerChannelChangeListener(channelListener)
    }

    override fun onResume() {
        super.onResume()

        spLoadingChannelAll.visibility = View.VISIBLE
        RainbowConnection.registerChannelChangeListener(channelListener)
        context?.runOnUiThread {
            channel = RainbowContext.getInfrastructure().channelMgr.tenMostRecentChannels
            if (channel.isNotEmpty()) {
                channelAllAdapter.submitList(channel)
                spKitLoadingChannelAll?.visibility = View.INVISIBLE
            }

        }
    }

    override fun onUpdate() {
        context?.runOnUiThread {
            channel = RainbowContext.getInfrastructure().channelMgr.tenMostRecentChannels
            if (channel.isNotEmpty()) {
                channelAllAdapter.submitList(channel)
                spKitLoadingChannelAll?.visibility = View.INVISIBLE
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RainbowConnection.unregisterChannelChangeListener(channelListener)
        RainbowConnection.unregisterAllChannel(this)
    }

}

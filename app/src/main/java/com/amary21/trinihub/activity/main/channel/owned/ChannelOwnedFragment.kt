package com.amary21.trinihub.activity.main.channel.owned


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ale.infra.list.IItemListChangeListener
import com.ale.infra.manager.channel.Channel
import com.ale.rainbowsdk.RainbowSdk
import com.amary21.trinihub.R
import com.amary21.trinihub.activity.channel.CreateChannelActivity
import com.amary21.trinihub.rainbow.RainbowConnection
import kotlinx.android.synthetic.main.fragment_channel_owned.*
import org.jetbrains.anko.runOnUiThread

/**
 * A simple [Fragment] subclass.
 */
class ChannelOwnedFragment : Fragment(), Channel.IChannelListener {

    private lateinit var channelOwnedAdapter: ChannelOwnedAdapter
    private var channel = mutableListOf<Channel>()
    private val channelListener = IItemListChangeListener{
        RainbowConnection.registerAllChannel(this)
    }

    companion object {
        fun newInstance() =
            ChannelOwnedFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_channel_owned, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spLoadingChannelOwned.visibility = View.VISIBLE
        channelOwnedAdapter = ChannelOwnedAdapter(context as Activity)
        rvChannelOwn.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = channelOwnedAdapter
        }

        RainbowConnection.registerChannelChangeListener(channelListener)

        btnAddChannel.setOnClickListener {
            startActivity(Intent(context, CreateChannelActivity::class.java))
        }

    }

    override fun onUpdate() {
        context?.runOnUiThread {
            channel = RainbowSdk.instance().channels().allOwnedChannels
            if (channel.isNotEmpty()){
                channelOwnedAdapter.submitList(channel)
                spLoadingChannelOwned.visibility = View.GONE
            }

        }
    }

    override fun onResume() {
        super.onResume()
        spLoadingChannelOwned.visibility = View.VISIBLE
        RainbowConnection.registerChannelChangeListener(channelListener)
        context?.runOnUiThread {
            channel = RainbowSdk.instance().channels().allOwnedChannels
            if (channel.isNotEmpty()){
                channelOwnedAdapter.submitList(channel)
                spLoadingChannelOwned.visibility = View.GONE
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RainbowConnection.unregisterChannelChangeListener(channelListener)
        RainbowConnection.unregisterAllChannel(this)
    }


}

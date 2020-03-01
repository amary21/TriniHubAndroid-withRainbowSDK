package com.amary21.trinihub.activity.main.home


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ale.infra.application.RainbowContext
import com.ale.infra.list.IItemListChangeListener
import com.ale.infra.manager.channel.Channel
import com.amary21.trinihub.R
import com.amary21.trinihub.activity.main.MainActivity
import com.amary21.trinihub.activity.main.channel.all.ChannelAllAdapter
import com.amary21.trinihub.activity.main.meet.MeetAdapter
import com.amary21.trinihub.data.local.FanArt
import com.amary21.trinihub.data.network.ApiClient
import com.amary21.trinihub.data.network.model.MeetHub
import com.amary21.trinihub.data.network.model.MeetHubResponse
import com.amary21.trinihub.rainbow.RainbowConnection
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */

class HomeFragment : Fragment(), Channel.IChannelListener {

    private lateinit var activity: MainActivity

    private val fanArt = mutableListOf<FanArt>()

    private var homeAdapter: HomeAdapter? = null
    private lateinit var adapterMeet: MeetAdapter
    private var meetItem = mutableListOf<MeetHub>()


    private lateinit var adapterChannel: ChannelAllAdapter
    private var channelHome = mutableListOf<Channel>()
    private val channelListener = IItemListChangeListener {
        RainbowConnection.registerAllChannel(this)
    }

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity() as MainActivity
        adapterChannel = ChannelAllAdapter(activity)
        adapterMeet = MeetAdapter(activity, meetItem)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initFanArt()
        initMeeting()
        initChannel()
    }

    private fun initChannel() {
        spLoadingChannelHome.visibility = View.VISIBLE
        rvHomeChannel.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = adapterChannel
        }

        RainbowConnection.registerChannelChangeListener(channelListener)

    }

    override fun onResume() {
        super.onResume()
        RainbowConnection.registerChannelChangeListener(channelListener)
        getDataChannel()
    }

    private fun getDataChannel() {
        activity.runOnUiThread {
            channelHome = RainbowContext.getInfrastructure().channelMgr.tenBestChannels
            if (channelHome.isNotEmpty()) {
                spLoadingChannelHome.visibility = View.GONE
                adapterChannel.submitList(channelHome)
                rvHomeChannel.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun initMeeting() {
        spLoadingMeetHome.visibility = View.VISIBLE
        rvHomeMeeting.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterMeet
        }

        getDataMeeting()

    }

    private fun getDataMeeting() {
        meetItem.clear()
        ApiClient.getClientHub().getDataMeetTomorowHub()
            .enqueue(object : Callback<MeetHubResponse> {
                override fun onFailure(call: Call<MeetHubResponse>, t: Throwable) {
                    Log.e("DATA MEET HOME", t.message.toString())
                }

                override fun onResponse(
                    call: Call<MeetHubResponse>,
                    response: Response<MeetHubResponse>
                ) {
                    if (response.isSuccessful) {
                        spLoadingMeetHome.visibility = View.GONE
                        response.body()?.result?.let { meetItem.addAll(it) }
                        rvHomeMeeting.adapter?.notifyDataSetChanged()
                    }
                }

            })
    }

    private fun initFanArt() {
        homeAdapter = context?.let { HomeAdapter(it) }
        vpFanArt.adapter = homeAdapter
        dotsIndicator.setViewPager(vpFanArt)
        vpFanArt.adapter?.registerDataSetObserver(dotsIndicator.dataSetObserver)

        fanArt.add(FanArt(context?.getDrawable(R.drawable.slide_1)))
        fanArt.add(FanArt(context?.getDrawable(R.drawable.slide_2)))
        fanArt.add(FanArt(context?.getDrawable(R.drawable.slide_3)))
        fanArt.add(FanArt(context?.getDrawable(R.drawable.slide_4)))
        fanArt.add(FanArt(context?.getDrawable(R.drawable.slide_5)))

            if (fanArt.isNotEmpty()) {
                homeAdapter?.setImage(fanArt)
            }
    }

    override fun onUpdate() {
        getDataChannel()
    }

}

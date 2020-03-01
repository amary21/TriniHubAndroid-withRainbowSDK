package com.amary21.trinihub.activity.main.meet


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.amary21.trinihub.R
import com.amary21.trinihub.activity.main.MainActivity
import com.amary21.trinihub.activity.meeting.CreateMeetActivity
import com.amary21.trinihub.data.network.ApiClient
import com.amary21.trinihub.data.network.model.MeetHub
import com.amary21.trinihub.data.network.model.MeetHubResponse
import kotlinx.android.synthetic.main.fragment_meet.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class MeetFragment : Fragment() {

    private lateinit var activity: MainActivity
    private lateinit var meetAdapter: MeetAdapter
    private var meetItem = mutableListOf<MeetHub>()

    companion object {
        fun newInstance() = MeetFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity() as MainActivity
        meetAdapter = MeetAdapter(activity, meetItem)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rvMeetList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = meetAdapter
        }

        getDataMeetList()

        swpMeet.setOnRefreshListener {
            getDataMeetList()
        }

        btnCreateMeet.setOnClickListener {
            startActivity(Intent(context, CreateMeetActivity::class.java))
        }
    }

    private fun getDataMeetList() {
        spLoadingMeet.visibility = View.VISIBLE
        meetItem.clear()
        ApiClient.getClientHub().getDataMeetHub()
            .enqueue(object : Callback<MeetHubResponse>{
                override fun onFailure(call: Call<MeetHubResponse>, t: Throwable) {
                    Log.e("DATA MEET", t.message.toString())
                }

                override fun onResponse(
                    call: Call<MeetHubResponse>,
                    response: Response<MeetHubResponse>
                ) {
                    if (response.isSuccessful){
                        val result = response.body()?.result
                        if (result != null){
                            meetItem.addAll(result)
                            activity.runOnUiThread {
                                rvMeetList.adapter?.notifyDataSetChanged()
                                swpMeet.isRefreshing = false
                                spLoadingMeet.visibility = View.GONE
                            }
                        }
                    }
                }


            })
    }

}

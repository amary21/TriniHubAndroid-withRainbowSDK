package com.amary21.trinihub.activity.meeting.guestlist

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.amary21.trinihub.R
import com.amary21.trinihub.data.network.ApiClient
import com.amary21.trinihub.data.network.QR_CODE_API
import com.amary21.trinihub.data.network.model.UserHub
import com.amary21.trinihub.data.network.model.UserHubResponse
import com.amary21.trinihub.utils.DownloadImageTask
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_guestlist_meet.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GuestlistMeetActivity : AppCompatActivity() {
    private lateinit var btmCreateDialog: BottomSheetDialog
    private lateinit var btmCreateDialogView: View
    private lateinit var guestAdapter: GuesListAdapter
    private var userItem = mutableListOf<UserHub>()

    companion object {
        const val GUEST_LIST_CODE =
            "com.amary21.trinihub.activity.meeting.guestlist.GUEST_LIST_CODE"
        const val GUEST_LIST_MEET =
            "com.amary21.trinihub.activity.meeting.guestlist.GUEST_LIST_MEET"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guestlist_meet)

        guestAdapter = GuesListAdapter(this, userItem)
        rvGuestList.apply {
            layoutManager = LinearLayoutManager(this@GuestlistMeetActivity)
            setHasFixedSize(true)
            adapter = guestAdapter
        }

        val idMeet = intent.getStringExtra(GUEST_LIST_MEET)
        if (idMeet != null)
            getGuestData(idMeet)

        btmCreateDialog = BottomSheetDialog(this)
        val codeInvite = intent.getStringExtra(GUEST_LIST_CODE)
        btmCreateDialogView = layoutInflater.inflate(R.layout.bottom_barcode, null)
        btmCreateDialog.setContentView(btmCreateDialogView)
        val imgQrCode = btmCreateDialogView.findViewById<ImageView>(R.id.imgQrCodeInvite)
        DownloadImageTask(imgQrCode).execute(QR_CODE_API + codeInvite)

        btnInvitationMeeting.setOnClickListener {
            openQrCode()
        }

        btnBackGuestlist.setOnClickListener {
            onBackPressed()
            finish()
        }

        swpRefreshGuestlist.setOnRefreshListener {
            if (idMeet != null)
                getGuestData(idMeet)
        }
    }

    private fun getGuestData(idMeet: String) {
        spLoadingGuestlist.visibility = View.VISIBLE
        userItem.clear()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("idMeet", idMeet)
            .build()

        ApiClient.getClientHub().getGuestList(requestBody)
            .enqueue(object : Callback<UserHubResponse> {
                override fun onFailure(call: Call<UserHubResponse>, t: Throwable) {
                    Toast.makeText(
                        this@GuestlistMeetActivity,
                        t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    swpRefreshGuestlist.isRefreshing = false
                    spLoadingGuestlist.visibility = View.GONE
                }

                override fun onResponse(
                    call: Call<UserHubResponse>,
                    response: Response<UserHubResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()?.result
                        if (result != null) {
                            userItem.addAll(result)
                            rvGuestList.adapter?.notifyDataSetChanged()
                            swpRefreshGuestlist.isRefreshing = false
                            spLoadingGuestlist.visibility = View.GONE

                        }
                    }
                }

            })
    }

    private fun openQrCode() {
        btmCreateDialog.show()
    }
}

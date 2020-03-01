package com.amary21.trinihub.activity.meeting

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.amary21.trinihub.R
import com.amary21.trinihub.activity.meeting.ScaneMeetActivity.Companion.SCANE_INVITE_CODE
import com.amary21.trinihub.data.local.Account
import com.amary21.trinihub.data.local.GuestList
import com.amary21.trinihub.data.local.database
import com.amary21.trinihub.data.network.model.MeetHub
import com.amary21.trinihub.utils.DateConvert
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_detail_meet.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select

class DetailMeetActivity : AppCompatActivity() {
    companion object {
        const val DATA_MEET = "com.amary21.trinihub.activity.meeting.DATA_MEET"
    }

    private var dataMeet: MeetHub? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_meet)

        dataMeet = intent.getParcelableExtra(DATA_MEET)
        if (dataMeet != null) {
            tvTitleDetailMeet.text = dataMeet!!.name_meet
            tvDesDetailMeet.text = dataMeet!!.des_meet
            tvCategoryDetailMeet.text = dataMeet!!.category
            tvDateDetailMeet.text = DateConvert.convertDateMeet(dataMeet!!.date)
            tvTimeDetailMeet.text = "${dataMeet!!.time_start}  - ${dataMeet!!.time_end}"
            tvPlaceDetailMeet.text = dataMeet!!.name_place
            tvAdressDetailMeet.text = dataMeet!!.address
            Glide.with(this)
                .load(dataMeet!!.photo)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.no_image)
                        .error(R.drawable.no_image)
                )
                .into(imgPhotoDetailMeet)

            getReadyMeeting()
        }

        btnBackDetailMeet.setOnClickListener {
            onBackPressed()
            finish()
        }

        btnGetMeet.setOnClickListener {
            val intent = Intent(this, ScaneMeetActivity::class.java)
            intent.putExtra(SCANE_INVITE_CODE, dataMeet)
            startActivity(intent)
        }
    }

    private fun getReadyMeeting() {
        val idUser = getUserId()
        val userIdHaveMeet = getHaveMeetId(idUser, dataMeet!!.id_meet)

        Log.e("DATA THIS MEET", userIdHaveMeet.toString())

        if (userIdHaveMeet) {
            btnGetMeet.visibility = View.GONE
            btnYouHaveMeet.visibility = View.VISIBLE
            btnYouHaveMeet.isEnabled = false
        } else {
            btnGetMeet.visibility = View.VISIBLE
            btnYouHaveMeet.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        getReadyMeeting()
    }

    private fun getUserId(): String {
        var idUser: String? = null
        try {
            database.use {
                val result = select(Account.TABLE_ACCOUNT)
                val account = result.parseList(classParser<Account>())
                idUser = account[0].idUser.toString()
            }
        } catch (e: SQLiteConstraintException) {
            idUser = null
        }
        return idUser.toString()
    }

    private fun getHaveMeetId(idUserr: String, idMeet: String): Boolean {
        var haveReady = false
        try {
            database.use {
                val result =
                    select(GuestList.TABLE_ACCOUNT).whereArgs(
                        "(ID_MEET = {idMeet}) and (ID_USER = {idUser})",
                        "idMeet" to idMeet,
                        "idUser" to idUserr
                    )
                val account = result.parseList(classParser<GuestList>())
                Log.e("DATA MEET", account.toString())

                haveReady = account.isNotEmpty()
            }
        } catch (e: SQLiteConstraintException) {
            haveReady = false
        }

        return haveReady
    }

}

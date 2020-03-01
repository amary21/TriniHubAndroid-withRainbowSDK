package com.amary21.trinihub.activity.main.meet

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amary21.trinihub.R
import com.amary21.trinihub.activity.meeting.DetailMeetActivity
import com.amary21.trinihub.activity.meeting.DetailMeetActivity.Companion.DATA_MEET
import com.amary21.trinihub.activity.meeting.guestlist.GuestlistMeetActivity
import com.amary21.trinihub.activity.meeting.guestlist.GuestlistMeetActivity.Companion.GUEST_LIST_CODE
import com.amary21.trinihub.activity.meeting.guestlist.GuestlistMeetActivity.Companion.GUEST_LIST_MEET
import com.amary21.trinihub.data.local.Account
import com.amary21.trinihub.data.local.database
import com.amary21.trinihub.data.network.model.MeetHub
import com.amary21.trinihub.utils.DateConvert
import com.amary21.trinihub.utils.DownloadImageTask
import kotlinx.android.synthetic.main.item_meet.view.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select

class MeetAdapter(val context: Context, private val meetHub: List<MeetHub>) :
    RecyclerView.Adapter<MeetAdapter.MeetViewHolder>() {

    private fun getUserId(): String {

        var idUser: String? = null
        try {
            context.database.use {
                val result = select(Account.TABLE_ACCOUNT)
                val account = result.parseList(classParser<Account>())
                idUser = account[0].idUser.toString()
            }
        } catch (e: SQLiteConstraintException) {
            idUser = null
        }

        return idUser.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetViewHolder {
        return MeetViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_meet,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return meetHub.size
    }

    override fun onBindViewHolder(holder: MeetViewHolder, position: Int) {
        holder.bind(meetHub[position])
    }


    inner class MeetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(meetHub: MeetHub) {
            itemView.tvTitleMeetList.text = meetHub.name_meet
            itemView.tvDateMeetList.text = DateConvert.convertDateMeet(meetHub.date)
            itemView.tvPlaceNameMeetList.text = meetHub.name_place

            if (meetHub.photo != null) {
                DownloadImageTask(itemView.imgPhotoMeetList).execute(meetHub.photo)
            }

            itemView.setOnClickListener {
                if (meetHub.id_user == getUserId()) {
                    val intent = Intent(context, GuestlistMeetActivity::class.java)
                    intent.putExtra(GUEST_LIST_CODE, meetHub.invitation)
                    intent.putExtra(GUEST_LIST_MEET, meetHub.id_meet)
                    context.startActivity(intent)
                } else {
                    val intent = Intent(context, DetailMeetActivity::class.java)
                    intent.putExtra(DATA_MEET, meetHub)
                    context.startActivity(intent)
                }
            }
        }

    }
}
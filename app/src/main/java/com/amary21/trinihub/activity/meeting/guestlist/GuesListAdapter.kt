package com.amary21.trinihub.activity.meeting.guestlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amary21.trinihub.R
import com.amary21.trinihub.data.network.model.UserHub
import kotlinx.android.synthetic.main.item_guestlist.view.*

class GuesListAdapter(val context: Context, private val user: List<UserHub>) :
    RecyclerView.Adapter<GuesListAdapter.GuestListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuestListViewHolder {
        return GuestListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_guestlist, parent, false))
    }

    override fun getItemCount(): Int {
        return user.size
    }

    override fun onBindViewHolder(holder: GuestListViewHolder, position: Int) {
        holder.bind(user[position], position)
    }


    inner class GuestListViewHolder(view: View) : RecyclerView.ViewHolder(view){
        fun bind(userHub: UserHub, position: Int) {
            val no = position + 1
            itemView.tvNoGuest.text = no.toString()
            itemView.tvNameGuest.text = userHub.name_user
            itemView.tvEmailGuest.text = userHub.email_user
            itemView.tvPhoneGuest.text = userHub.phone_user
            itemView.tvJobGuest.text = userHub.job_title
        }

    }
}
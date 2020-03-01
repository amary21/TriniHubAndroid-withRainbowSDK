package com.amary21.trinihub.activity.contact

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.contact.IRainbowContact
import com.ale.infra.contact.RainbowPresence
import com.amary21.trinihub.R
import kotlinx.android.synthetic.main.item_contact.view.*

class ContactAdapter(var items : List<IRainbowContact>,
                     val context: Context,
                     val clickRosterListener: ((IRainbowContact, Boolean) -> Unit)? = null,
                     val clickListener: ((IRainbowContact)-> Unit)? = null) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        if (position < items.size)
            holder.bind(items[position])
    }

    inner class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n", "RestrictedApi")
        fun bind(contact: IRainbowContact) {

            val inisialFirstName = if (contact.firstName != null || contact.firstName.isNotEmpty()){
                contact.firstName[0].toUpperCase().toString()
            }else{
                ""
            }

            val inisialLastName = if (contact.lastName != null || contact.lastName.isNotEmpty()){
                contact.lastName[0].toUpperCase().toString()
            }else{
                ""
            }

            if (contact.photo != null){
                itemView.imgProfileContactTrue.visibility = View.VISIBLE
                itemView.rlProfileContact.visibility = View.INVISIBLE
                itemView.imgProfileContactTrue.setImageBitmap(contact.photo)
            }else{
                itemView.imgProfileContactTrue.visibility = View.INVISIBLE
                itemView.rlProfileContact.visibility = View.VISIBLE
                itemView.tvNameProfileContact.text = inisialFirstName + inisialLastName
            }

            val color = when (contact.presence) {
                RainbowPresence.AWAY, RainbowPresence.MANUAL_AWAY -> R.drawable.shape_st_yellow
                RainbowPresence.MOBILE_ONLINE -> R.drawable.shape_st_blue
                RainbowPresence.ONLINE -> R.drawable.shape_st_gren
                RainbowPresence.XA, RainbowPresence.OFFLINE, RainbowPresence.UNSUBSCRIBED -> R.drawable.shape_st_gray
                else -> R.drawable.shape_st_red
            }

            val status = when(contact.presence){
                RainbowPresence.AWAY, RainbowPresence.MANUAL_AWAY -> "Away"
                RainbowPresence.MOBILE_ONLINE -> "Mobile"
                RainbowPresence.ONLINE -> "Online"
                RainbowPresence.XA, RainbowPresence.OFFLINE, RainbowPresence.UNSUBSCRIBED -> "Offline"
                else -> "Offline"
            }

            itemView.tvNameContact.text = contact.getDisplayName("Unknown")
            itemView.ivPresenceStatusContact.setBackgroundResource(color)
            itemView.tvContactStatus.text = status
            itemView.setOnClickListener {
                clickListener?.invoke(contact)
            }
            itemView.imgAddContact.setOnClickListener {
                clickRosterListener?.invoke(contact, true)
            }
            itemView.imgRemoveContact.setOnClickListener {
                clickRosterListener?.invoke(contact, false)
            }

            if (clickRosterListener != null) {
                if (contact.presence == RainbowPresence.UNSUBSCRIBED) {
                    itemView.imgAddContact.visibility = View.VISIBLE
                    itemView.imgRemoveContact.visibility = View.GONE

                } else {
                    itemView.imgRemoveContact.visibility = View.VISIBLE
                    itemView.imgAddContact.visibility = View.GONE
                }
            }

        }

    }
}
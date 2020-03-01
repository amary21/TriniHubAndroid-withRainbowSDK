package com.amary21.trinihub.activity.main.chat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.contact.RainbowPresence
import com.ale.infra.manager.IMMessage
import com.ale.infra.proxy.conversation.IRainbowConversation
import com.amary21.trinihub.utils.DateConvert
import com.amary21.trinihub.R
import com.amary21.trinihub.activity.chat_room.ChatroomActivity
import kotlinx.android.synthetic.main.item_chat.view.*
import java.io.ByteArrayOutputStream
import java.util.*

class ChatAdapter(val context: Context, val item : List<IRainbowConversation>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false))
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(item[position])
    }

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var name: String = ""
        private var inisialFirstName = ""
        private var inisialLastName = ""
        @SuppressLint("SetTextI18n")
        fun bind(item: IRainbowConversation) {

            name = if (item.isRoomType) {
                item.room.name
            } else {
                item.contact.getDisplayName("Unknown")
            }

            inisialFirstName = if (!item.contact.firstName.isNullOrEmpty()){
                item.contact.firstName[0].toUpperCase().toString()
            }else{
                ""
            }

            inisialLastName = if (item.contact.lastName != null || item.contact.lastName.isNotEmpty()){
                item.contact.lastName[0].toUpperCase().toString()
            }else{
                ""
            }

            itemView.tvNameChat.text = name
            itemView.tvChatRoom.text = item.lastMessage.messageContent

            val timeStamp = item.lastMessage.timeStamp
            val date = Date(timeStamp)

            itemView.tvTimeChat.text = DateConvert.prettyFormat(date)

            if (item.contact.photo != null){
                itemView.imgProfileChatTrue.visibility = View.VISIBLE
                itemView.rlProfileChat.visibility = View.INVISIBLE
                itemView.imgProfileChatTrue.setImageBitmap(item.contact.photo)
            }else{
                itemView.imgProfileChatTrue.visibility = View.INVISIBLE
                itemView.rlProfileChat.visibility = View.VISIBLE
                itemView.tvNameProfileChat.text = inisialFirstName + inisialLastName
            }

            when(item.contact.presence){
                RainbowPresence.ONLINE ->{
                    itemView.ivPresenceStatus.setImageDrawable(ColorDrawable(Color.GREEN))
                }
                RainbowPresence.OFFLINE ->{
                    itemView.ivPresenceStatus.setImageDrawable(ColorDrawable(Color.GRAY))
                }
                RainbowPresence.MOBILE_ONLINE ->{
                    itemView.ivPresenceStatus.setImageDrawable(ColorDrawable(Color.BLUE))
                }
                RainbowPresence.AWAY ->{
                    itemView.ivPresenceStatus.setImageDrawable(ColorDrawable(Color.YELLOW))
                }
                RainbowPresence.BUSY ->{
                    itemView.ivPresenceStatus.setImageDrawable(ColorDrawable(Color.CYAN))
                }
                RainbowPresence.DND ->{
                    itemView.ivPresenceStatus.setImageDrawable(ColorDrawable(Color.RED))
                }
                RainbowPresence.DND_PRESENTATION ->{
                    itemView.ivPresenceStatus.setImageDrawable(ColorDrawable(Color.RED))
                }
                else ->{
                    itemView.ivPresenceStatus.setImageDrawable(ColorDrawable(Color.GRAY))
                }
            }

            when (item.lastMessage.deliveryState) {
                IMMessage.DeliveryState.RECEIVED -> {
                    itemView.imgCekStatusMessage.setImageResource(R.drawable.shape_send_chat)
                    itemView.tvChatRoom.setTypeface(itemView.tvChatRoom.typeface, Typeface.BOLD)
                }
                IMMessage.DeliveryState.READ -> {
                    itemView.imgCekStatusMessage.setImageResource(R.drawable.shape_read_chat)
                    itemView.tvChatRoom.setTypeface(itemView.tvChatRoom.typeface, Typeface.NORMAL)
                }
                IMMessage.DeliveryState.SENT_CLIENT_READ -> {
                    itemView.imgCekStatusMessage.setImageResource(R.drawable.ic_message_read)
                    itemView.tvChatRoom.setTypeface(itemView.tvChatRoom.typeface, Typeface.NORMAL)
                }
                IMMessage.DeliveryState.SENT_CLIENT_RECEIVED -> {
                    itemView.imgCekStatusMessage.setImageResource(R.drawable.ic_message_received)
                    itemView.tvChatRoom.setTypeface(itemView.tvChatRoom.typeface, Typeface.NORMAL)
                }
                IMMessage.DeliveryState.SENT_SERVER_RECEIVED -> {
                    itemView.imgCekStatusMessage.setImageResource(R.drawable.ic_message_received)
                    itemView.tvChatRoom.setTypeface(itemView.tvChatRoom.typeface, Typeface.NORMAL)
                }
                IMMessage.DeliveryState.UNKNOWN -> {
                    itemView.imgCekStatusMessage.setImageResource(R.drawable.ic_autorenew)
                    itemView.tvChatRoom.setTypeface(itemView.tvChatRoom.typeface, Typeface.NORMAL)
                }
                else -> {
                    itemView.imgCekStatusMessage.setImageResource(R.drawable.ic_autorenew)
                    itemView.tvChatRoom.setTypeface(itemView.tvChatRoom.typeface, Typeface.NORMAL)
                }
            }

            Log.e("STATUS PESAN", item.lastMessage.deliveryState.toString())

            itemView.setOnClickListener {
                val intent = Intent(context, ChatroomActivity::class.java)

                //JID Contact
                intent.putExtra("JID", item.contact.jid)

                //Person Data
                intent.putExtra("displayName", name)
                intent.putExtra("firstName", item.contact.firstName)
                intent.putExtra("lastName", item.contact.lastName)
                intent.putExtra("nickName", item.contact.nickName)

                //Email Data
                intent.putExtra("firstEmail", item.contact.firstEmailAddress)
                intent.putExtra("mainEmail", item.contact.mainEmailAddress)

                //Phone Number Data
                if (item.contact.firstOfficePhoneNumber != null){
                    Log.e("DATA PHONE", item.contact.firstOfficePhoneNumber.toString())
                    intent.putExtra("phoneNumber", item.contact.firstOfficePhoneNumber.phoneNumberValue)
                }else{
                    intent.putExtra("phoneNumber", "")
                }

                //Photo Data
                if (item.contact.photo != null){
                    val bmpStream = ByteArrayOutputStream()
                    item.contact.photo.compress(Bitmap.CompressFormat.PNG, 50, bmpStream)
                    val byteArray = bmpStream.toByteArray()
                    intent.putExtra("photoProfile", byteArray)
                    intent.putExtra("isPhoto", true)
                }else{
                    intent.putExtra("isPhoto", false)
                    intent.putExtra("nameProfile", inisialFirstName + inisialLastName)
                }

                //Job Data
                intent.putExtra("jobTitle", item.contact.jobTitle)
                intent.putExtra("company", item.contact.companyName)

                context.startActivity(intent)
            }
        }

    }

}

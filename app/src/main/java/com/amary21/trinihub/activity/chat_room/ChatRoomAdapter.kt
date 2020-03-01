package com.amary21.trinihub.activity.chat_room

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.contact.IRainbowContact
import com.ale.infra.manager.IMMessage
import com.ale.rainbowsdk.RainbowSdk
import com.amary21.trinihub.utils.DateConvert
import com.amary21.trinihub.R
import kotlinx.android.synthetic.main.item_chat_receive.view.*
import kotlinx.android.synthetic.main.item_chat_send.view.*

class ChatRoomAdapter(private val message: MutableList<IMMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val contactCache = mutableMapOf<String, IRainbowContact>()

    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }

//    fun submilist(updateMessage : MutableList<IMMessage>){
//        message.clear()
//        message.addAll(updateMessage)
//        notifyDataSetChanged()
//    }

    override fun getItemViewType(position: Int) =
        if (message[position].isMsgSent) VIEW_TYPE_MESSAGE_SENT else VIEW_TYPE_MESSAGE_RECEIVED

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_MESSAGE_SENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_send, parent, false)
                ChatSenderView(view)
            }
            VIEW_TYPE_MESSAGE_RECEIVED -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_receive, parent, false)
                ChatReceivedView(view)
            }
            else -> throw IllegalArgumentException("Unknown View Type")
        }
    }

    override fun getItemCount() = message.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < message.size) {
            if (!contactCache.containsKey(message[position].contactJid))
                contactCache[message[position].contactJid] = RainbowSdk.instance().contacts()
                    .getContactFromJabberId(message[position].contactJid)

            when (holder) {
                is ChatSenderView -> {
                    holder.bind(message[position])
                }
                is ChatReceivedView -> {
                    holder.bind(message[position])
                }

                else -> throw IllegalArgumentException("Unknown View Type")
            }
        }
    }


    class ChatReceivedView(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(imMessage: IMMessage?) {
            if (imMessage != null) {
                itemView.tv_received_message.text = imMessage.messageContent
                itemView.tv_received_time.text = DateConvert.prettyFormat(imMessage.messageDate)
            }
        }

    }

    class ChatSenderView(view: View) : RecyclerView.ViewHolder(view) {
        var i: Int = 0
        fun bind(imMessage: IMMessage?) {

            if (imMessage != null) {
                i++
                itemView.tv_sender_message.text = imMessage.messageContent
                itemView.tv_sender_time.text = DateConvert.prettyFormat(imMessage.messageDate)

                when (imMessage.deliveryState) {
                    IMMessage.DeliveryState.RECEIVED -> {
                        itemView.imgCekStatusChatRoom.setImageResource(R.drawable.ic_message_sent)
                    }
                    IMMessage.DeliveryState.READ -> {
                        itemView.imgCekStatusChatRoom.setImageResource(R.drawable.shape_read_chat)
                    }
                    IMMessage.DeliveryState.SENT_CLIENT_READ -> {
                        itemView.imgCekStatusChatRoom.setImageResource(R.drawable.ic_message_received)
                    }
                    IMMessage.DeliveryState.SENT_CLIENT_RECEIVED -> {
                        itemView.imgCekStatusChatRoom.setImageResource(R.drawable.ic_message_received)
                    }
                    IMMessage.DeliveryState.SENT_SERVER_RECEIVED -> {
                        itemView.imgCekStatusChatRoom.setImageResource(R.drawable.ic_message_received)
                    }
                    IMMessage.DeliveryState.UNKNOWN -> {
                        itemView.imgCekStatusChatRoom.setImageResource(R.drawable.ic_autorenew)
                    }
                    else -> {
                        itemView.imgCekStatusChatRoom.setImageResource(R.drawable.ic_autorenew)
                    }
                }
            }
        }

    }
}
package com.amary21.trinihub.activity.main.channel.subscribe

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.manager.channel.Channel
import com.amary21.trinihub.R
import com.amary21.trinihub.activity.channel.detail.DetailChannelActivity
import com.amary21.trinihub.activity.channel.detail.DetailChannelActivity.Companion.ID_CHANNEL
import com.amary21.trinihub.activity.channel.detail.DetailChannelActivity.Companion.IS_CHANNEL_DES
import com.amary21.trinihub.activity.channel.detail.DetailChannelActivity.Companion.IS_CHANNEL_POST
import com.amary21.trinihub.activity.channel.detail.DetailChannelActivity.Companion.IS_CHANNEL_SUB
import kotlinx.android.synthetic.main.item_channel.view.*

class ChannelSubAdapter(val context: Context) :
    ListAdapter<Channel, ChannelSubAdapter.ChannelSubViewHolder>(
        object : DiffUtil.ItemCallback<Channel>(){
            override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean {
                return oldItem.id == newItem.id
            }

        }
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelSubViewHolder {
        return ChannelSubViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_channel,
                parent,
                false)
            )
    }


    override fun onBindViewHolder(holder: ChannelSubViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class ChannelSubViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(channel: Channel) {
            itemView.tvTitleChannel.text = channel.getDisplayName("no channel name")
            itemView.tvDesChannel.text = channel.description

            val avatatar = channel.channelAvatar
            if (avatatar != null) {
                itemView.imgAvatarChannel.setImageBitmap(avatatar)
            }

            itemView.setOnClickListener {
                val intent = Intent(context, DetailChannelActivity::class.java)
                intent.putExtra(ID_CHANNEL, channel.id)
                intent.putExtra(IS_CHANNEL_POST, channel.isOwner)
                intent.putExtra(IS_CHANNEL_SUB, channel.isSubscribed)
                intent.putExtra(IS_CHANNEL_DES, channel.description)
                context.startActivity(intent)
            }
        }

    }
}
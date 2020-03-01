package com.amary21.trinihub.activity.channel.detail

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.http.adapter.concurrent.RainbowServiceException
import com.ale.infra.manager.channel.Channel
import com.ale.infra.manager.channel.ChannelItem
import com.ale.infra.manager.channel.ChannelItemBuilder
import com.ale.infra.proxy.channel.IChannelProxy
import com.ale.rainbowsdk.RainbowSdk
import com.amary21.trinihub.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.item_post_channel.view.*
import org.jetbrains.anko.runOnUiThread
import java.text.DateFormat


class DetailChannelAdapter(var context: Context, private var channelItem: ArrayList<ChannelItem>, var channel: Channel) :
    RecyclerView.Adapter<DetailChannelAdapter.DetailChannelViewHolder>() {

    private var isOwned: Boolean = false

    fun setIsOwned(owned: Boolean) {
        this.isOwned = owned
    }

    fun getIsOwned(): Boolean {
        return isOwned
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailChannelViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_post_channel, parent, false)
        return DetailChannelViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.channelItem.size
    }

    override fun onBindViewHolder(holder: DetailChannelViewHolder, position: Int) {
        holder.bind(this.channelItem[position])
    }

    inner class DetailChannelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var btmCreateDialog: BottomSheetDialog

        fun bind(item: ChannelItem) {
            btmCreateDialog = BottomSheetDialog(context)

            if (getIsOwned()) {
                itemView.btnModifyPost.visibility = View.VISIBLE
                itemView.btnModifyPost.setOnClickListener {
                    val popupMenu = PopupMenu(context, it)
                    popupMenu.setOnMenuItemClickListener {itemClick ->
                        when(itemClick.itemId){
                            R.id.mn_update ->{
                                openUpdateSheet(it, item.message, item.id)
                                true
                            }
                            R.id.mn_delete ->{
                                openDeleteItem(it, channel.id, item.id)
                                true
                            }
                            else -> false
                        }
                    }

                    popupMenu.inflate(R.menu.menu_modify_post)
                    try {
                        val fieldPopupMenu = PopupMenu::class.java.getDeclaredField("mPopup")
                        fieldPopupMenu.isAccessible = true
                        val mPopup = fieldPopupMenu.get(popupMenu)
                        mPopup.javaClass
                            .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                            .invoke(mPopup, true) as Boolean
                    }catch (e : Exception){
                        Log.e("DATA CHANNEL","Error", e)
                    }finally {
                        popupMenu.show()
                    }
                }
            } else {
                itemView.btnModifyPost.visibility = View.INVISIBLE
            }

            val displayName = item.contact.getDisplayName("no display name")
            val dateTime = item.date

            itemView.tvItemUserName.text = displayName
            itemView.tvItemDate.text = DateFormat.getDateTimeInstance().format(dateTime)
            itemView.tvItemPost.text = item.message

            val contactAvatar = item.contact.photo
            if (contactAvatar != null) {
                itemView.imgItemAvatar.setImageBitmap(contactAvatar)
            }
        }

        @SuppressLint("InflateParams")
        private fun openUpdateSheet(
            v: View?,
            message: String,
            id: String
        ) {
            val context = v?.context
            val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val btmView = inflater.inflate(R.layout.bottom_update_post, null)

            val edtUpdatePost = btmView.findViewById<EditText>(R.id.edtUpdatePostChannel)
            val btnUpdatePost = btmView.findViewById<LinearLayout>(R.id.btnUpdatePostChannel)

            val mBottomSheetDialog = BottomSheetDialog(context)
            mBottomSheetDialog.setContentView(btmView)
            mBottomSheetDialog.setCancelable(true)
            mBottomSheetDialog.window?.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            mBottomSheetDialog.window?.setGravity(Gravity.BOTTOM)
            mBottomSheetDialog.show()

            edtUpdatePost.setText(message)

            btnUpdatePost.setOnClickListener {
                val channelItemBuilder = ChannelItemBuilder().setMessage(edtUpdatePost.text.toString()).build()
                RainbowSdk.instance().channels().updateItem(channel, id, channelItemBuilder, object : IChannelProxy.IChannelCreateItemListener{
                    override fun onCreateMessageItemFailed(p0: RainbowServiceException?) {
                        Log.e("DATA UPDATE", p0?.message.toString())
                    }

                    override fun onCreateMessageItemSuccess(p0: String?) {
                        context.runOnUiThread {
                            notifyDataSetChanged()
                            mBottomSheetDialog.cancel()
                        }
                    }

                })
            }
        }

        private fun openDeleteItem(
            v: View,
            channelId: String?,
            itemId: String?
        ) {
            val context = v.context
            RainbowSdk.instance().channels().deleteItem(channelId, itemId, object : IChannelProxy.IChannelDeleteItemListener{
                override fun onDeleteItemFailed(p0: RainbowServiceException?) {

                }

                override fun onDeleteItemSuccess() {
                    context.runOnUiThread {
                        notifyDataSetChanged()
                    }
                }

            })
        }
    }

}
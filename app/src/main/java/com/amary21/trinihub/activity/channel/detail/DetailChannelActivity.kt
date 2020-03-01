package com.amary21.trinihub.activity.channel.detail

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ale.infra.http.adapter.concurrent.RainbowServiceException
import com.ale.infra.manager.channel.Channel
import com.ale.infra.manager.channel.ChannelItem
import com.ale.infra.manager.channel.ChannelItemBuilder
import com.ale.infra.manager.channel.ChannelMgr
import com.ale.infra.proxy.avatar.IAvatarProxy
import com.ale.infra.proxy.channel.IChannelProxy
import com.ale.rainbowsdk.RainbowSdk
import com.amary21.trinihub.R
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vincent.filepicker.Constant
import com.vincent.filepicker.activity.ImagePickActivity
import com.vincent.filepicker.filter.entity.ImageFile
import kotlinx.android.synthetic.main.activity_detail_channel.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DetailChannelActivity : AppCompatActivity() {

    companion object {
        const val ID_CHANNEL = "com.amary21.trinihub.activity.channel.detail"
        const val IS_CHANNEL_POST = "com.amary21.trinihub.activity.channel.detail.IS_CHANNEL_POST"
        const val IS_CHANNEL_SUB = "com.amary21.trinihub.activity.channel.detail.IS_CHANNEL_SUB"
        const val IS_CHANNEL_DES = "com.amary21.trinihub.activity.channel.detail.IS_CHANNEL_DES"
    }

    private var channelItems: ArrayList<ChannelItem> = ArrayList()
    private var pickedImage: String? = null
    private var channelID: String? = null
    private var channel: Channel? = null
    private var postChannelAdapter: DetailChannelAdapter? = null
    private lateinit var btmCreateDialog: BottomSheetDialog
    private lateinit var btmCreateDialogView: View
    private var channelListener = object : IChannelProxy.IChannelFetchItemsListener {
        override fun onFetchItemsFailed(p0: RainbowServiceException?) {

        }

        override fun onFetchItemsSuccess() {
            refreshList()
        }

    }
    private val avatatarListener = object : IAvatarProxy.IAvatarListener {
        override fun onAvatarFailure(p0: RainbowServiceException?) {
            Log.e("ERROR DATA PHOTO", p0.toString())
        }

        override fun onAvatarSuccess(p0: Bitmap?) {
            runOnUiThread {
                Glide.with(this@DetailChannelActivity).load(pickedImage).into(imgPhotoDetailChannel)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_channel)
        btmCreateDialog = BottomSheetDialog(this)

        btnBackPostChannel.setOnClickListener {
            onBackPressed()
            finish()
        }

        channelID = intent.getStringExtra(ID_CHANNEL)
        if (channelID != null) {

            channel = RainbowSdk.instance().channels().getChannel(channelID)
            getTitleChannel(channel)
            getPostChannel(channel)
            getPhotoAvatar(channel)

            btnPostChannel.setOnClickListener { createPostChannel(channel) }
            imgAddPhotoDetailChannel.setOnClickListener { photoDetailChannel(channelID) }
        }

        postChannelAdapter = channel?.let { DetailChannelAdapter(this, channelItems, it) }
        rvPostChannel.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@DetailChannelActivity)
            adapter = postChannelAdapter
        }

        val isOwned = intent.getBooleanExtra(IS_CHANNEL_POST, false)
        val isSuscribed = intent.getBooleanExtra(IS_CHANNEL_SUB, false)
        val desChannel = intent.getStringExtra(IS_CHANNEL_DES)

        postChannelAdapter?.setIsOwned(isOwned)
        if (isOwned) {
            layoutImageAvatar.visibility = View.VISIBLE
            layoutPost.visibility = View.VISIBLE
            btnEditDescriptionChannel.visibility = View.VISIBLE
            btnFollowthisChannel.visibility = View.GONE
            btnEditDescriptionChannel.setOnClickListener {
                updateDesChannel(channel, desChannel)
            }
        } else {
            btnEditDescriptionChannel.visibility = View.GONE
            btnFollowthisChannel.visibility = View.VISIBLE
            layoutImageAvatar.visibility = View.GONE
            layoutPost.visibility = View.GONE
            if (isSuscribed) {
                btnFollowthisChannel.text = "unfollow"
            } else {
                btnFollowthisChannel.text = "follow"
            }

            btnFollowthisChannel.setOnClickListener {
                followChannel(isSuscribed, channel)
            }
        }
    }

    private fun followChannel(
        suscribed: Boolean,
        channel: Channel?
    ) {
        if (suscribed) {
            RainbowSdk.instance().channels()
                .unsubscribeToChannel(channel, object : IChannelProxy.IChannelUnsubscribeListener {
                    override fun onUnsubscribeSuccess() {
                        runOnUiThread {
                            btnFollowthisChannel.text = "follow"
                        }
                    }

                    override fun onUnsubscribeFailed(p0: RainbowServiceException?) {

                    }

                })
        } else {
            RainbowSdk.instance().channels()
                .subscribeToChannel(channel, object : IChannelProxy.IChannelSubscribeListener {
                    override fun onSubscribeSuccess(p0: Channel?) {
                        runOnUiThread {
                            btnFollowthisChannel.text = "unfollow"
                        }
                    }

                    override fun onSubscribeFailed(p0: RainbowServiceException?) {

                    }

                })
        }
    }

    private fun updateDesChannel(
        channel: Channel?,
        desChannel: String?
    ) {
        btmCreateDialogView = layoutInflater.inflate(R.layout.bottom_des_channel, null)
        btmCreateDialog.setContentView(btmCreateDialogView)
        btmCreateDialog.show()

        val edtUpdateDesChannel =
            btmCreateDialogView.findViewById<EditText>(R.id.edtUpdateDesChannel)
        val btnSaveDesChannel =
            btmCreateDialogView.findViewById<Button>(R.id.btnBottomEditDesChannel)
        val btnDeletChannel =
            btmCreateDialogView.findViewById<Button>(R.id.btnBottomDeleteThisChannel)

        edtUpdateDesChannel.setText(desChannel)
        btnSaveDesChannel.setOnClickListener {
            if (edtUpdateDesChannel.text != null || edtUpdateDesChannel.text.isNotEmpty()) {
                RainbowSdk.instance().channels().updateChannelDescription(
                    channel,
                    edtUpdateDesChannel.text.toString(),
                    object : IChannelProxy.IChannelUpdateListener {
                        override fun onUpdateSuccess(p0: Channel?) {
                            runOnUiThread {
                                btmCreateDialog.cancel()
                            }
                        }

                        override fun onUpdateFailed(p0: RainbowServiceException?) {
                            Log.e("DATA DES", p0?.message.toString())
                        }

                    })
            }
        }

        btnDeletChannel.setOnClickListener {
            RainbowSdk.instance().channels()
                .deleteChannel(channel, object : IChannelProxy.IChannelDeleteListener {
                    override fun onDeleteSuccess() {
                        runOnUiThread {
                            onBackPressed()
                            finish()
                        }
                    }

                    override fun onDeleteFailed(p0: RainbowServiceException?) {

                    }

                })
        }
    }

    private fun getPhotoAvatar(channel: Channel?) {
        val photo = channel?.channelAvatar
        if (photo != null) {
            imgPhotoDetailChannel.setImageBitmap(photo)
        }
    }


    private fun getPostChannel(channel: Channel?) {
        RainbowSdk.instance().channels()
            .fetchItems(channel, ChannelMgr.DEFAULT_LATEST_ITEMS_COUNT, channelListener)
        channel?.channelItems?.registerChangeListener(this::refreshList)
    }

    private fun getTitleChannel(channel: Channel?) {
        if (channel != null) {
            tvChannelNameDetail.text = channel.getDisplayName("")
        }
    }

    private fun createPostChannel(channel: Channel?) {
        val postChannel = tvCreatePostChannel.text.toString()
        if (postChannel.isNotEmpty()) {
            val channelItemBuilder = ChannelItemBuilder().setMessage(postChannel).build()
            RainbowSdk.instance().channels().createItem(channel, channelItemBuilder, object :
                IChannelProxy.IChannelCreateItemListener {
                override fun onCreateMessageItemFailed(p0: RainbowServiceException?) {

                }

                override fun onCreateMessageItemSuccess(p0: String?) {
                    refreshList()
                    tvCreatePostChannel.text = null
                }

            })
        }
    }

    private fun refreshList() {
        channelItems.clear()
        val channelPostItem = channel?.channelItems?.copyOfDataList
        if (channelPostItem != null ){
            if (channelPostItem.size >= 1){
                channelItems.addAll(channelPostItem)
                runOnUiThread {
                    tvNoPostChannel.visibility = View.GONE
                    rvPostChannel.visibility = View.VISIBLE
                    rvPostChannel.adapter?.notifyDataSetChanged()
                }
            }else{
                runOnUiThread {
                    tvNoPostChannel.visibility = View.VISIBLE
                    rvPostChannel.visibility = View.GONE
                }
            }
        }else{
            runOnUiThread {
                tvNoPostChannel.visibility = View.VISIBLE
                rvPostChannel.visibility = View.GONE
            }
        }
    }


    private fun photoDetailChannel(channelID: String?) {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val intent = Intent(this, ImagePickActivity::class.java)
            intent.putExtra(Constant.MAX_NUMBER, 1)
            startActivityForResult(intent, Constant.REQUEST_CODE_TAKE_IMAGE)
        } else {
            EasyPermissions.requestPermissions(
                this, "This application need your permission to access photo gallery.", 991,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            photoDetailChannel(channelID)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_CODE_TAKE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            pickedImage =
                data.getParcelableArrayListExtra<ImageFile>(Constant.RESULT_PICK_IMAGE)[0]?.path
            if (pickedImage != null) {
                val fileImage = File(pickedImage)
                RainbowSdk.instance().channels()
                    .uploadChannelAvatar(channelID, fileImage, avatatarListener)
            }
        }
    }
}

package com.amary21.trinihub.activity.chat_room

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ale.infra.contact.IRainbowContact
import com.ale.infra.http.adapter.concurrent.RainbowServiceException
import com.ale.infra.list.IItemListChangeListener
import com.ale.infra.manager.IMMessage
import com.ale.infra.manager.fileserver.IFileProxy
import com.ale.infra.manager.fileserver.RainbowFileDescriptor
import com.ale.infra.proxy.conversation.IRainbowConversation
import com.ale.listener.IRainbowImListener
import com.ale.rainbowsdk.RainbowSdk
import com.amary21.trinihub.R
import com.amary21.trinihub.activity.account_info.AccountClientActivity
import com.amary21.trinihub.rainbow.RainbowConnection
import com.amary21.trinihub.utils.PermissionsHelper
import kotlinx.android.synthetic.main.activity_chatroom.*


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ChatroomActivity : AppCompatActivity(), IRainbowImListener {

    private var chatRoomAdapter: ChatRoomAdapter? = null
    private lateinit var conversation: IRainbowConversation
    private val messages = mutableListOf<IMMessage>()
    private var scrollToBottom = true
    private val changeListener = IItemListChangeListener(::getMessages)
    private val uploadListener = object : IFileProxy.IUploadFileListener{
        override fun onUploadFailed(p0: RainbowFileDescriptor?, p1: RainbowServiceException?) {
            runOnUiThread {
                pbLoadingUploadFile.visibility = View.GONE
            }
        }

        override fun onUploadSuccess(p0: RainbowFileDescriptor?) {
            runOnUiThread {
                pbLoadingUploadFile.visibility = View.GONE
                rv_chat_list.adapter?.notifyDataSetChanged()
                Toast.makeText(this@ChatroomActivity, "File uploaded, Please check storage in your app", Toast.LENGTH_SHORT).show()

            }
        }

        override fun onUploadInProgress(p0: Int) {
            runOnUiThread {
                pbLoadingUploadFile.visibility = View.VISIBLE
            }
        }

    }

    companion object {
        const val NB_MESSAGE_TO_RETRIEVE = 100
        const val PICK_FILE_RESULT_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)

        onHeaderAppbar()

        val contactJID = intent.getStringExtra("JID")
        getIntentData(intent)

        conversation = RainbowConnection.getConversationFromContact(contactJID)
        conversation.messages.registerChangeListener(changeListener)
        RainbowSdk.instance().im().registerListener(this)

        chatRoomAdapter = ChatRoomAdapter(messages)

        retrieveMessages()
        rv_chat_list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ChatroomActivity)
            adapter = chatRoomAdapter
        }

        srRefreshChatRoom.setOnRefreshListener {
            retrieveMoreMessages()
        }

        btn_send_message.setOnClickListener {
            sendMessage()
        }

        btn_send_file.setOnClickListener {
            sendFile()
        }

        btnBackChatRoom.setOnClickListener {
            backChatRoom()
        }

        edt_message_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    RainbowSdk.instance().im().sendIsTyping(conversation, true)
                } else {
                    RainbowSdk.instance().im().sendIsTyping(conversation, false)
                }
            }

        })

    }

    private fun sendFile() {
        if (PermissionsHelper.instance()!!.isExternalStorageAllowed(this, this)) {
            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            intent = Intent.createChooser(intent, "Choose a file")
            startActivityForResult(intent, PICK_FILE_RESULT_CODE)
        } else {
            sendFile()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FILE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null)
                return

            val uri = data.data

            if (conversation.isRoomType){
                pbLoadingUploadFile.visibility = View.VISIBLE
                RainbowSdk.instance().fileStorage().uploadFileToBubble(conversation.room, uri, "File Upload", uploadListener)
            }else{
                pbLoadingUploadFile.visibility = View.VISIBLE
                RainbowSdk.instance().fileStorage().uploadFileToConversation(conversation, uri, "File Upload", uploadListener)
            }
        }

    }

    private fun getIntentData(intent: Intent) {
        //Person Data
        val mDisplayName = intent.getStringExtra("displayName")
        val mFirstName = intent.getStringExtra("firstName")
        val mLastName = intent.getStringExtra("lastName")
        val mNickName = intent.getStringExtra("nickName")

        //Email Data
        val mFirstEmail = intent.getStringExtra("firstEmail")
        val mMainEmail = intent.getStringExtra("mainEmail")

        //Phone Number Data
        val mPhoneNumber = intent.getStringExtra("phoneNumber")

        //Photo Data
        val mIsPhoto = intent.getBooleanExtra("isPhoto", false)
        val mPhotoProfile = intent.getByteArrayExtra("photoProfile")
        val mNamePhotoProfile = intent.getStringExtra("nameProfile")

        //Job Data
        val mJobTitle = intent.getStringExtra("jobTitle")
        val mCompany = intent.getStringExtra("company")


        lyProfileAccount.setOnClickListener {
            getProfileAccount(
                mDisplayName,
                mFirstName,
                mLastName,
                mNickName,
                mFirstEmail,
                mMainEmail,
                mPhoneNumber,
                mIsPhoto,
                mPhotoProfile,
                mNamePhotoProfile,
                mJobTitle,
                mCompany
            )
        }

    }

    private fun getProfileAccount(
        mDisplayName: String?,
        mFirstName: String?,
        mLastName: String?,
        mNickName: String?,
        mFirstEmail: String?,
        mMainEmail: String?,
        mPhoneNumber: String?,
        mIsPhoto: Boolean,
        mPhotoProfile: ByteArray?,
        mNamePhotoProfile: String?,
        mJobTitle: String?,
        mCompany: String?
    ) {
        val intent = Intent(this, AccountClientActivity::class.java)

        //Person Data
        intent.putExtra("displayName", mDisplayName)
        intent.putExtra("firstName", mFirstName)
        intent.putExtra("lastName", mLastName)
        intent.putExtra("nickName", mNickName)

        //Email Data
        intent.putExtra("firstEmail", mFirstEmail)
        intent.putExtra("mainEmail", mMainEmail)

        //Phone Number Data
        intent.putExtra("phoneNumber", mPhoneNumber)

        //Photo Data
        intent.putExtra("isPhoto", mIsPhoto)
        intent.putExtra("photoProfile", mPhotoProfile)
        intent.putExtra("nameProfile", mNamePhotoProfile)

        //Job Data
        intent.putExtra("jobTitle", mJobTitle)
        intent.putExtra("company", mCompany)

        startActivity(intent)
    }

    private fun backChatRoom() {
        onBackPressed()
        finish()
    }

    private fun retrieveMoreMessages() {
        scrollToBottom = false
        RainbowSdk.instance().im()
            .getMoreMessagesFromConversation(conversation, NB_MESSAGE_TO_RETRIEVE)
    }

    private fun retrieveMessages() {
        RainbowSdk.instance().im().getMessagesFromConversation(conversation, NB_MESSAGE_TO_RETRIEVE)
    }

    private fun onHeaderAppbar() {

        val nameProfile = intent.getStringExtra("displayName")
        tvTitleProfile.text = nameProfile

        val isPhoto = intent.getBooleanExtra("isPhoto", false)
        if (isPhoto) {
            val photoProfile = intent.getByteArrayExtra("photoProfile")
            val bitmap = BitmapFactory.decodeByteArray(photoProfile, 0, photoProfile.size)
            tvChatRoomProfile.setImageBitmap(bitmap)
            tvChatRoomProfile.visibility = View.VISIBLE
            rlProfileChatRoom.visibility = View.GONE
        } else {
            val namePhotoProfile = intent.getStringExtra("nameProfile")
            tvChatRoomProfile.visibility = View.GONE
            rlProfileChatRoom.visibility = View.VISIBLE
            tvNameProfileChatRoom.text = namePhotoProfile
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        conversation.messages.unregisterChangeListener(changeListener)
        RainbowSdk.instance().im().unregisterListener(this)
    }

    private fun getMessages() {
        messages.clear()
        conversation.messages?.copyOfDataList?.let { messages.addAll(it) }

        runOnUiThread {
            rv_chat_list.adapter?.notifyDataSetChanged()
            if (scrollToBottom)
                rv_chat_list.scrollToPosition(chatRoomAdapter?.itemCount!! - 1)
            srRefreshChatRoom.isEnabled = false
            spLoadingChatRoom.visibility = View.GONE
            scrollToBottom = true
        }
    }

    private fun sendMessage() {
        val inputMessage: String = edt_message_input.text.toString()
        val validInput = inputMessage.replace("\\s+".toRegex(), " ")
        RainbowSdk.instance().im().sendMessageToConversation(conversation, validInput)
        edt_message_input.setText("")
    }

    override fun onImSent(p0: String?, p1: IMMessage?) {
    }

    override fun onImReceived(p0: String?, p1: IMMessage?) {
        if (!conversation.isRoomType) {
            RainbowSdk.instance().im().markMessageFromConversationAsRead(conversation, p1)
            runOnUiThread {
                chatRoomAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onMessagesListUpdated(p0: Int, p1: String?, p2: MutableList<IMMessage>?) {
    }

    override fun isTypingState(p0: IRainbowContact?, p1: Boolean, p2: String?) {
        if (p1) {
            runOnUiThread {
                tvStatusProfileChatRoom.text = "is typing.."
            }
        } else {
            runOnUiThread {
                tvStatusProfileChatRoom.text = ""
            }
        }
    }

    override fun onMoreMessagesListUpdated(p0: Int, p1: String?, p2: MutableList<IMMessage>?) {
    }
}

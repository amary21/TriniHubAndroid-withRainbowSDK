package com.amary21.trinihub.activity.main.chat


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ale.infra.list.IItemListChangeListener
import com.ale.infra.proxy.conversation.IRainbowConversation
import com.ale.rainbowsdk.RainbowSdk
import com.amary21.trinihub.R
import com.amary21.trinihub.activity.contact.ContactActivity
import com.amary21.trinihub.activity.main.MainActivity
import com.amary21.trinihub.data.local.Account
import com.amary21.trinihub.data.local.database
import kotlinx.android.synthetic.main.fragment_chat.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() {

    private var email: String? = null
    private var pass: String? = null

    private lateinit var activity: MainActivity
    private val conversations = mutableListOf<IRainbowConversation>()
    private val changeListener = IItemListChangeListener(::getConversations)

    private lateinit var chatAdapter: ChatAdapter

    companion object {
        fun newInstance() = ChatFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity() as MainActivity
        chatAdapter = ChatAdapter(activity, conversations)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        RainbowSdk.instance().conversations()
            .allConversations.registerChangeListener(changeListener)
        conversations.clear()
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rvChatList.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = chatAdapter
        }

        loadDataLocal()
        swpRefreshChat.setOnRefreshListener {
            getConversations()
        }
        getConversations()

        btnAddChat.setOnClickListener {
            context?.startActivity(Intent(context, ContactActivity::class.java))
        }
    }

    private fun loadDataLocal() {
        context?.database?.use {
            val result = select(Account.TABLE_ACCOUNT)
            val account = result.parseList((classParser<Account>()))

            email = account[0].loginEmaul
            pass = account[0].password
        }
    }

    private fun getConversations() {
        spLoadingChat.visibility = View.VISIBLE
        conversations.clear()
        conversations.addAll(RainbowSdk.instance().conversations().allConversations.copyOfDataList)

        activity.runOnUiThread {
            rvChatList.adapter?.notifyDataSetChanged()
            spLoadingChat.visibility = View.GONE
            swpRefreshChat.isRefreshing = false
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        RainbowSdk.instance().conversations().allConversations.unregisterChangeListener(
            changeListener
        )
    }


}

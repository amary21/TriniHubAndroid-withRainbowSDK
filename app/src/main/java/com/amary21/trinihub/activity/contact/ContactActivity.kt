package com.amary21.trinihub.activity.contact

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.ale.infra.contact.Contact
import com.ale.infra.contact.IRainbowContact
import com.ale.infra.contact.RainbowPresence
import com.ale.infra.http.adapter.concurrent.RainbowServiceException
import com.ale.infra.list.IItemListChangeListener
import com.ale.infra.proxy.users.IUserProxy
import com.ale.listener.IRainbowContactsSearchListener
import com.ale.listener.IRainbowSentInvitationListener
import com.ale.rainbowsdk.RainbowSdk
import com.amary21.trinihub.R
import com.amary21.trinihub.activity.chat_room.ChatroomActivity
import kotlinx.android.synthetic.main.activity_contact.*
import java.io.ByteArrayOutputStream

class ContactActivity : AppCompatActivity(), IRainbowContact.IContactListener {


    private val contacts = mutableListOf<IRainbowContact>()
    private lateinit var contactsAdapter: ContactAdapter

    // Listener on contacts
    private val changeListener = IItemListChangeListener(::getContacts)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        contactsAdapter = ContactAdapter(contacts, this, ::addOrRemoveContactToRoster, ::contactClicked)
        RainbowSdk.instance().contacts().rainbowContacts.registerChangeListener(changeListener)

        rvListContact.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ContactActivity)
            adapter = contactsAdapter
        }

        btnBackContact.setOnClickListener { backContact() }

        swpRefreshContact.setOnRefreshListener { getContacts() }

        getContacts()

        searchView()
    }

    private fun backContact() {
        onBackPressed()
        finish()
    }

    private fun searchView() {
        btnSearchContact.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()){
                    RainbowSdk.instance().contacts().searchByName(newText, object : IRainbowContactsSearchListener{
                        override fun searchStarted() {
                            unregisterListener()
                        }

                        override fun searchError(p0: RainbowServiceException?) {
                            Toast.makeText(this@ContactActivity, p0.toString(), Toast.LENGTH_SHORT).show()
                        }

                        override fun searchFinished(p0: MutableList<Contact>) {
                            contacts.clear()
                            if (p0.isNotEmpty())
                                contacts.addAll(p0)
                            runOnUiThread {
                                contactsAdapter.notifyDataSetChanged()
                            }
                        }

                    })
                }else{
                    getContacts()
                }

                return true
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterListener()
        RainbowSdk.instance().contacts().rainbowContacts.unregisterChangeListener(changeListener)
    }

    private fun contactClicked(contact: IRainbowContact) {
        // go chat room
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

        val intent = Intent(this, ChatroomActivity::class.java)
        intent.putExtra("JID", contact.jid)

        intent.putExtra("displayName", contact.getDisplayName("Unknown"))
        intent.putExtra("firstName", contact.firstName)
        intent.putExtra("lastName", contact.lastName)
        intent.putExtra("nickName", contact.nickName)

        //Email Data
        intent.putExtra("firstEmail", contact.firstEmailAddress)
        intent.putExtra("mainEmail", contact.mainEmailAddress)

        //Phone Number Data
        if (contact.firstOfficePhoneNumber != null){
            intent.putExtra("phoneNumber", contact.firstOfficePhoneNumber.phoneNumberValue)
        }else{
            intent.putExtra("phoneNumber", "")
        }

        if (contact.photo != null){
            intent.putExtra("isPhoto", true)
            val bmpStream = ByteArrayOutputStream()
            contact.photo.compress(Bitmap.CompressFormat.PNG, 50, bmpStream)
            val byteArray = bmpStream.toByteArray()
            intent.putExtra("photoProfile", byteArray)
        }else{
            intent.putExtra("isPhoto", false)
            intent.putExtra("nameProfile", inisialFirstName + inisialLastName)
        }

        //Job Data
        intent.putExtra("jobTitle", contact.jobTitle)
        intent.putExtra("company", contact.companyName)

        startActivity(intent)
    }

    private fun addOrRemoveContactToRoster(contact: IRainbowContact, add: Boolean) = when (add) {
        false -> RainbowSdk.instance().contacts().removeContactFromRoster(contact.id, object : IUserProxy.IContactRemovedFromRosterListener{
            override fun onSuccess() {
                runOnUiThread { contactsAdapter.notifyDataSetChanged() }
            }

            override fun onFailure(p0: RainbowServiceException?) {
            }

        })
        else -> RainbowSdk.instance().contacts().addRainbowContactToRoster(contact, object : IRainbowSentInvitationListener{
            override fun onInvitationError() {
            }

            override fun onInvitationSentError(p0: RainbowServiceException?) {
            }

            override fun onInvitationSentSuccess() {
                runOnUiThread { contactsAdapter.notifyDataSetChanged() }
            }

        })
    }

    private fun getContacts() {
        unregisterListener()
        contacts.clear()
        contacts.addAll(RainbowSdk.instance().contacts().rainbowContacts.copyOfDataList)
        registerListener()

        runOnUiThread {
            rvListContact.adapter?.notifyDataSetChanged()
            swpRefreshContact.isRefreshing = false
        }
    }

    private fun unregisterListener() {
        for (contact in contacts){
            contact.unregisterChangeListener(this)
        }
    }

    private fun registerListener() {
        for (contact in contacts){
            contact.registerChangeListener(this)
        }
    }

    override fun onCompanyChanged(p0: String?) {
        runOnUiThread {
            contactsAdapter.notifyDataSetChanged()
        }
    }

    override fun onPresenceChanged(p0: IRainbowContact?, p1: RainbowPresence?) {
        runOnUiThread {
            contactsAdapter.notifyDataSetChanged()
        }
    }

    override fun onActionInProgress(p0: Boolean) {}

    override fun contactUpdated(p0: IRainbowContact?) {}
}

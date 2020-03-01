package com.amary21.trinihub.rainbow

import android.os.Handler
import android.os.Looper
import com.ale.infra.contact.IRainbowContact
import com.ale.infra.list.IItemListChangeListener
import com.ale.infra.manager.channel.Channel
import com.ale.infra.proxy.conversation.IRainbowConversation
import com.ale.listener.SigninResponseListener
import com.ale.listener.StartResponseListener
import com.ale.rainbowsdk.RainbowSdk

object RainbowConnection {

    private val rainbowContacts: List<IRainbowContact>
        get() = RainbowSdk.instance().contacts().rainbowContacts.copyOfDataList

    val rainbowInstance: RainbowSdk
        get() = RainbowSdk.instance()

    private val allChannels: List<Channel>
        get() = RainbowSdk.instance().channels().allChannels.copyOfDataList


    fun startConnection(connection: RainbowConnectionListener.Connection) {
        RainbowSdk.instance().connection().start(object : StartResponseListener() {
            override fun onStartSucceeded() {
                Handler(Looper.getMainLooper())
                    .post(connection::onConnectionSuccess)
            }

            override fun onRequestFailed(errorCode: RainbowSdk.ErrorCode, s: String) {
                Handler(Looper.getMainLooper())
                    .post {
                        connection.onConnectionFailed(s)
                    }
            }
        })
    }

    fun startSignIn(email: String, password: String, login: RainbowConnectionListener.Login) {
        RainbowSdk.instance().connection()
            .signin(email, password, "sandbox.openrainbow.com", object : SigninResponseListener() {
                override fun onSigninSucceeded() {
                    login::onSignInSuccess
                }

                override fun onRequestFailed(errorCode: RainbowSdk.ErrorCode, s: String) {
                    Handler(Looper.getMainLooper())
                        .post {
                            login.onSignInFailed(s)
                        }
                }
            })
    }


    fun getConversationFromContact(contactJid: String?): IRainbowConversation =
        RainbowSdk.instance().conversations().getConversationFromContact(contactJid)

    fun registerChannelChangeListener(listener: IItemListChangeListener) =
        RainbowSdk.instance().channels().allChannels.registerChangeListener(listener)

    fun unregisterChannelChangeListener(listener: IItemListChangeListener) =
        RainbowSdk.instance().channels().allChannels.unregisterChangeListener(listener)

    fun registerAllChannel(listener: Channel.IChannelListener) {
        for (channel in allChannels) {
            channel.registerListener(listener)
            channel.mode = Channel.ChannelMode.ALL_PUBLIC
        }
    }

    fun unregisterAllChannel(listener: Channel.IChannelListener) {
        for (channel in allChannels) {
            channel.unregisterListener(listener)
            channel.mode = Channel.ChannelMode.ALL_PUBLIC
        }
    }



    fun getMessagesFromConversation(conversation: IRainbowConversation) =
        RainbowSdk.instance().im().getMessagesFromConversation(conversation, 100)

    fun sendMessageToConversation(conversation: IRainbowConversation, message: String) =
        RainbowSdk.instance().im().sendMessageToConversation(conversation, message)

    fun registerAllRainbowContact(listener: IItemListChangeListener) =
        RainbowSdk.instance().contacts().rainbowContacts.registerChangeListener(listener)

    fun unregisterAllRainbowContact(listener: IItemListChangeListener) =
        RainbowSdk.instance().contacts().rainbowContacts.unregisterChangeListener(listener)

    fun registerContactChangeListener(listener: IRainbowContact.IContactListener) {
        rainbowContacts.forEach {
            it.registerChangeListener(listener)
        }
    }

    fun unregisterContactChangeListener(listener: IRainbowContact.IContactListener) {
        rainbowContacts.forEach {
            it.unregisterChangeListener(listener)
        }
    }

}
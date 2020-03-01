package com.amary21.trinihub.rainbow

import android.os.Handler
import android.os.Looper
import com.ale.infra.contact.IRainbowContact
import com.ale.infra.contact.RainbowPresence

class RainbowConnectionListener {

    abstract class Contact : IRainbowContact.IContactListener {
        override fun onCompanyChanged(p0: String?) {
        }

        override fun onPresenceChanged(p0: IRainbowContact?, p1: RainbowPresence?) {
            Handler(Looper.getMainLooper())
                .post { contactUpdated() }
        }

        override fun onActionInProgress(p0: Boolean) {

        }

        override fun contactUpdated(p0: IRainbowContact?) {
            Handler(Looper.getMainLooper())
                .post { contactUpdated() }
        }

        abstract fun contactUpdated()
    }

    interface Connection {
        fun onConnectionSuccess()

        fun onConnectionFailed(error: String)
    }

    interface Login {
        fun onSignInSuccess()

        fun onSignInFailed(error: String)
    }
}
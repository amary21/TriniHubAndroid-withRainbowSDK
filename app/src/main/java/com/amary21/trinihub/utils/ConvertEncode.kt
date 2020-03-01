package com.amary21.trinihub.utils

import android.content.Context
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


object ConvertEncode {

    fun base64Encode(emailOrId: String, password: String): String {
        val data: ByteArray?
        val auth = "$emailOrId:$password"

        data = auth.toByteArray()

        return Base64.encodeToString(data, Base64.NO_WRAP)
    }

    fun hideSoftKey(view: View) {
        if (view.context != null) {
            val iml =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            iml.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    @Throws(NoSuchAlgorithmException::class)
    fun getSHA(input: String): ByteArray? {
        val md: MessageDigest = MessageDigest.getInstance("SHA-256")
        return md.digest(input.toByteArray())
    }

    fun toHexString(hash: ByteArray?): String? {
        val number = BigInteger(1, hash)
        val hexString = StringBuilder(number.toString(16))
        while (hexString.length < 32) {
            hexString.insert(0, '0')
        }
        return hexString.toString()
    }
}
package com.amary21.trinihub.utils

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object UtilsForm {
    fun createFormString(description: String): RequestBody {
        return description.toRequestBody(MultipartBody.FORM)
    }
}
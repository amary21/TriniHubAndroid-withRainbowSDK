package com.amary21.trinihub.data.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhoneNumbers (
    @SerializedName("number") val number : String?,
    @SerializedName("type") val type : String?,
    @SerializedName("deviceType") val deviceType : String?
) : Parcelable
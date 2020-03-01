package com.amary21.trinihub.data.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Emails (
    @SerializedName("email") val email : String,
    @SerializedName("type") val type : String
) : Parcelable
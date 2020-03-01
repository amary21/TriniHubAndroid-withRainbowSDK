package com.amary21.trinihub.data.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    @SerializedName("id") val id : String?,
    @SerializedName("loginEmail") val loginEmail : String?,
    @SerializedName("firstName") val firstName : String?,
    @SerializedName("lastName") val lastName : String?,
    @SerializedName("displayName") val displayName : String?,
    @SerializedName("jobTitle") val jobTitle : String?,
    @SerializedName("companyName") val companyName : String?,
    @SerializedName("nickName") val nickName : String?,
    @SerializedName("emails") val emails : List<Emails>?,
    @SerializedName("phoneNumbers") val phoneNumbers : List<PhoneNumbers>?,
    @SerializedName("country") val country : String?,
    @SerializedName("language") val language : String?,
    @SerializedName("visibility") val visibility : String?,
    @SerializedName("isActive") val isActive : Boolean?,
    @SerializedName("isInitialized") val isInitialized : Boolean?
) : Parcelable
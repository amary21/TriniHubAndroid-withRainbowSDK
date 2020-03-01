package com.amary21.trinihub.data.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MeetHub(
    @SerializedName("id_meet") val id_meet: String,
    @SerializedName("name_meet") val name_meet: String,
    @SerializedName("des_meet") val des_meet: String,
    @SerializedName("date") val date: String,
    @SerializedName("time_start") val time_start: String,
    @SerializedName("time_end") val time_end: String,
    @SerializedName("place_name") val name_place: String,
    @SerializedName("address") val address: String,
    @SerializedName("invitation") val invitation: String,
    @SerializedName("category") val category: String,
    @SerializedName("id_user") val id_user: String,
    @SerializedName("photo") val photo: String?
) : Parcelable
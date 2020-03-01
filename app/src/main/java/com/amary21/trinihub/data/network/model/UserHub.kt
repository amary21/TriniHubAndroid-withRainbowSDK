package com.amary21.trinihub.data.network.model

import com.google.gson.annotations.SerializedName

data class UserHub (
    @SerializedName("id_user") val id_user : String,
    @SerializedName("name_user") val name_user : String,
    @SerializedName("email_user") val email_user : String,
    @SerializedName("phone_user") val phone_user : String,
    @SerializedName("job_title") val job_title : String
)
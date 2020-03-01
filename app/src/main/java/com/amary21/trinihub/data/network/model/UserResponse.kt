package com.amary21.trinihub.data.network.model

import com.google.gson.annotations.SerializedName

data class UserResponse (
    @SerializedName("data") val data : User
)
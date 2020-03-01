package com.amary21.trinihub.data.network.model

import com.google.gson.annotations.SerializedName

data class UserHubResponse (
    @SerializedName("result") val result : List<UserHub>
)
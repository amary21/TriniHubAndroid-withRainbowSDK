package com.amary21.trinihub.data.network.model

import com.google.gson.annotations.SerializedName

data class MeetHubResponse (
    @SerializedName("result") val result : List<MeetHub>
)
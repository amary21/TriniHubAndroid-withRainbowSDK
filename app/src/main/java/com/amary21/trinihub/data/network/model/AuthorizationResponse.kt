package com.amary21.trinihub.data.network.model

import com.google.gson.annotations.SerializedName

data class AuthorizationResponse (
    @SerializedName("token") val token : String,
    @SerializedName("loggedInUser") val loggedInUser : LoggedInUser
//    @SerializedName("loggedInApplication") val loggedInApplication : LoggedInApplication
)
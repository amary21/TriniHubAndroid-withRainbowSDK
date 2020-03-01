package com.amary21.trinihub.data.local

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Account(
    val id : Long?,
    val idUser : String?,
    val token : String?,
    val firstName : String?,
    val lastName : String?,
    val displayName : String?,
    val jobTitle : String?,
    val workEmail : String?,
    val homeEmail : String?,
    val phone : String?,
    val language : String?,
    val location : String?,
    val loginEmaul : String?,
    val password : String?,
    val nickName : String?,
    val company : String?

) : Parcelable {
    companion object{
        const val TABLE_ACCOUNT : String = "TABLE_ACCOUNT"
        const val ID : String = "ID_"
        const val ID_USER : String = "ID_USER"
        const val TOKEN : String = "TOKEN_"
        const val FIRST_NAME : String = "FIRST_NAME"
        const val LAST_NAME : String = "LAST_NAME"
        const val DISPLAY_NAME : String = "DISPLAY_NAME"
        const val JOB_TITLE : String = "JOB_TITLE"
        const val WORK_EMAIL : String = "WORK_EMAIL"
        const val HOME_EMAIL : String = "HOME_EMAIL"
        const val PHONE : String = "PHONE_"
        const val LANGUAGE : String = "LANGUAGE_"
        const val LOCATION : String = "LOCATION_"
        const val LOGIN_EMAIL : String = "LOGIN_EMAIL"
        const val PASSWORD : String = "PASSWORD_"
        const val NICKNAME : String = "NICKNAME_"
        const val COMPANY : String = "COMPANY_"

    }
}
package com.amary21.trinihub.data.local

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GuestList (
    val id : Long?,
    val idUser : String?,
    val idMeet : String?
) : Parcelable{
    companion object{
        const val TABLE_ACCOUNT : String = "TABLE_GUESTLIST"
        const val ID : String = "ID_"
        const val ID_USER : String = "ID_USER"
        const val ID_MEET : String = "ID_MEET"
    }
}
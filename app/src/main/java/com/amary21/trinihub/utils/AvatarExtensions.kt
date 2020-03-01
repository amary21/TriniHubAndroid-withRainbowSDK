package com.amary21.trinihub.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Resources
import android.graphics.*
import android.text.TextPaint
import com.ale.infra.contact.IRainbowContact
import com.ale.infra.manager.room.Room
import com.amary21.trinihub.R
import java.util.*

fun IRainbowContact.getPictureForRainbowContact(activity: Activity) : Bitmap =
    if (this.photo != null)
        this.photo
    else
        getPictureByInitials(activity, this.firstName.substring(0,1) + this.lastName.substring(0,1))

fun Room.getPictureForRoom(activity: Activity) : Bitmap =
    when {
        this.photo != null -> this.photo
        this.name.isNotEmpty() -> {
            getPictureByInitials(activity, this.name.substring(0, 2).toUpperCase(Locale.getDefault()))
        }
        else -> {
            getPictureByInitials(activity, this.name)
        }
    }


@SuppressLint("ResourceAsColor")
fun getPictureByInitials(activity: Activity, initials: String) : Bitmap {
    val drawable = activity.getDrawable(R.drawable.shape_profile) ?: throw Resources.NotFoundException()

    val picture = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(picture)

    drawable.apply {
        bounds = Rect(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        draw(canvas)
    }

    val paint = TextPaint()
    paint.apply {
        style = Paint.Style.FILL_AND_STROKE
        typeface = Typeface.DEFAULT_BOLD
        textSize = 48f
        color = R.color.colorFontAccount2
    }

    canvas.drawText(
        initials,
        (picture.width / 2) - 25f,
        (picture.height / 2) + 12f,
        paint
    )

    return picture
}
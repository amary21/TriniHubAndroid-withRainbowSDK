package com.amary21.trinihub.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionsHelper private constructor() {
    fun isExternalStorageAllowed(
        context: Context?,
        activity: Activity?
    ): Boolean {
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_STORAGE
            )
            return false
        }
        return true
    }

    companion object {
        private const val PERMISSION_STORAGE = 1
        private var singleton: PermissionsHelper? = null
        fun instance(): PermissionsHelper? {
            if (singleton == null) {
                singleton = PermissionsHelper()
            }
            return singleton
        }
    }
}
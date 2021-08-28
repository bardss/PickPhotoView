package com.jakubaniola.pickphotoview

import android.content.Context
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import com.jakubaniola.pickphotoview.utils.ImageFileHandler
import com.jakubaniola.pickphotoview.utils.PermissionsUtil

class PickPhotoImageUtil(private val compressQuality: Int = 60) {

    fun getBitmapFromPath(context: Context, path: String?, cornerRadius: Float = 20f): RoundedBitmapDrawable? {
        return if (PermissionsUtil.isStoragePermissionGranted(context)) {
             ImageFileHandler(compressQuality).getBitmapDrawableFromPath(context, path, cornerRadius)
        } else null
    }
}
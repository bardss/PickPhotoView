package com.jakubaniola.pickphotoview

import android.content.Intent

interface PickPhotoActions {
    fun startActivityForResult(intent: Intent, requestCode: Int)
    fun setOnCorrectPhotoPickListener(path: String) { }
}
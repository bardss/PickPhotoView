package com.jakubaniola.pickphotoview

import android.content.Intent

interface PickPhotoReceiver {
    fun onPicturePicked(resultCode: Int, intent: Intent?)
}
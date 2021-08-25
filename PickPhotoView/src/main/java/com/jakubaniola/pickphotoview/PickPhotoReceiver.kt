package com.jakubaniola.pickphotoview

import android.content.Intent

interface PickPhotoReceiver {
    fun onPicturePicked(requestCode: Int, resultCode: Int, intent: Intent?)
}
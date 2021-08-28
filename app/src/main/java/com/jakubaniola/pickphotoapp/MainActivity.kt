package com.jakubaniola.pickphotoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jakubaniola.pickphotoview.PickPhotoActions
import com.jakubaniola.pickphotoview.PickPhotoLayout

class MainActivity : AppCompatActivity(), PickPhotoActions {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<PickPhotoLayout>(R.id.pick_photo_layout).setPickPhotoFragment(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        findViewById<PickPhotoLayout>(R.id.pick_photo_layout).onPicturePicked(requestCode, resultCode, data)
    }

    override fun setOnCorrectPhotoPickListener(path: String) {
        Log.e("onCorrectPhoto:", path)
    }
}
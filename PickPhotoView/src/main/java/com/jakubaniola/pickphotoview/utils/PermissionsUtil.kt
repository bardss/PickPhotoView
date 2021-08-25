package com.jakubaniola.pickphotoview.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission

internal object PermissionsUtil {

    fun isStoragePermissionGranted(context: Context): Boolean {
        val storageWritePermission =
            context.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val storageReadPermission =
            context.checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        return storageWritePermission == PackageManager.PERMISSION_GRANTED &&
                storageReadPermission == PackageManager.PERMISSION_GRANTED
    }

    fun isCameraPermissionGranted(context: Context): Boolean {
        val cameraPermission =
            context.checkCallingOrSelfPermission(Manifest.permission.CAMERA)
        return cameraPermission == PackageManager.PERMISSION_GRANTED
    }

    fun isMicrophonePermissionGranted(context: Context): Boolean {
        val microphonePermission =
            context.checkCallingOrSelfPermission(Manifest.permission.RECORD_AUDIO)
        return microphonePermission == PackageManager.PERMISSION_GRANTED
    }

    fun askForCameraPermission(
        context: Context?,
        onDeniedAction: () -> Unit = {},
        onGrantedAction: () -> Unit = {}
    ) {
        if (context != null) {
            AndPermission
                .with(context)
                .runtime()
                .permission(Permission.Group.CAMERA)
                .onDenied { onDeniedAction() }
                .onGranted { onGrantedAction() }
                .start()
        }
    }

    fun askForStoragePermission(
        context: Context?,
        onDeniedAction: () -> Unit = {},
        onGrantedAction: () -> Unit = {}
    ) {
        if (context != null) {
            AndPermission
                .with(context)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onDenied { onDeniedAction() }
                .onGranted { onGrantedAction() }
                .start()
        }
    }

    fun askForMicrophonePermission(
        context: Context?,
        onDeniedAction: () -> Unit = {},
        onGrantedAction: () -> Unit = {}
    ) {
        if (context != null) {
            AndPermission
                .with(context)
                .runtime()
                .permission(Permission.Group.MICROPHONE)
                .onDenied { onDeniedAction() }
                .onGranted { onGrantedAction() }
                .start()
        }
    }
}
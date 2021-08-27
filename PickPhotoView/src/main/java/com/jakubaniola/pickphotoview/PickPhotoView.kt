package com.jakubaniola.pickphotoview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.jakubaniola.pickphotoview.PickPhotoViewMode.*
import com.jakubaniola.pickphotoview.utils.ImageUtils
import com.jakubaniola.pickphotoview.utils.PermissionsUtil


internal class PickPhotoView(
    context: Context,
    private val mode: PickPhotoViewMode,
    private val placeholderPicture: Drawable
) : FrameLayout(context), PickPhotoReceiver {

    private val requestPhotoCode = UniqueIdGenerator.generateNextId()
    private val selectedPictureImageView: ImageView
    private val editPictureImageView: ImageView
    private var takePhotoFileUri: Uri? = null
    private var pickPhotoActions: PickPhotoActions? = null

    var picturePath: String? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_pick_photo, this)
        selectedPictureImageView = findViewById(R.id.selected_picture_image_view)
        editPictureImageView = findViewById(R.id.edit_picture_image_view)
        setupPickPhotoOnClick()
        setupView()
        setupPlaceholderPicture()
    }

    private fun setupPlaceholderPicture() {
        selectedPictureImageView.background = placeholderPicture
    }

    fun setPickPhotoFragment(pickPhotoActions: PickPhotoActions?) {
        this.pickPhotoActions = pickPhotoActions
    }

    override fun onPicturePicked(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == requestPhotoCode && resultCode == Activity.RESULT_OK) {
            intent?.data?.let { uri ->
                onPickPhotoSuccess(uri)
            }
            takePhotoFileUri?.let { uri ->
                onTakePhotoSuccess(uri)
            }
        }
    }

    private fun addNextPickPhotoViewInParent() {
        val parent = parent as PickPhotoLayout
        parent.addPickPhotoView()
    }

    private fun setPickPhotoReceiverInParent() {
        val parent = parent as PickPhotoLayout
        parent.setPickPhotoReceiver(this)
    }

    private fun onPickPhotoSuccess(uri: Uri) {
        val pathToPhoto = ImageUtils.getPathFromUri(context, uri)
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (storageDir != null) {
            val pathToOptimisedFile =
                ImageUtils.saveOptimisedPictureFromPathAndReturnPath(pathToPhoto, storageDir)
            setPathAsSelectedPicture(pathToOptimisedFile)
        }
    }

    private fun onTakePhotoSuccess(uri: Uri) {
        val pathToPhoto = ImageUtils.getPathFromUri(context, uri)
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (storageDir != null) {
            val pathToOptimisedFile =
                ImageUtils.overwriteOptimisedPictureFromPathAndReturnPath(pathToPhoto)
            setPathAsSelectedPicture(pathToOptimisedFile)
            if (mode == ENABLE_ADD_MULTIPLE) {
                addNextPickPhotoViewInParent()
            }
        }
    }

    fun setPathAsSelectedPicture(path: String?) {
        val pictureBitmap = ImageUtils.getBitmapDrawableFromPath(context, path)
        if (pictureBitmap != null) {
            this.picturePath = path
            editPictureImageView.setImageDrawable(context.resources.getDrawable(R.drawable.ic_edit))
            selectedPictureImageView.setImageDrawable(pictureBitmap)
        }
    }

    private fun setupPickPhotoOnClick() {
        when (mode) {
//            ONLY_SHOW -> onClickWithShowMode()
            ENABLE_ADD_ONE, ENABLE_ADD_MULTIPLE -> setupOnPickPhotoClick()
        }
    }

    private fun setupView() {
        when (mode) {
            ONLY_SHOW -> setupOnlyShowView()
            ENABLE_ADD_ONE, ENABLE_ADD_MULTIPLE -> setupEnableAddView()
        }
    }

    private fun setupOnPickPhotoClick() {
        setOnClickListener { onClickWithAddMode() }
    }

    private fun setupOnlyShowView() {
        editPictureImageView.visibility = View.GONE
        selectedPictureImageView.alpha = 1f
    }

    private fun setupEnableAddView() {
        editPictureImageView.visibility = View.VISIBLE
        selectedPictureImageView.alpha = 0.4f
    }

    private fun onClickWithAddMode() {
        takePhotoFileUri = null
        setPickPhotoReceiverInParent()
        if (!PermissionsUtil.isStoragePermissionGranted(context) &&
            !PermissionsUtil.isCameraPermissionGranted(context)
        ) {
            PermissionsUtil.askForStoragePermission(context) {
                PermissionsUtil.askForCameraPermission(context,
                    onDeniedAction = { startActivityGallery() },
                    onGrantedAction = { startActivityGalleryAndCamera() })
            }
        } else if (!PermissionsUtil.isStoragePermissionGranted(context) &&
            PermissionsUtil.isCameraPermissionGranted(context)
        ) {
            PermissionsUtil.askForStoragePermission(context) {
                startActivityGalleryAndCamera()
            }
        } else if (PermissionsUtil.isStoragePermissionGranted(context) &&
            !PermissionsUtil.isCameraPermissionGranted(context)
        ) {
            PermissionsUtil.askForCameraPermission(context,
                onDeniedAction = { startActivityGallery() },
                onGrantedAction = { startActivityGalleryAndCamera() }
            )
        } else if (PermissionsUtil.isStoragePermissionGranted(context) &&
            PermissionsUtil.isCameraPermissionGranted(context)
        ) {
            startActivityGalleryAndCamera()
        }
    }

    private fun startActivityGallery() {
        val photoPickerIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        photoPickerIntent.type = "image/*"
        pickPhotoActions?.startActivityForResult(photoPickerIntent, requestPhotoCode)
    }

    private fun startActivityGalleryAndCamera() {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (storageDir != null) {
            val galleryPhoto =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryPhoto.type = "image/*"
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePhotoFileUri = ImageUtils.createFileAndGetURI(storageDir)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, takePhotoFileUri)
            val chooser =
                Intent.createChooser(galleryPhoto, context.resources.getString(R.string.gallery))
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
            pickPhotoActions?.startActivityForResult(chooser, requestPhotoCode)
        }
    }
}
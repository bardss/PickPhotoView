package com.jakubaniola.pickphotoview

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.children
import com.jakubaniola.pickphotoview.utils.ImageFileHandler

private const val DEFAULT_COMPRESS_QUALITY = 60

class PickPhotoLayout : LinearLayout {

    val pickPhotoViewId = UniqueIdGenerator.generateNextId()
    private var pickPhotoActions: PickPhotoActions? = null
    private var pickPhotoReceiver: PickPhotoReceiver? = null
    private var mode: PickPhotoViewMode = PickPhotoViewMode.ONLY_SHOW
    private lateinit var imageFileHandler: ImageFileHandler
    private lateinit var placeholderPicture: Drawable

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        saveAttributes(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        saveAttributes(context, attrs)
    }

    private fun saveAttributes(
        context: Context,
        attrs: AttributeSet?
    ) {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.PickPhotoLayout, 0, 0
        ).apply {
            placeholderPicture = getDrawable(R.styleable.PickPhotoLayout_placeholderPicture)
                ?: context.resources.getDrawable(R.drawable.ic_picture)
            val modeValue = getInt(R.styleable.PickPhotoLayout_mode, 0)
            mode = PickPhotoViewMode.values()[modeValue]
            val imageCompressQuality =
                getInt(R.styleable.PickPhotoLayout_imageCompressQuality, DEFAULT_COMPRESS_QUALITY)
            initImageFileHandler(imageCompressQuality)
            recycle()
        }
    }

    init {
        orientation = VERTICAL
    }

    private fun initImageFileHandler(imageCompressQuality: Int) {
        if (imageCompressQuality < 1 || imageCompressQuality > 100) {
            throw WrongCompressQualityException()
        } else {
            imageFileHandler = ImageFileHandler(imageCompressQuality)
        }
    }

    fun setPickPhotoFragment(pickPhotoActions: PickPhotoActions) {
        this.pickPhotoActions = pickPhotoActions
        if (mode.isAddEnabled()) {
            addPickPhotoView()
        }
    }

    fun setPictures(picturePaths: List<String>) {
        if (!isPicturePathsEmpty(picturePaths)) {
            visibility = View.VISIBLE
            val numberOfPictures = picturePaths.size
            for (i in getPickPhotoViewCount() until numberOfPictures) {
                addView(createPickPhotoView())
            }
            forEachPickPhotoViewIndexed { index, view ->
                picturePaths.getOrNull(index)?.let { path ->
                    view.setPathAsSelectedPicture(path)
                }
            }
        } else visibility = View.GONE
    }

    private fun isPicturePathsEmpty(picturePaths: List<String>): Boolean {
        return picturePaths.size == 1 && picturePaths.first() == ""
    }

    fun getPicturePaths(): List<String> {
        val picturePaths = mutableListOf<String?>()
        forEachPickPhotoViewIndexed { _, view ->
            val picturePath = view.picturePath
            picturePaths.add(picturePath)
        }
        return picturePaths.filterNotNull()
    }

    fun onPicturePicked(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == pickPhotoViewId) {
            pickPhotoReceiver?.onPicturePicked(resultCode, intent)
        }
    }

    fun setPickPhotoReceiver(pickPhotoReceiver: PickPhotoReceiver) {
        this.pickPhotoReceiver = pickPhotoReceiver
    }

    fun addPickPhotoView() {
        addView(createPickPhotoView())
    }

    private fun getPickPhotoViewCount(): Int =
        children
            .filterIsInstance<PickPhotoView>()
            .count()

    private fun forEachPickPhotoViewIndexed(action: (Int, PickPhotoView) -> Unit) {
        children
            .filterIsInstance<PickPhotoView>()
            .forEachIndexed(action)
    }

    private fun createPickPhotoView(): PickPhotoView {
        return PickPhotoView(
            context,
            mode,
            placeholderPicture,
            imageFileHandler,
            pickPhotoViewId
        ).apply {
            setPickPhotoFragment(pickPhotoActions)
        }
    }
}
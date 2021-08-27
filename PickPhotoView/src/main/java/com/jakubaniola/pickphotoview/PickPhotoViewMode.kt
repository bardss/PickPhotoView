package com.jakubaniola.pickphotoview

internal enum class PickPhotoViewMode {
    ONLY_SHOW,
    ENABLE_ADD_ONE,
    ENABLE_ADD_MULTIPLE;

    fun isAddEnabled() = this == ENABLE_ADD_ONE || this == ENABLE_ADD_MULTIPLE
}
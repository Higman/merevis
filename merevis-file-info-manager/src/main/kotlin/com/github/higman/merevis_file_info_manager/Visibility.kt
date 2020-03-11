package com.github.higman.merevis_file_info_manager

interface Visibility {
    fun isVisible(target: Node, user: Node) : Boolean
    abstract class VisibilityFactory {
        abstract fun getPublicVisibility(): Visibility
    }
}

class None: Visibility {
    override fun isVisible(target: Node, user: Node): Boolean = false
}
package com.github.higman.merevis_file_info_manager

open class Package(override val name: Name, override val visibility: Visibility) :
    Node() {
    override var parentNode: Node? = null
    override var childrenNode: MutableList<Node> = mutableListOf()
}
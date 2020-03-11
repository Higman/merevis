package com.github.higman.merevis_file_info_manager

abstract class Field(override val name: Name, override val visibility: Visibility) : ClassMember() {
    override var parentNode: Node? = null
    override var childrenNode: MutableList<Node> = mutableListOf()
}
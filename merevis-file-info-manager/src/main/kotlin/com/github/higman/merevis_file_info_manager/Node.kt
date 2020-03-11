package com.github.higman.merevis_file_info_manager

abstract class Node {
    abstract val name: Name
    abstract val visibility: Visibility
    abstract var parentNode: Node?
    abstract var childrenNode: MutableList<Node>
    fun isVisibleFrom(other: Node): Boolean = visibility.isVisible(this, other)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (name != other.name) return false
        if (visibility != other.visibility) return false

        if (childrenNode.size != other.childrenNode.size) return false
        if (childrenNode.any { c1 -> !other.childrenNode.any { c2 -> c1 == c2 } }) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + visibility.hashCode()
        result = 31 * result + childrenNode.hashCode()
        return result
    }

    override fun toString(): String {
        return "N(name=${name.fullyQualifiedName}, visibility=$visibility)"
    }


}
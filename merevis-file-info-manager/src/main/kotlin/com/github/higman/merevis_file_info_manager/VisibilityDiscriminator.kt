package com.github.higman.merevis_file_info_manager

/**
 * 与えられた可視性から可視範囲を判別する
 */
class VisibilityDiscriminator(val ast: Node) {
    var focusedNode: Node = ast
        set(value) {
            if (!isExist(value)) throw NoSuchNodeException()
            field = value
        }

    fun canReferTo(target: Node): Boolean {
        if (isExist(target)) throw NoSuchNodeException()
        return focusedNode.isVisibleFrom(target)
    }

    fun getVisibleNodes(): List<Node> {
        return ast.getVisibleNodesFromChildren(focusedNode)
    }

    fun isExist(searchTarget: Node): Boolean {
        return ast.has(searchTarget)
    }

    private fun Node.has(searchTarget: Node): Boolean =
        this == searchTarget || childrenNode.any { it.has(searchTarget) }

    private fun Node.getVisibleNodesFromChildren(focusNode: Node): List<Node> {
        return if (this.isVisibleFrom(focusNode)) {
            mutableListOf(this)
        } else {
            mutableListOf()
        }.also { it += this.childrenNode.map { it.getVisibleNodesFromChildren(focusNode) }.flatten() }

    }
}

class NoSuchNodeException : Exception()
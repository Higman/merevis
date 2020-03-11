package com.github.higman.merevis_file_info_manager

class TemporaryNode(
    override val name: Name,
    override val visibility: Visibility = None(),
    override var parentNode: Node? = null,
    override var childrenNode: MutableList<Node> = mutableListOf()
) : Node() {
}
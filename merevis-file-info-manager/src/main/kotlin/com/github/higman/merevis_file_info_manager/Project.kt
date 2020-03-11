package com.github.higman.merevis_file_info_manager

import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.nio.file.Path

open class Project(val directoryPath: Path, visibilityFactory: Visibility.VisibilityFactory, override val visibility: Visibility = visibilityFactory.getPublicVisibility()) :
    Node() {
    init {
        if (!Files.isDirectory(directoryPath)) throw IllegalArgumentException("Specified path is not directory path")
    }

    override val name: Name =
        Name(directoryPath.fileName.toString())
    override var parentNode: Node? = null
    override var childrenNode: MutableList<Node> = mutableListOf()
}
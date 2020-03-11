package com.github.higman.merevis_file_info_manager

import com.github.higman.merevis_file_info_manager.java.JavaVisibilityParser
import java.nio.file.Path

/**
 * 文字列によって可視性情報を提供するクラス。
 */
class VisibilityInfoManager(val rootDirectory: Path) {
    private val parser: VisibilityParser
    private var discriminator: VisibilityDiscriminator? = null
    private var ast: Node? = null

    init {
        parser = JavaVisibilityParser(rootDirectory)
    }

    fun analyze(): VisibilityInfoManager {
        ast = parser.build()
        discriminator = VisibilityDiscriminator(ast as Node)
        return this
    }

    fun getClassInfoList(): List<String> = parser.classOrInterfaceNodeMapper.map { it.value.name.fullyQualifiedName }
    fun getPackageInfoList(): List<String> = parser.packageNodeMapper.map { it.value.name.fullyQualifiedName }
    fun getFieldInfoList(): List<String> = parser.fieldNodeMapper.map { it.value.name.fullyQualifiedName }
    fun getMethodInfoList(): List<String> = parser.methodNodeMapper.map { it.value.name.fullyQualifiedName }

    fun getAllItem(): List<String> {
        return getClassInfoList() + getPackageInfoList() + getFieldInfoList() + getMethodInfoList()
    }

    fun getStringTree(): StringVisibilityNode {
        val a = ast ?: throw NotExecuteAnalyzeException()
        val identStr = getIdentifyStr(a)
        return StringVisibilityNode(a.name.fullyQualifiedName, identStr).apply {
            this.parent = this
            this.children = getStringTreeRec(a, this)
        }
    }

    private fun getIdentifyStr(a: Node): String {
        return when (a) {
            is ClassOrInterface -> "ClassOrInterface"
            is Field -> "Field"
            is Method -> "Method"
            is Package -> "Package"
            else -> "UnKnown"
        }
    }

    private fun getStringTreeRec(node: Node, parent: StringVisibilityNode): List<StringVisibilityNode> {
        return node.childrenNode.map {
            StringVisibilityNode(it.name.fullyQualifiedName, getIdentifyStr(it)).apply {
                this.parent = parent
                this.children = getStringTreeRec(it, this)
            }
        }
    }

    fun getChildren(parentItem: String): List<String> =
        searchNode(parentItem).childrenNode.map { it.name.fullyQualifiedName }

    fun getVisibleList(focusItem: String): List<String> {
        val node = searchNode(focusItem)
        return discriminator?.apply { focusedNode = node }?.getVisibleNodes()?.map { it.name.fullyQualifiedName }
            ?: throw NotExecuteAnalyzeException()
    }

    fun getVisibility(item: String) : String {
        val node = searchNode(item)
        return node.visibility.toString()
    }

    private fun searchNode(focusItem: String): Node {
        return (parser.classOrInterfaceNodeMapper[focusItem] ?: parser.packageNodeMapper[focusItem]
        ?: parser.fieldNodeMapper[focusItem] ?: parser.methodNodeMapper[focusItem]
        ?: throw IllegalArgumentException("No such item: $focusItem"))
    }
}

data class StringVisibilityNode(val item: String, val identifyStr: String) {
    var parent: StringVisibilityNode? = null
    var children: List<StringVisibilityNode> = mutableListOf()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StringVisibilityNode

        if (item != other.item) return false
        if (identifyStr != other.identifyStr) return false

        return true
    }

    override fun hashCode(): Int {
        var result = item.hashCode()
        result = 31 * result + identifyStr.hashCode()
        return result
    }

}

class NotExecuteAnalyzeException : RuntimeException()

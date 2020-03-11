package com.github.higman.merevis_file_info_manager

abstract class ClassOrInterface(override val name: Name, override val visibility: Visibility) :
    Node() {
    val superClassOrInterface = mutableListOf<ClassOrInterface>()
    var isInterface: Boolean = false
    override var parentNode: Node? = null
    override var childrenNode: MutableList<Node> = mutableListOf()

    /**
     * 自身が指定のクラス/インタフェースのスーパークラスであるか調べる
     */
    fun isSuperClassOf(clazz: ClassOrInterface) : Boolean {
        return clazz.superClassOrInterface.any {
            this == it || it.isSuperClassOf(this)
        }
    }

    /**
     * 自身が指定のクラス/インタフェースのインナークラスであるか調べる
     */
    fun isInnerClassOf(clazz: ClassOrInterface) : Boolean {
        var pNode: Node? = this.parentNode
        while(pNode is ClassOrInterface) {
            if (pNode == clazz) return true
            pNode = pNode.parentNode
        }
        return false
    }
}
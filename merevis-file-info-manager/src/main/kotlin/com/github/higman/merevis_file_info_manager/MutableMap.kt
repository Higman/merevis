package com.github.higman.merevis_file_info_manager

fun MutableMap<String, in Node>.putWithValidation(key: String, value: Node): Node? {
    val registeredNode = this[key] as? Node
    if (registeredNode != null) {
        value.parentNode = registeredNode.parentNode
        value.childrenNode.plusAssign(registeredNode.childrenNode)
        value.childrenNode.distinctBy { it.name }

        registeredNode.parentNode?.childrenNode?.apply { remove(registeredNode) }?.add(value)
        registeredNode.childrenNode.forEach {
            it.parentNode = value
        }
    }
    return this.put(key, value) as Node?
}

package com.github.higman.merevis_file_info_manager

data class Name(val fullyQualifiedName: String, val shortName: String = fullyQualifiedName) {
    override fun equals(other: Any?): Boolean {
        return if (other is Name) {
            fullyQualifiedName == other.fullyQualifiedName
        } else false
    }
}
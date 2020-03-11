package com.github.higman.merevis_file_info_manager

abstract class ClassMember : Node() {
    var isStatic: Boolean = false
    var isFinal: Boolean = false
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as ClassMember

        if (isStatic != other.isStatic) return false
        if (isFinal != other.isFinal) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + isStatic.hashCode()
        result = 31 * result + isFinal.hashCode()
        return result
    }

}
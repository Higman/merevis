package com.github.higman.merevis_file_info_manager.java.extractor

import com.github.higman.merevis_file_info_manager.java.JavaVisibility
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.PackageDeclaration
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithAccessModifiers

fun convertJavaVisibility(node: NodeWithAccessModifiers<*>): JavaVisibility {
    return when {
        node.isPublic -> JavaVisibility.PUBLIC
        node.isProtected -> JavaVisibility.PROTECTED
        node.isPrivate -> JavaVisibility.PRIVATE
        else -> JavaVisibility.PACKAGE_PRIVATE
    }
}

fun extractPackageString(unit: CompilationUnit): String {
    return unit.findAll(PackageDeclaration::class.java).stream().findFirst().map { it.childNodes.get(0).toString() }
        .orElseGet { "" }
}
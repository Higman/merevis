package com.github.higman.merevis_file_info_manager.java

import com.github.higman.merevis_file_info_manager.*
import com.github.higman.merevis_file_info_manager.java.extractor.JavaClassOrInterfaceExtractor
import com.github.higman.merevis_file_info_manager.java.extractor.JavaFieldExtractor
import com.github.higman.merevis_file_info_manager.java.extractor.JavaMethodExtractor
import com.github.higman.merevis_file_info_manager.java.extractor.JavaPackageExtractor
import com.github.higman.merevis_file_info_manager.java.nodes.JavaClassOrInterface
import com.github.higman.merevis_file_info_manager.java.nodes.JavaField
import com.github.higman.merevis_file_info_manager.java.nodes.JavaMethod
import com.github.higman.merevis_file_info_manager.java.nodes.JavaPackage
import java.nio.file.Path
import kotlin.reflect.KClass

class JavaVisibilityParser(rootDirectory: Path) : VisibilityParser(rootDirectory) {
    override lateinit var classOrInterfaceExtractor: ClassOrInterfaceExtractor
    override lateinit var packageExtractor: PackageExtractor
    override lateinit var fieldExtractor: FieldExtractor
    override lateinit var methodExtractor: MethodExtractor

    init {
        // TODO ここのキャスト処理をなくしたい
        val mappers: Array<Pair<KClass<out Node>, MutableMap<String, in Node>>> = arrayOf(
            Pair(JavaClassOrInterface::class, _classOrInterfaceNodeMapper as MutableMap<String, Node>),
            Pair(JavaPackage::class, _packageNodeMapper as MutableMap<String, Node>),
            Pair(JavaField::class, _fieldNodeMapper as MutableMap<String, Node>),
            Pair(JavaMethod::class, _methodNodeMapper as MutableMap<String, Node>))
        classOrInterfaceExtractor = JavaClassOrInterfaceExtractor(mappers)
        packageExtractor = JavaPackageExtractor(mappers)
        fieldExtractor = JavaFieldExtractor(mappers)
        methodExtractor = JavaMethodExtractor(mappers)
    }

    override fun isSpecifiedFile(file: Path): Boolean = file.toFile().extension == "java"

    override val visibilityFactory: Visibility.VisibilityFactory = JavaVisibility.JavaVisibilityFactory()
}
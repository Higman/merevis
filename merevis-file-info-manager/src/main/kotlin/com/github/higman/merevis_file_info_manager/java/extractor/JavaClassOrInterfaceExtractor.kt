package com.github.higman.merevis_file_info_manager.java.extractor

import com.github.higman.merevis_file_info_manager.*
import com.github.higman.merevis_file_info_manager.java.nodes.JavaClassOrInterface
import com.github.higman.merevis_file_info_manager.java.nodes.JavaPackage
import com.github.javaparser.ParseProblemException
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import java.util.*
import kotlin.reflect.KClass

class JavaClassOrInterfaceExtractor(mapperInfo: Array<Pair<KClass<out Node>, MutableMap<String, in Node>>>) :
    ClassOrInterfaceExtractor(mapperInfo) {
    override fun extract(): List<Pair<ClassOrInterface, String>> {
        val unit = try {
            StaticJavaParser.parse(targetCode)
        } catch (e: ParseProblemException) {
            throw IncorrectSyntaxSourceCodeException(targetCode ?: "")
        }
        val extractedInfo: MutableList<Pair<ClassOrInterface, String>> = mutableListOf()
        val packageStr = extractPackageString(unit)
        // TODO インナークラスへの対応
        registerClasses(unit, packageStr, extractedInfo)

        return extractedInfo
    }

    private fun registerClasses(
        unit: CompilationUnit,
        packageStr: String,
        extractedInfo: MutableList<Pair<ClassOrInterface, String>>
    ) {
        unit.findAll(ClassOrInterfaceDeclaration::class.java).stream().forEach { coid ->
            val name = Name("$packageStr.${coid.nameAsString}")
            val node = JavaClassOrInterface(name, convertJavaVisibility(coid)).apply {
                isInterface = coid.isInterface
                // すでにパッケージ情報が登録されているか検索
                val registeredPackageInfo = try {
                    nodeMappers[JavaPackage::class]?.entries?.first { it.key == packageStr }?.value as Node
                } catch (e: NoSuchElementException) {
                    null
                }
                // まだパッケージ情報が登録されていない場合、仮置き情報を登録
                parentNode = (registeredPackageInfo ?: TemporaryNode(Name(packageStr))).also { pn ->
                    // 親ノードの子ノードとして登録
                    pn.childrenNode.add(this)
                }
                parentNode?.let { nodeMappers[JavaPackage::class]?.put(packageStr, it) }
            }
            extractedInfo += Pair(node, coid.toString())
            nodeMappers[JavaClassOrInterface::class]?.putWithValidation(name.fullyQualifiedName, node)
        }
    }
}
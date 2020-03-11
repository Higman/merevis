package com.github.higman.merevis_file_info_manager.java.extractor

import com.github.higman.merevis_file_info_manager.*
import com.github.higman.merevis_file_info_manager.java.nodes.JavaClassOrInterface
import com.github.higman.merevis_file_info_manager.java.nodes.JavaField
import com.github.javaparser.ParseProblemException
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.VariableDeclarator
import java.util.*
import kotlin.reflect.KClass

class JavaFieldExtractor(mapperInfo: Array<Pair<KClass<out Node>, MutableMap<String, in Node>>>) :
    FieldExtractor(mapperInfo) {
    override fun extract(): List<Pair<Field, String>> {
        val unit = try {
            StaticJavaParser.parse(targetCode)
        } catch (e: ParseProblemException) {
            throw IncorrectSyntaxSourceCodeException(targetCode ?: "")
        }
        val extractedInfo: MutableList<Pair<Field, String>> = mutableListOf()
        val packageStr = extractPackageString(unit)
        registerFields(unit, packageStr, extractedInfo)
        return extractedInfo
    }

    private fun registerFields(
        unit: CompilationUnit,
        packageStr: String,
        extractedInfo: MutableList<Pair<Field, String>>
    ) {
        unit.findAll(FieldDeclaration::class.java).stream().forEach { fd ->
            val memberedClass = fd.parentNode.get() as ClassOrInterfaceDeclaration
            val classStr = "$packageStr.${memberedClass.nameAsString}"
            fd.childNodes.filterIsInstance<VariableDeclarator>().forEach { vd ->
                val fieldNameStr = vd.childNodes[1].toString()
                val fieldTypeStr = vd.typeAsString
                val name = Name("$classStr#${fieldNameStr}:$fieldTypeStr")
                val node = JavaField(name, convertJavaVisibility(fd)).apply {
                    isStatic = fd.isStatic
                    isFinal = fd.isFinal
                    // すでにフィールドを有するクラス情報が登録されているか検索
                    val registeredClassInfo = try {
                        nodeMappers[JavaClassOrInterface::class]?.entries?.first { it.key == classStr }?.value as Node
                    } catch (e: NoSuchElementException) {
                        null
                    }
                    // まだパッケージ情報が登録されていない場合、仮置き情報を登録
                    parentNode = (registeredClassInfo ?: TemporaryNode(Name(classStr))).also { pn ->
                        // 親ノードの子ノードとして登録
                        pn.childrenNode.add(this)
                    }
                    parentNode?.let { nodeMappers[JavaClassOrInterface::class]?.put(classStr, it) }
                }
                extractedInfo += Pair(node, fd.toString())
                nodeMappers[JavaField::class]?.putWithValidation(name.fullyQualifiedName, node)
            }
        }
    }
}
package com.github.higman.merevis_file_info_manager.java.extractor

import com.github.higman.merevis_file_info_manager.*
import com.github.higman.merevis_file_info_manager.java.nodes.JavaClassOrInterface
import com.github.higman.merevis_file_info_manager.java.nodes.JavaMethod
import com.github.javaparser.ParseProblemException
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import java.util.*
import kotlin.reflect.KClass

class JavaMethodExtractor(mapperInfo: Array<Pair<KClass<out Node>, MutableMap<String, in Node>>>) :
    MethodExtractor(mapperInfo) {
    override fun extract(): List<Pair<Method, String>> {
        val unit = try {
            StaticJavaParser.parse(targetCode)
        } catch (e: ParseProblemException) {
            throw IncorrectSyntaxSourceCodeException(targetCode ?: "")
        }
        val extractedInfo: MutableList<Pair<Method, String>> = mutableListOf()
        val packageStr = extractPackageString(unit)
        registerMethods(unit, packageStr, extractedInfo)
        return extractedInfo
    }

    private fun registerMethods(
        unit: CompilationUnit,
        packageStr: String,
        extractedInfo: MutableList<Pair<Method, String>>
    ) {
        unit.findAll(MethodDeclaration::class.java).stream().forEach { md ->
            val memberedClass = md.parentNode.get() as ClassOrInterfaceDeclaration
            val classStr = "$packageStr.${memberedClass.nameAsString}"
            val methodNameStr = md.nameAsString
            val methodTypeStr = md.typeAsString
            val methodParamStr =
                "(" + md.parameters.map { "${it.typeAsString}${if (it.isVarArgs) "..." else ""}" }.joinToString() + ")"
            val name = Name("$classStr#$methodNameStr$methodParamStr:$methodTypeStr")
            val node = JavaMethod(name, convertJavaVisibility(md)).apply {
                isStatic = md.isStatic
                isFinal = md.isFinal
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
            extractedInfo += Pair(node, md.toString())
            nodeMappers[JavaMethod::class]?.putWithValidation(name.fullyQualifiedName, node)
        }
    }
}
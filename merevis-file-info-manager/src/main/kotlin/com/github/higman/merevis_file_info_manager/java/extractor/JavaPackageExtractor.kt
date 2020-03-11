package com.github.higman.merevis_file_info_manager.java.extractor

import com.github.higman.merevis_file_info_manager.*
import com.github.higman.merevis_file_info_manager.java.nodes.JavaPackage
import com.github.javaparser.ParseProblemException
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.PackageDeclaration
import kotlin.reflect.KClass

class JavaPackageExtractor(mapperInfo: Array<Pair<KClass<out Node>, MutableMap<String, in Node>>>) :
    PackageExtractor(mapperInfo) {
    override fun extract(): List<Pair<Package, String>> {
        val unit = try {
            StaticJavaParser.parse(targetCode)
        } catch (e: ParseProblemException) {
            throw IncorrectSyntaxSourceCodeException(targetCode ?: "")
        }
        val extractedInfo: MutableList<Pair<Package, String>> = mutableListOf()
        val packageDec = unit.findAll(PackageDeclaration::class.java).stream().findFirst()
        val packageStr = packageDec.map { it.childNodes.get(0).toString() }.orElseGet { "" }
        // パッケージの登録
        registerPackages(packageStr)

        val javaPac = nodeMappers[JavaPackage::class]?.get(packageStr) as JavaPackage
        extractedInfo += Pair(javaPac, packageDec.let { if (it.isPresent) it.toString() else "" })

        return extractedInfo
    }

    private fun registerPackages(packageStr: String) {
        var prevPackageStr: Node? = null
        packageStr.split(".").toMutableList().fold("") { sum, acc ->
            val fullQualityName = if (sum.isEmpty()) acc else "$sum.$acc"
    //            val registeredPackage = nodeMappers[JavaPackage::class]?.get(fullQualityName)
            val newPackage = JavaPackage(Name(fullQualityName)).apply {
                parentNode = prevPackageStr
                parentNode?.childrenNode?.add(this)
                // 重複削除
                parentNode?.childrenNode =
                    parentNode?.childrenNode?.distinctBy { it.name }?.toMutableList() ?: mutableListOf()
            }
            prevPackageStr = newPackage
            nodeMappers[JavaPackage::class]?.putWithValidation(fullQualityName, newPackage)

            fullQualityName
        }
    }
}
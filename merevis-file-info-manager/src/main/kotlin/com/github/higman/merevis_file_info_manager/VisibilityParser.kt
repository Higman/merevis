package com.github.higman.merevis_file_info_manager

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.function.BiPredicate

/**
 * 指定のディレクトリ下に存在するソースコードから可視性情報を持つ抽象構文木を生成する責務を持つ。
 * クラスやパッケージのノードには、対応するファイルやディレクトリのパスを対応付けする。
 */
abstract class VisibilityParser(val rootDirectory: Path) {
    abstract val visibilityFactory: Visibility.VisibilityFactory
    var ast: Node? = null
        private set

    /**
     * マッパーへの登録ではキーに完全修飾名を指定すること。引数型や返却値型も完全修飾名
     * クラス/インターフェース: [ex: AAクラス]    aaa.bbb.cc.AAA
     * パッケージ: [ex: bbb パッケージ]          aaa.bbb
     * フィールド: [ex: AAクラスのフィールド f]  aaa.bbb.cc.AA#f:型
     * メソッド: [ex: AAクラスのフィールド m]    aaa.bbb.cc.AA#m(引数型, ...):返却値型
     */
    protected val _classOrInterfaceNodeMapper = mutableMapOf<String, ClassOrInterface>()
    protected val _packageNodeMapper = mutableMapOf<String, Package>()
    protected val _fieldNodeMapper = mutableMapOf<String, Field>()
    protected val _methodNodeMapper = mutableMapOf<String, Method>()

    val classOrInterfaceNodeMapper
        get() = _classOrInterfaceNodeMapper.toMap()
    val packageNodeMapper
        get() = _packageNodeMapper.toMap()
    val fieldNodeMapper
        get() = _fieldNodeMapper.toMap()
    val methodNodeMapper
        get() = _methodNodeMapper.toMap()

    protected abstract var classOrInterfaceExtractor: ClassOrInterfaceExtractor
    protected abstract var packageExtractor: PackageExtractor
    protected abstract var fieldExtractor: FieldExtractor
    protected abstract var methodExtractor: MethodExtractor

    /**
     * 抽象構文木生成メソッド
     */
    fun build(): Node {
        _classOrInterfaceNodeMapper.clear()
        _fieldNodeMapper.clear()
        _methodNodeMapper.clear()
        _packageNodeMapper.clear()

        ast = Project(rootDirectory, visibilityFactory)

        // 指定のディレクトリ下のファイルを操作し、パッケージ/クラス・インターフェース/フィールド/メソッドを抽出
        Files.find(
            rootDirectory,
            33,
            BiPredicate<Path, BasicFileAttributes> { file, attrs -> isSpecifiedFile(file) }).forEach { file ->
            val code = Files.readAllLines(file).joinToString("\n")
            packageExtractor.apply { targetCode = code }.extract()
            classOrInterfaceExtractor.apply { targetCode = code }.extract()
            fieldExtractor.apply { targetCode = code }.extract()
            methodExtractor.apply { targetCode = code }.extract()
        }

        // ルートノード（プロジェクト）に子ノードを設定
        ast?.childrenNode = (_classOrInterfaceNodeMapper + _packageNodeMapper).values.
            filter { it.parentNode == null && it !is TemporaryNode }.apply {
            // 子ノードの親ノードにルートノードを設定
            forEach { it.parentNode = ast }
        }.toMutableList()

        return ast ?: throw FailedToBuildVisibilityAST()
    }

    /**
     * 対象となるプログラミング言語のソースコードファイルかどうかを判定する
     */
    abstract fun isSpecifiedFile(file: Path): Boolean

    class FailedToBuildVisibilityAST : Exception()
}
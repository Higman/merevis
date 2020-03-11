package com.github.higman.merevis_file_info_manager

import kotlin.reflect.KClass


abstract class Extractor<T: Node>(mapperInfo: Array<Pair<KClass<out Node>, MutableMap<String, in Node>>>) {
    protected val nodeMappers: MutableMap<KClass<out Node>, MutableMap<String, in Node>> = mutableMapOf(*mapperInfo)

    // 抽出対象のコード
    var targetCode: String? = null
    // first elem = 抽出された Node, second elem = 抽出されたNodeに属する部分のコードの文字列
    abstract fun extract() : List<Pair<T, String>>
}

abstract class ClassOrInterfaceExtractor(mapperInfo: Array<Pair<KClass<out Node>, MutableMap<String, in Node>>>) :
    Extractor<ClassOrInterface>(mapperInfo)
abstract class PackageExtractor(mapperInfo: Array<Pair<KClass<out Node>, MutableMap<String, in Node>>>) :
    Extractor<Package>(mapperInfo)
abstract class FieldExtractor(mapperInfo: Array<Pair<KClass<out Node>, MutableMap<String, in Node>>>) :
    Extractor<Field>(mapperInfo)
abstract class MethodExtractor(mapperInfo: Array<Pair<KClass<out Node>, MutableMap<String, in Node>>>) :
    Extractor<Method>(mapperInfo)

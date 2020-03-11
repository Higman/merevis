package com.github.higman.merevis_file_info_manager

/**
 * 解析対象のソースコードの構文が不適切なときに発生する
 */
class IncorrectSyntaxSourceCodeException(code: String) : Exception(code)
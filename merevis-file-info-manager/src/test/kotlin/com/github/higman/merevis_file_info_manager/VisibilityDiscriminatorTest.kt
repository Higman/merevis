package com.github.higman.merevis_file_info_manager

import com.github.higman.merevis_file_info_manager.java.JavaVisibilityParser
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.nio.file.Path

internal class VisibilityDiscriminatorTest {
    companion object {
        lateinit var parser: JavaVisibilityParser
        @BeforeAll
        @JvmStatic
        fun setup() {
            parser = JavaVisibilityParser(Path.of(javaClass.getResource("/source_codes/testcase1").toURI()))
        }
    }

    @Test
    fun getVisibleNodesテスト() {
        // TODO ちゃんとしたの書く
        val p = parser
        val vd = VisibilityDiscriminator(p.build())
        vd.focusedNode = p.classOrInterfaceNodeMapper["aaa.bbb.BBB"] ?: fail("no register such node")
        val list = vd.getVisibleNodes()
        println(list)
    }
}
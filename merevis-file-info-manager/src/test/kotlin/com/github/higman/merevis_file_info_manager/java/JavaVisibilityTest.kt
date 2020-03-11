package com.github.higman.merevis_file_info_manager.java

import com.github.higman.merevis_file_info_manager.Name
import com.github.higman.merevis_file_info_manager.Node
import com.github.higman.merevis_file_info_manager.Project
import com.github.higman.merevis_file_info_manager.java.nodes.JavaClassOrInterface
import com.github.higman.merevis_file_info_manager.java.nodes.JavaField
import com.github.higman.merevis_file_info_manager.java.nodes.JavaPackage
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.nio.file.Path

internal class JavaVisibilityTest {
    companion object {
        val ast: Node = Project(Path.of(""), JavaVisibility.JavaVisibilityFactory())
        val A = JavaClassOrInterface(
            Name("aaa.A"),
            JavaVisibility.PUBLIC
        )
        val AA = JavaClassOrInterface(
            Name("aaa.bbb.AA"),
            JavaVisibility.PUBLIC
        )
        val f = JavaField(
            Name("aaa.bbb.AA#field:int"),
            JavaVisibility.PROTECTED
        )
        val AASub1 =
            JavaClassOrInterface(
                Name("aaa.bbb.AASub1"),
                JavaVisibility.PUBLIC
            ).apply { superClassOrInterface += AA }
        val BB = JavaClassOrInterface(
            Name("aaa.bbb.BB"),
            JavaVisibility.PRIVATE
        )
        val CC = JavaClassOrInterface(
            Name("aaa.bbb.CC"),
            JavaVisibility.PACKAGE_PRIVATE
        )

        @BeforeAll
        @JvmStatic
        fun setUp() {
            ast.childrenNode = mutableListOf(
                JavaPackage(Name("aaa")).also { aaa ->
                    aaa.childrenNode = mutableListOf(
                        JavaPackage(Name("bbb")).also { bbb ->
                            bbb.childrenNode = mutableListOf(
                                AA.apply {
                                    parentNode = bbb
                                    childrenNode.plusAssign(f.apply { parentNode =
                                        AA
                                    })
                                },
                                AASub1.apply { parentNode = bbb },
                                BB.apply { parentNode = bbb },
                                CC.apply { parentNode = bbb }
                            )
                        }.apply { parentNode = aaa },
                        A.apply { parentNode = aaa }
                    )
                }
            )
        }
    }

    @Test
    fun `PUBLIC#isVisibleテスト`() {
        assertTrue(AA.isVisibleFrom(AA))
        assertTrue(AA.isVisibleFrom(BB))
        assertTrue(AA.isVisibleFrom(CC))
        assertTrue(AA.isVisibleFrom(A))
        assertTrue(A.isVisibleFrom(AA))
        assertTrue(A.isVisibleFrom(BB))
        assertTrue(A.isVisibleFrom(CC))
        assertTrue(A.isVisibleFrom(A))
    }

    @Test
    fun `PRIVATE#isVisibleテスト`() {
        assertTrue(BB.isVisibleFrom(BB))
        assertFalse(BB.isVisibleFrom(AA))
        assertFalse(BB.isVisibleFrom(CC))
        assertFalse(CC.isVisibleFrom(A))
    }


    @Test
    fun `PACKAGE#isVisibleテスト`() {
        assertTrue(CC.isVisibleFrom(AA))
        assertTrue(CC.isVisibleFrom(BB))
        assertTrue(CC.isVisibleFrom(CC))
        assertFalse(CC.isVisibleFrom(A))
    }

    @Test
    fun `PROTECTED#isVisibleテスト`() {
        assertTrue(f.isVisibleFrom(AASub1))
        assertTrue(f.isVisibleFrom(AA))
        assertFalse(f.isVisibleFrom(BB))
        assertFalse(f.isVisibleFrom(CC))
        assertFalse(f.isVisibleFrom(A))
    }
}
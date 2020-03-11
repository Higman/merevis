package com.github.higman.merevis_file_info_manager.java

import com.github.higman.merevis_file_info_manager.Name
import com.github.higman.merevis_file_info_manager.Node
import com.github.higman.merevis_file_info_manager.Project
import com.github.higman.merevis_file_info_manager.VisibilityParser
import com.github.higman.merevis_file_info_manager.java.nodes.JavaClassOrInterface
import com.github.higman.merevis_file_info_manager.java.nodes.JavaField
import com.github.higman.merevis_file_info_manager.java.nodes.JavaMethod
import com.github.higman.merevis_file_info_manager.java.nodes.JavaPackage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.nio.file.Path

internal class JavaVisibilityParserTest {


    companion object {
        lateinit var javaVisibilityParser: VisibilityParser
        lateinit var expected: Node

        @BeforeAll
        @JvmStatic
        fun setup() {
            val rootDirectory = Path.of(javaClass.getResource("/source_codes/testcase1").toURI())
            javaVisibilityParser = JavaVisibilityParser(rootDirectory)
            expected = Project(rootDirectory, JavaVisibility.JavaVisibilityFactory()).also { pr ->
                pr.childrenNode = mutableListOf(
                    JavaPackage(Name("aaa")).also { aaa ->
                        aaa.parentNode = pr
                        aaa.childrenNode = mutableListOf(
                            JavaPackage(Name("aaa.bbb")).also { bbb ->
                                bbb.parentNode = aaa
                                bbb.childrenNode = mutableListOf(
                                    JavaClassOrInterface(Name("aaa.bbb.BBB"), JavaVisibility.PACKAGE_PRIVATE).also { BBB ->
                                        BBB.parentNode = bbb
                                        BBB.childrenNode = mutableListOf(
                                            JavaField(
                                                Name("aaa.bbb.BBB#b:int[]"),
                                                JavaVisibility.PROTECTED
                                            ).also { b1 ->
                                                b1.parentNode = BBB
                                            }
                                        )
                                    },
                                    JavaClassOrInterface(Name("aaa.bbb.CCC"), JavaVisibility.PUBLIC).also { CCC ->
                                        CCC.parentNode = bbb
                                        CCC.childrenNode = mutableListOf()
                                    },
                                    JavaClassOrInterface(Name("aaa.bbb.CCC2"), JavaVisibility.PACKAGE_PRIVATE).also { CCC2 ->
                                        CCC2.parentNode = bbb
                                        CCC2.childrenNode = mutableListOf()
                                    },
                                    JavaClassOrInterface(Name("aaa.bbb.DDD"), JavaVisibility.PACKAGE_PRIVATE).also { DDD ->
                                        DDD.parentNode = bbb
                                        DDD.childrenNode = mutableListOf(
                                            JavaMethod(Name("aaa.bbb.DDD#d():int"), JavaVisibility.PACKAGE_PRIVATE).also { d ->
                                                d.parentNode = DDD
                                            },
                                            JavaMethod(Name("aaa.bbb.DDD#dd(int, double, BBB, int...):double"), JavaVisibility.PACKAGE_PRIVATE).also { dd ->
                                                dd.parentNode = DDD
                                            }
                                        )
                                    },
                                    JavaPackage(Name("aaa.bbb.ccc")).also {ccc->
                                        ccc.parentNode = bbb
                                        ccc.childrenNode = mutableListOf(
                                            JavaClassOrInterface(Name("aaa.bbb.ccc.EEE"), JavaVisibility.PACKAGE_PRIVATE).also { EEE->
                                                EEE.parentNode = ccc
                                            }
                                        )
                                    }

                                )
                            },
                            JavaClassOrInterface(Name("aaa.AAA"), JavaVisibility.PACKAGE_PRIVATE).also { AAA ->
                                AAA.parentNode = aaa
                                AAA.childrenNode = mutableListOf(
                                    JavaField(Name("aaa.AAA#a1:int"), JavaVisibility.PUBLIC).also { a1 ->
                                        a1.parentNode = AAA
                                    },
                                    JavaField(Name("aaa.AAA#a2:int"), JavaVisibility.PUBLIC).also { a2 ->
                                        a2.parentNode = AAA
                                    },
                                    JavaField(Name("aaa.AAA#a3:int[]"), JavaVisibility.PUBLIC).also { a3 ->
                                        a3.parentNode = AAA
                                    },
                                    JavaField(Name("aaa.AAA#a4:double"), JavaVisibility.PACKAGE_PRIVATE).also { a4 ->
                                        a4.parentNode = AAA
                                    },
                                    JavaField(Name("aaa.AAA#a5:BBB"), JavaVisibility.PRIVATE).also { a5 ->
                                        a5.parentNode = AAA
                                    }
                                )
                            }
                        )
                    }
                )
            }
        }
    }

    @Test
    fun buildテスト() {
        val aa = javaVisibilityParser.build()
        assertEquals(expected, aa)
    }

}
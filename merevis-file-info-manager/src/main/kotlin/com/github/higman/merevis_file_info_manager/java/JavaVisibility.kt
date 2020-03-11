package com.github.higman.merevis_file_info_manager.java

import com.github.higman.merevis_file_info_manager.*

enum class JavaVisibility : Visibility {
    PUBLIC {
        override fun isVisible(target: Node, user: Node): Boolean = true
    },
    PROTECTED {
        override fun isVisible(target: Node, user: Node): Boolean {
            /**
             * userが属するクラスがtargetの属するクラスのサブクラスであるかをチェックし、またインナークラスをチェックする
             */
            return detectClassVisible(
                target
            ) { tClass ->
                detectClassVisible(user) { uClass ->
                    tClass == uClass || detectSubClass(
                        tClass,
                        uClass
                    )
                }
            } || detectInnerClass(
                target,
                user
            )
        }

        private fun detectSubClass(
            clazz: ClassOrInterface,
            `sub?`: ClassOrInterface
        ): Boolean {
            return clazz.isSuperClassOf(`sub?`)
        }
    },
    PACKAGE_PRIVATE {
        override fun isVisible(target: Node, user: Node): Boolean {
            return detectPackage(target, user) || detectInnerClass(
                target,
                user
            )
        }

        private fun detectPackage(target: Node, user: Node): Boolean {
            var tNode: Node? = target
            var uNode: Node? = user
            while (tNode != null && tNode !is Package) tNode = tNode?.parentNode
            while (uNode != null && uNode !is Package) uNode = uNode?.parentNode
            while (tNode != null && tNode == uNode) {
                tNode = tNode.parentNode
                uNode = uNode.parentNode
            }
            return tNode == uNode
        }
    },
    PRIVATE {
        override fun isVisible(target: Node, user: Node): Boolean {
            return detectClassVisible(
                target
            ) { tClass ->
                detectClassVisible(user) { uClass ->
                    tClass == uClass
                }
            }
        }
    };

    class JavaVisibilityFactory : Visibility.VisibilityFactory() {
        override fun getPublicVisibility(): Visibility =
            PUBLIC
    }

    companion object {
        /**
         * 指定したノードを所有するクラス（ノード自身を含む）を調べ、そのクラスに対して、第二引数で指定した関数を処理する。
         * 関数の結果の真偽値、または、指定したノードがクラスに属さなかった場合(PackageやProject)falseを返す
         */
        private fun detectClassVisible(node: Node, detect: (nClass: ClassOrInterface) -> Boolean): Boolean {
            return when (node) {
                is ClassOrInterface -> detect(node)
                is Field, is Method -> detect(node.parentNode as ClassOrInterface)
                else -> false
            }
        }

        /**
         * inner?がclazzのインナークラスかどうかを調べる
         */
        private fun detectInnerClass(
            clazz: Node,
            `inner?`: Node
        ): Boolean {
            return detectClassVisible(
                `inner?`
            ) { userClass ->
                detectClassVisible(clazz) { targetClass ->
                    userClass.isInnerClassOf(
                        targetClass
                    )
                }
            }
        }
    }
}


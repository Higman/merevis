package com.github.higman.merevis_client_app

import com.github.higman.merevis_client_app.views.VisibilityView
import tornadofx.App
import tornadofx.launch
import java.io.File
import java.net.URI

fun main() {
    launch<MerevisApp>()
}

class MerevisApp : App(VisibilityView::class) {
    companion object {
        @JvmStatic
        val JAR_PARENT_DIR: URI = MerevisApp::class.java.protectionDomain.codeSource.location.toURI().resolve(".")

        /**
         * 設定ファイル用のディレクトリ
         */
        @JvmStatic
        internal val VAR_DIR: URI by lazy {
            val dir = JAR_PARENT_DIR.resolve("var/")
            val varDir = File(dir)
            if (!varDir.exists()) varDir.mkdir()
            dir
        }
    }
}

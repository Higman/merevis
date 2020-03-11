package com.github.higman.merevis_client_app.components

import com.github.higman.merevis_client_app.MerevisApp
import com.github.higman.merevis_client_app.loadRootFXML
import javafx.beans.property.SimpleObjectProperty
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.stage.DirectoryChooser
import java.io.*
import java.nio.file.Path
import java.util.*

class DirectorySelector : HBox() {
    @FXML
    lateinit var selectorButton: Button
    @FXML
    lateinit var pathLabel: Label

    var path: SimpleObjectProperty<Path?> = SimpleObjectProperty(null)
        private set

    lateinit var properties: Properties

    private val propertiesStr = Path.of(MerevisApp.VAR_DIR.resolve("record.properties")).toString()

    init {
        loadRootFXML(hasControllerAttribute = true)
        properties = Properties()
        try {
            val fileInputStream = FileInputStream(propertiesStr)
            val inputStreamReader = InputStreamReader(fileInputStream, Charsets.UTF_8)
            properties.load(inputStreamReader)
        } catch (e: Exception) {
            when (e) {
                is FileNotFoundException -> {
                }
                is NullPointerException, is IOException -> {
                    e.printStackTrace()
                }
                else -> throw e
            }
        }

        properties.getProperty("dir")?.let { path.set(Path.of(it)) }
        pathLabel.text = path.get()?.fileName?.toString() ?: "none"

    }

    @FXML
    private fun onClickButton(event: ActionEvent) {
        val selectDir = DirectoryChooser().showDialog(scene?.window)
        if (selectDir != null) {
            pathLabel.text = selectDir.name
            path.set(selectDir.toPath())
            try {
                properties.setProperty("dir", path.get().toString())
                val fileOutputStream = FileOutputStream(propertiesStr)
                val outputStreamWriter = OutputStreamWriter(fileOutputStream, Charsets.UTF_8)
                properties.store(outputStreamWriter, null)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
    }
}
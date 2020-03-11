package com.github.higman.merevis_client_app


import javafx.fxml.FXMLLoader
import javafx.scene.Node
import tornadofx.*
import java.util.*

/**
 * カスタムコントローラのFXML読込メソッド
 */
internal fun Node.loadRootFXML(location: String? = null, hasControllerAttribute: Boolean = false) {
    val componentType = this.javaClass
    val targetLocation = location ?: componentType.simpleName+".fxml"
    val fxml = requireNotNull(componentType.getResource(targetLocation)) { "FXML not found for $componentType in $targetLocation" }
    val messages = try {
        ResourceBundle.getBundle(javaClass.name, Locale.getDefault(), FXResourceBundleControl)
    } catch (e: MissingResourceException) {
        null
    } catch (e: NullPointerException) {
        null
    }

    val fxmlLoader = FXMLLoader(fxml).apply {
        resources = messages
    }
    fxmlLoader.setRoot(this)
    if (hasControllerAttribute) {
        fxmlLoader.setControllerFactory { this }
    } else {
        fxmlLoader.setController(this)
    }

    fxmlLoader.load<Node>()
}
package com.github.higman.merevis_client_app.visibility_nodes

import com.github.higman.merevis_client_app.loadRootFXML
import javafx.beans.NamedArg
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import java.net.URL
import java.util.*

class VisibilityNode(@NamedArg("name", defaultValue = "name") nodeName: String, val callback: (vn: VisibilityNode) -> Unit) : GridPane(), Initializable {
    /**
     * ノード名文字列のバッキングプロパティ
     */
    private var _name: SimpleStringProperty? = SimpleStringProperty(nodeName)
    @FXML
    internal lateinit var nameLabel: Label

    @FXML
    internal lateinit var nodePane: FlowPane

    @FXML
    internal lateinit var identifyLabel: Label

    var backgroundColor: Color = Color.LIGHTGRAY
        set(value) {
            field = value
            background = Background(BackgroundFill(field, CornerRadii.EMPTY, Insets(1.0)))
            nodePane.background = Background(BackgroundFill(field.desaturate(), CornerRadii.EMPTY, Insets(1.0)))
        }

    var name: String
        get() = name().get()
        set(value) {
            name().set(value)
        }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
    }

    fun name(): SimpleStringProperty {
        if (_name == null) _name = SimpleStringProperty("name")
        return _name ?: throw NullPointerException()
    }

    fun addChild(child : VisibilityNode) {
        nodePane.children.add(child)
    }

    init {
        loadRootFXML(hasControllerAttribute = true)
        nameLabel.text = nodeName
    }

    @FXML
    private fun onMouseClicked(mouse: MouseEvent) {
        callback.invoke(this)
        mouse.consume()
    }
}
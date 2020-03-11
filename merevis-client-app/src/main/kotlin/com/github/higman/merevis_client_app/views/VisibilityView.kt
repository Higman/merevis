package com.github.higman.merevis_client_app.views

import com.github.higman.merevis_client_app.components.DirectorySelector
import com.github.higman.merevis_client_app.visibility_nodes.VisibilityNode
import com.github.higman.merevis_file_info_manager.StringVisibilityNode
import com.github.higman.merevis_file_info_manager.VisibilityInfoManager
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.scene.Camera
import javafx.scene.PerspectiveCamera
import javafx.scene.SubScene
import javafx.scene.control.Alert
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import tornadofx.View
import tornadofx.clear

class VisibilityView : View() {
    override val root: BorderPane by fxml(hasControllerAttribute = true)

    private val sceneForCanvas: SubScene by fxid()
    private val stackPane: StackPane by fxid()
    private val rootDirSelector: DirectorySelector by fxid()
    private val pane: Pane
    private val camera: Camera = PerspectiveCamera()

    private val itemAmdNodeRelationMap = mutableMapOf<String, VisibilityNode>()

    init {
        sceneForCanvas.heightProperty().bind(stackPane.heightProperty())
        sceneForCanvas.widthProperty().bind(stackPane.widthProperty())

        pane = Pane().apply {
            background = Background(BackgroundFill(Color.ALICEBLUE, CornerRadii.EMPTY, Insets(1.0)))
        }
        sceneForCanvas.root = pane
        sceneForCanvas.camera = camera
    }

    var visibilityInfoManager: VisibilityInfoManager? = null
    @FXML
    fun onClickedRunButton() {
        val rootDirectory = rootDirSelector.path.get() ?: run {
            Alert(Alert.AlertType.ERROR).apply {
                contentText = "Error: ディレクトリを指定してください。"
            }.show()
            return
        }
        pane.clear()
        visibilityInfoManager = VisibilityInfoManager(rootDirectory)
        visibilityInfoManager?.analyze()?.also { vm ->
            vm.getStringTree().children.forEach { sv ->
                val value = registerNode(sv, vm)?.apply {
                    layoutX = (pane.children.size + 1) * 10.0
                    layoutY = (pane.children.size + 1) * 10.0
                } ?: throw IllegalArgumentException()
                value.backgroundColor = getColor(sv)
                itemAmdNodeRelationMap.put(sv.item, value)
                pane.children.add(value)
            }
        }
    }

    private fun getColor(sv: StringVisibilityNode): Color {
        return when (sv.identifyStr) {
            "ClassOrInterface" -> Color.LIGHTPINK.brighter()
            "Field" -> Color.DARKSEAGREEN.brighter()
            "Method" -> Color.LIGHTSKYBLUE.brighter()
            "Package" -> Color.KHAKI.brighter()
            else -> Color.LIGHTSLATEGRAY
        }
    }

    private fun registerNode(
        sNode: StringVisibilityNode?,
        visibilityInfoManager: VisibilityInfoManager
    ): VisibilityNode? {
        if (sNode == null) return null
        val vNode = VisibilityNode(sNode.item) {vn ->
            itemAmdNodeRelationMap.forEach { t, u -> u.styleClass.remove("focusItem")}
            vn.styleClass.add("focusItem")
            itemAmdNodeRelationMap.forEach { t, u -> if (!u.styleClass.contains("nonVisible")) u.styleClass.add("nonVisible"); }
            visibilityInfoManager.getVisibleList(sNode.item).forEach {
                itemAmdNodeRelationMap[it]?.styleClass?.remove("nonVisible")
            }
        }.apply {
            backgroundColor = getColor(sNode)
            identifyLabel.text = visibilityInfoManager.getVisibility(this.name).replace("_", " ")
        }
        itemAmdNodeRelationMap.put(sNode.item, vNode)
        sNode.children.forEach { registerNode(it, visibilityInfoManager)?.let { vNode.addChild(it) } }
        return vNode
    }

    private var prevLocal = Point2D(0.0, 0.0)
    private var cameraLayout = Point2D(0.0, 0.0)
    @FXML
    fun onMousePressedOfSubScene(event: MouseEvent) {
        prevLocal = Point2D(event.sceneX, event.sceneY)
        cameraLayout = Point2D(camera.layoutX, camera.layoutY)
    }

    @FXML
    fun onMouseDraggedOfSubScene(event: MouseEvent) {
        val currLocal = Point2D(event.sceneX, event.sceneY)
        camera.layoutX = cameraLayout.x + prevLocal.x - currLocal.x
        camera.layoutY = cameraLayout.y + prevLocal.y - currLocal.y
    }
}
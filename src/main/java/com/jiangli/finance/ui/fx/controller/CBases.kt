package com.jiangli.finance.ui.fx.controller

import com.jiangli.finance.ui.fx.mainStage
import javafx.fxml.FXMLLoader
import javafx.fxml.JavaFXBuilderFactory
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File
import java.io.InputStream
import javax.swing.filechooser.FileSystemView


class CBases

open class BaseController:AnchorPane() {
    lateinit var page:Pane
    val errorCls = "validate-error"

    open fun processPage(page:Pane) {
        this.page = page
    }

    fun validateNotEmpty(textField: TextField) {
        textField.styleClass.remove(errorCls)
        if (isEmpty(textField.text)) {
            textField.styleClass.add(errorCls)
            textField.promptText = "不能为空"
//            textField.addEventFilter(Focus)
            //            inputPath.isFocused = true
            throw Exception()
        }
    }

}

fun isEmpty(s: String?): Boolean {
    return s==null || s.trim().isEmpty()
}


fun fileChoose(s: String?="选择文件"): File? {
    val fileChooser = FileChooser()
    fileChooser.setTitle(s);
    fileChooser.setInitialDirectory(
//         File(System.getProperty("user.home"))
          FileSystemView.getFileSystemView() .getHomeDirectory()
    );
    fileChooser.extensionFilters.addAll(
//            FileChooser.ExtensionFilter("All Images", "*.*"),
//            FileChooser.ExtensionFilter("JPG", "*.jpg"),
//            FileChooser.ExtensionFilter("PNG", "*.png")

            FileChooser.ExtensionFilter("xlsx", "*.xlsx")
    )

    val f = fileChooser.showOpenDialog(mainStage)
    return f
}
fun dirChoose(s: String?="选择文件夹"): File? {
    val fileChooser = DirectoryChooser()
    fileChooser.setTitle(s);
    fileChooser.setInitialDirectory(
//            File(System.getProperty("user.home"))
            FileSystemView.getFileSystemView() .getHomeDirectory()
    );
    val f = fileChooser.showDialog(mainStage)
    return f
}

fun fileToTextField(field: TextField) {
    val f = fileChoose()
    f?.let {
        field.text = it.absolutePath
    }
}
fun dirToTextField(field: TextField) {
    val f = dirChoose()
    f?.let {
        field.text = it.absolutePath
    }
}

fun src(fxml: String): InputStream {
    return CBases::class.java.getResourceAsStream(fxml)!!
}

fun createIntSpinner(defaultVal:Int=1): Spinner<Int> {
    val svf = SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99,defaultVal)
    val sp = Spinner<Int>()
    sp.setValueFactory(svf)
    //    sp.getStyleClass().add(styles[i])
    sp.setPrefWidth(80.0)
    sp.isEditable = true

    //        page.children.add(sp)
    return sp
}

@Throws(Exception::class)
 fun replaceSceneContent(fxml: String,stage: Stage): Node {
//    val stage = mainStage

    val loader = FXMLLoader()
    //        InputStream in = Main.class.getResourceAsStream(fxml);
    val `in` = src(fxml)
    loader.builderFactory = JavaFXBuilderFactory()
    loader.location = CBases::class.java.getResource(fxml)
    val page: AnchorPane
    try {
        page = loader.load<Any>(`in`) as AnchorPane
    } finally {
        `in`.close()
    }

    // Store the stage width and height in case the user has resized the window
    var stageWidth = stage.getWidth()
    if (!java.lang.Double.isNaN(stageWidth)) {
        stageWidth -= stage.getWidth() - stage.getScene().getWidth()
    }

    var stageHeight = stage.getHeight()
    if (!java.lang.Double.isNaN(stageHeight)) {
        stageHeight -= stage.getHeight() - stage.getScene().getHeight()
    }

    val scene = Scene(page)
    if (!java.lang.Double.isNaN(stageWidth)) {
        page.prefWidth = stageWidth
    }
    if (!java.lang.Double.isNaN(stageHeight)) {
        page.prefHeight = stageHeight
    }


    val node = loader.getController<Any>() as BaseController
    node.page=page
    node.processPage(page)

    stage.setScene(scene)
    stage.sizeToScene()


    return loader.getController<Any>() as Node
//    return page
}
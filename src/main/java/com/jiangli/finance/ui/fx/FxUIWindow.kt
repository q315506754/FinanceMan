package com.jiangli.finance.ui.fx

import com.jiangli.finance.ui.fx.controller.replaceSceneContent
import com.jiangli.finance.ui.fx.controller.src
import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage


/**
 *
 *
 * @author Jiangli
 * @date 2018/3/11 12:41
 */
var mainStage: Stage= Stage()
class FxUIWindow : Application(){

    override fun start(primaryStage: Stage?) {
        mainStage = primaryStage!!
        mainStage.minWidth = 200.0
        mainStage.minHeight = 200.0
        mainStage.title = "财务小工具"
        mainStage.getIcons().add(Image(src("logo.jpg")))

        //default action
        replaceSceneContent("SplitExcelController.fxml")


        primaryStage.show()
    }
}
package com.jiangli.finance.ui.fx.controller

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.Spinner
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane



/**
 *
 *
 * @author Jiangli
 * @date 2018/3/11 14:53
 */
class SplitExcelController : BaseController() {
    @FXML
    lateinit internal var inputPath: TextField

    @FXML
    lateinit internal var outputPath: TextField

    @FXML
    lateinit internal var inputGridPane: GridPane

    @FXML
    lateinit internal var msgArea: HBox

    @FXML
    lateinit internal var splitBtn: Button

    lateinit internal var splitColumnNum: Spinner<Int>
    lateinit internal var startRowNum: Spinner<Int>

    val errorCls = "validate-error"

    private fun validate(): Boolean {

        try {
            validateNotEmpty(inputPath)
            validateNotEmpty(outputPath)
        } catch (e: Exception) {
            return false
        }

        return true
    }

    private fun validateNotEmpty(textField: TextField) {
        textField.styleClass.remove(errorCls)

        if (isEmpty(textField.text)) {
            textField.styleClass.add(errorCls)
            textField.promptText = "不能为空"
//            textField.addEventFilter(Focus)
    //            inputPath.isFocused = true
            throw Exception()
        }
    }

    fun splitExcel(actionEvent: ActionEvent) {
        println(actionEvent)

        if(!validate()){
            return
        }


        val p1 = ProgressIndicator()
        p1.setPrefSize(50.0, 50.0)

        msgArea.children.clear()
        msgArea.children.add(p1)
//        msgArea.children.add(Label("分割中..."))
        splitBtn.isDisable = true



    }

    fun chooseInput(actionEvent: ActionEvent) {
        fileToTextField(inputPath)
    }

    fun chooseOutput(actionEvent: ActionEvent) {
        dirToTextField(outputPath)
    }

    override fun processPage(page: Pane){
        /* Integer spinners */
        this.splitColumnNum = createIntSpinner(1)
        this.startRowNum = createIntSpinner(2)

        inputGridPane.add(this.splitColumnNum,1,1)
        inputGridPane.add(this.startRowNum,1,2)
//        inputGridPane.add(sp,1,2)
//        val tabPane = page.children[0] as TabPane
//        val tab = tabPane.tabs[0]
//        val archorPane = tab.content as Pane
//        println(tab.content)
    }




}
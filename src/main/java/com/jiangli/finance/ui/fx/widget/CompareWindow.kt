package com.jiangli.finance.ui.fx.widget

import com.jiangli.finance.excel.business.Cmp2DiffRow
import com.jiangli.finance.excel.business.Cmp2DiffRs
import com.jiangli.finance.excel.business.convertCmpRsToTable
import com.jiangli.finance.ui.fx.controller.BaseController
import com.jiangli.finance.ui.fx.controller.replaceSceneContent
import com.jiangli.finance.ui.fx.controller.src
import javafx.application.Application
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage


/**
 *
 *
 * @author Jiangli
 * @date 2018/3/11 12:41
 */
var compareRsStage: Stage= Stage()

fun showCompareRs(rs: List<Cmp2DiffRs>, function: () -> Unit) {
//    CompareWindow()

    compareRsStage = Stage()
    CompareWindow(rs).start(compareRsStage)

    compareRsStage.setOnCloseRequest { event ->
        function()
    }
}

class CompareWindow(val rs: List<Cmp2DiffRs>) : Application(){

    override fun start(primaryStage: Stage?) {
        compareRsStage = primaryStage!!
        compareRsStage.minWidth = 200.0
        compareRsStage.minHeight = 200.0
        compareRsStage.title = "比对结果"
        compareRsStage.getIcons().add(Image(src("logo.jpg")))

        //default action
        val node = replaceSceneContent("CompareController.fxml", compareRsStage) as CompareController

        compareRsStage.show()

        val cmp2DiffRs = rs[0]
        val tableList = convertCmpRsToTable(cmp2DiffRs)

        node.col1.text=cmp2DiffRs.titleLeft
        node.col2.text=cmp2DiffRs.titleRight
//        node.colCommon.text="${cmp2DiffRs.titleLeft} 和 ${cmp2DiffRs.titleRight} 公共值"

        node.tableView.items = FXCollections.observableArrayList(tableList)

//        node.col1.setCellFactory {
//            param->object : TableCell<Cmp2DiffRow, String>() {
//                override fun updateItem(item: String?, empty: Boolean) {
//                    super.updateItem(item, empty)
//                }
//            }
//        }

        node.colIdx.setCellValueFactory(PropertyValueFactory<Cmp2DiffRow, String>("idx"))
//        node.colIdx.setCellFactory(TextFieldTableCell.forTableColumn())
//        node.colIdx.setCellFactory({ param -> CheckBoxTableCell() })
        node.colIdx.setCellFactory{ param ->
            object : TableCell<Cmp2DiffRow, String>() {

               override fun updateItem(item: String?, empty: Boolean) {
//                    super.updateItem(item, empty)

                    setTextFill(Color.RED);
                    setText(item);
                }
            }
        }

        node.col1.setCellValueFactory(PropertyValueFactory<Cmp2DiffRow, String>("colLeft"))
        node.col1.setCellFactory(TextFieldTableCell.forTableColumn())

        node.col2.setCellValueFactory(PropertyValueFactory<Cmp2DiffRow, String>("colRight"))
        node.col2.setCellFactory(TextFieldTableCell.forTableColumn())

        node.colCommon.setCellValueFactory(PropertyValueFactory<Cmp2DiffRow, String>("colCommon"))
        node.colCommon.setCellFactory(TextFieldTableCell.forTableColumn())

//        node.colIdx.cellValueFactory = Callback<TableColumn.CellDataFeatures<Cmp2DiffRow, String>, ObservableValue<String>> { arg0 ->
//            SimpleStringProperty(arg0.value.idx)
//        }
//        node.col1.cellValueFactory = Callback<TableColumn.CellDataFeatures<Cmp2DiffRow, String>, ObservableValue<String>> { arg0 ->
//            SimpleStringProperty(arg0.value.colLeft)
//        }
//        node.col2.cellValueFactory = Callback<TableColumn.CellDataFeatures<Cmp2DiffRow, String>, ObservableValue<String>> { arg0 ->
//            SimpleStringProperty(arg0.value.colRight)
//        }
//        node.colCommon.cellValueFactory = Callback<TableColumn.CellDataFeatures<Cmp2DiffRow, String>, ObservableValue<String>> { arg0 ->
//            // return new
//            // SimpleStringProperty(arg0.getValue(),"sd",arg0.getValue().getFirstName());
//            // //bean, bean的名称，值
//            SimpleStringProperty(arg0.value.colCommon)
//            // 这样你能够不建立值与对象的映射关系。
//        }
    }
}

class CompareController : BaseController() {
    @FXML
    lateinit internal var tableView: TableView<Cmp2DiffRow>
    @FXML
    lateinit internal var colIdx: TableColumn<Cmp2DiffRow,String>
    @FXML
    lateinit internal var col1: TableColumn<Cmp2DiffRow,String>
    @FXML
    lateinit internal var col2: TableColumn<Cmp2DiffRow,String>
    @FXML
    lateinit internal var colCommon: TableColumn<Cmp2DiffRow,String>


    //initial
    override fun processPage(page: Pane){

    }
}
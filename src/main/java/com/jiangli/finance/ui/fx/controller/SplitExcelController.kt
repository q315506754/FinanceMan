package com.jiangli.finance.ui.fx.controller

import com.jiangli.finance.excel.ExcelUtil
import com.jiangli.finance.excel.business.cmpExcel
import com.jiangli.finance.ui.fx.widget.showCompareRs
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox


/**
 *
 *
 * @author Jiangli
 * @date 2018/3/11 14:53
 */
class SplitExcelController : BaseController() {
    @FXML
    lateinit internal var tabPane: TabPane


    /* SPLIT_MODULE */
    @FXML
    lateinit internal var split_inputPath: TextField
    @FXML
    lateinit internal var split_outputPath: TextField
    @FXML
    lateinit internal var split_inputGridPane: GridPane
    @FXML
    lateinit internal var split_msgArea: VBox
    @FXML
    lateinit internal var split_btn: Button
    lateinit internal var split_columnNum: Spinner<Int>
    lateinit internal var split_startRowNum: Spinner<Int>

    private fun validateSplit(): Boolean {
        try {
            validateNotEmpty(split_inputPath)
            validateNotEmpty(split_outputPath)
        } catch (e: Exception) {
            return false
        }
        return true
    }


    fun splitExcel(actionEvent: ActionEvent) {
        println(actionEvent)

        if(!validateSplit()){
            return
        }

        val p1 = ProgressIndicator()
        p1.setPrefSize(50.0, 50.0)

        split_msgArea.children.clear()
        split_msgArea.children.add(p1)
//        msgArea.children.add(Label("分割中..."))
        split_btn.isDisable = true

    }

    fun chooseInputSplit(actionEvent: ActionEvent) {
        fileToTextField(split_inputPath)
    }
    fun chooseOutputSplit(actionEvent: ActionEvent) {
        dirToTextField(split_outputPath)
    }

    /* SPLIT_MODULE */

    /* COLCMP_MODULE */
    @FXML
    lateinit internal var colcmp_inputPath: TextField
    @FXML
    lateinit internal var colcmp_inputGridPane: GridPane
    @FXML
    lateinit internal var colcmp_msgArea: VBox
    @FXML
    lateinit internal var colcmp_sheetChooseList: ComboBox<CellValueBean>
    @FXML
    lateinit internal var colcmp_chooseColOne: ComboBox<CellValueBean>
    @FXML
    lateinit internal var colcmp_chooseColAnother: ComboBox<CellValueBean>
    @FXML
    lateinit internal var colcmp_btn: Button
    lateinit internal var colcmp_columnNum: Spinner<Int>
    lateinit internal var colcmp_startRowNum: Spinner<Int>

     fun validateColCmp(): Boolean {
        try {
            validateNotEmpty(colcmp_inputPath)
            validateNotEmpty(split_outputPath)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    fun chooseInputColCmp(actionEvent: ActionEvent) {
        fileToTextField(colcmp_inputPath)

        ExcelUtil.process(colcmp_inputPath.text){
            file, workbook, sheet ->

            colcmp_sheetChooseList.items.clear()

            val numberOfSheets = workbook.numberOfSheets
            (0..(numberOfSheets-1)).forEach {
                val element = workbook.getSheetName(it)
//                colcmp_sheetChooseList.items.add(element)
                colcmp_sheetChooseList.items.add(CellValueBean(it,"$element","页"))

            }

            colcmp_sheetChooseList.selectionModel.select(0)
        }

    }

    fun colCmpExcel(actionEvent: ActionEvent) {
        println(actionEvent)

//        if(!validateColCmp()){
//            return
//        }

        val p1 = ProgressIndicator()
        p1.setPrefSize(50.0, 50.0)

        colcmp_msgArea.children.clear()
        colcmp_msgArea.children.add(p1)
//        msgArea.children.add(Label("分割中..."))
        colcmp_btn.isDisable = true


        val src = colcmp_inputPath.text
        val sheet = colcmp_sheetChooseList.selectionModel.selectedItem.idx
        val col1 = colcmp_chooseColOne.selectionModel.selectedItem.idx
        val col2 = colcmp_chooseColAnother.selectionModel.selectedItem.idx
//
        val rs = cmpExcel(src, sheet, col1, col2,colcmp_chooseColOne.selectionModel.selectedItem.toString(),colcmp_chooseColAnother.selectionModel.selectedItem.toString())
//        val rs = mutableListOf<Cmp2DiffRs>()

        showCompareRs(rs){
            colcmp_msgArea.children.clear()
            colcmp_btn.isDisable = false
        }

    }
    /* COLCMP_MODULE */


    //initial
    override fun processPage(page: Pane){
        //默认第3个
        tabPane.selectionModel.select(2)

        //选择sheet
        colcmp_sheetChooseList.setOnAction {
            val src = colcmp_inputPath.text
            val sheet = colcmp_sheetChooseList.selectionModel.selectedItem.idx

            colcmp_chooseColOne.items.clear()
            colcmp_chooseColAnother.items.clear()

            ExcelUtil.process(src,sheet){
                file, workbook, sheet1 ->

                val row = sheet1.getRow(0)
                var lastColIdx = ExcelUtil.maxSheetColIdx(sheet1)
                (0..lastColIdx).forEach {
                    val cellIdx = it
                    val cell = row?.getCell(it)
                    val cellValue = ExcelUtil.getCellValue(cell)

                    colcmp_chooseColOne.items.add(CellValueBean(cellIdx,"$cellValue","列"))
                    colcmp_chooseColAnother.items.add(CellValueBean(cellIdx,"$cellValue","列"))
                }
            }

            colcmp_chooseColOne.selectionModel.select(0)
            colcmp_chooseColAnother.selectionModel.select(0)

        }

//        colcmp_sheetChooseList.cellFactory = Callback<ListView<CellValueBean>?, ListCell<CellValueBean>?> {
//            object : ListCell<CellValueBean>() {
//                    init {
//                    }
//                override fun updateItem(item: CellValueBean?, empty: Boolean) {
//                    super.updateItem(item, empty)
//                    text = item?.title
//                    println(item)
//                }
//
//            }
//        }



        /* Integer spinners */
        this.split_columnNum = createIntSpinner(1)
        this.split_startRowNum = createIntSpinner(2)

        split_inputGridPane.add(this.split_columnNum,1,1)
        split_inputGridPane.add(this.split_startRowNum,1,2)
    }




}
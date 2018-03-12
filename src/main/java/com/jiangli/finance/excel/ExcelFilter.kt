package com.jiangli.finance.excel

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 *
 *
 * @author Jiangli
 * @date 2018/3/12 15:12
 */
data class ExcelFileFilter (val col:Int, val name:String) : ExcelFileMeta() {
    constructor(file: File, sheetIdx: Int, rowRange: IntRange, colRange: IntRange, col:Int,  name:String) : this(col, name) {
        initial(file, sheetIdx, rowRange, colRange)
    }

    override fun toString(): String {
        return "ExcelFileFilter(col=$col, name='$name') ${super.toString()}"
    }

}

object ExcelFileFilterProcessor{
    fun process(filterList: List<ExcelFileFilter>) {
        filterList.forEach {
            process(it)
        }
    }

    fun process(it: ExcelFileFilter) {
        println(it)

        val workbook = XSSFWorkbook(FileInputStream(it.file))
        val sheet1 = workbook.getSheetAt(it.sheetIdx)
        val filter = it

        for (i in it.rowRange) {
            val row = sheet1.getRow(i)

            val cellValue = ExcelUtil.getCellValue(row?.getCell(it.col) ?: null)
            if (it.name != cellValue) {
//                println("delete row $cellValue")

                row?.let {
                    val nRow = it
                    filter.colRange.forEach {
                        val cell = nRow.getCell(it)
                        if (cell != null) {
                            nRow.removeCell(cell)
                        }
                    }
                }
            }
        }


        //save
        workbook.write(FileOutputStream(it.file))

    }
}
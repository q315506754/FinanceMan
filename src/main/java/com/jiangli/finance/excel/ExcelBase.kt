package com.jiangli.finance.excel

import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.util.NumberToTextConverter
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.*
import java.text.SimpleDateFormat

class Car

fun buy(vararg cars: Car) {
    drive(cars) //compile error
}

fun drive(cars: Array<out Car>) {
    //...
}

open  class ExcelFileMeta {
     open lateinit var  file: File
     open var  sheetIdx:Int = 0
    open lateinit var  rowRange:IntRange
     open lateinit var  colRange:IntRange

    constructor()

    constructor(file: File, sheetIdx: Int, rowRange: IntRange, colRange: IntRange) {
        this.file = file
        this.sheetIdx = sheetIdx
        this.rowRange = rowRange
        this.colRange = colRange
    }

    fun initial(file: File, sheetIdx: Int, rowRange: IntRange, colRange: IntRange) {
        this.file = file
        this.sheetIdx = sheetIdx
        this.rowRange = rowRange
        this.colRange = colRange
    }

    override fun toString(): String {
        return "ExcelFileMeta(file=$file, sheetIdx=$sheetIdx, rowRange=$rowRange, colRange=$colRange)"
    }


}

/**
 *
 *
 * @author Jiangli
 * @date 2018/3/12 10:02
 */
 enum class ExCode(str: String) {
    NO_SUCH_FILE("找不到文件")
    ,NOT_DIRECTORY("不是文件夹")
    ,NUM_ROW_OVERFLOW("所选行数%d超过了最大限制%d")
    ,NUM_COL_OVERFLOW("所选列数%d超过了最大限制%d")
    ;

    val code:String = str
}

data class InterruptException(val code:ExCode) :Exception() {
   var args:MutableList<Any> = mutableListOf()

    constructor( code:ExCode,  vararg args:Any) :this(code){
        args.forEach {
            this.args.add(it)
        }
    }

    fun getMsg():String {
        //spread
        return code.code.format(*(args.toTypedArray()))
    }

    override fun toString(): String {
        return "InterruptException(code=$code,msg=${getMsg()})"
    }


}

object FUtil {
    private val EOF = -1
    private val DEFAULT_BUFFER_SIZE = 1024 * 4

    @Throws(IOException::class)
    fun copyLarge(input: InputStream, output: OutputStream): Long {
        return copyLarge(input, output, ByteArray(kotlin.io.DEFAULT_BUFFER_SIZE))
    }

    @Throws(IOException::class)
    fun copyLarge(input: InputStream, output: OutputStream, buffer: ByteArray): Long {
        var count: Long = 0
        var n = input.read(buffer)
        while (EOF != n) {
            output.write(buffer, 0, n)
            count += n.toLong()

            n = input.read(buffer)
        }
        return count
    }
    fun copy(from: File,to: File): File {
        val inputStream = FileInputStream(from)
        val out = FileOutputStream(to)
        copyLarge(inputStream, out)
        inputStream.close()
        out.close()
        return to
    }

    fun suffix(str: String): String {
        return str.substring(str.lastIndexOf(".")+1)
    }
    fun prefix(str: String): String {
        return str.substring(0,str.lastIndexOf("."))
    }

    fun file(str: String): File {
        val file = File(str)
        if (!file.exists()) {
            throw InterruptException(ExCode.NO_SUCH_FILE)
        }
        return file
    }
    fun files(str: String): List<File> {
        val file = File(str)
        if (!file.exists()) {
            throw InterruptException(ExCode.NO_SUCH_FILE)
        }
        val mutableListOf = mutableListOf<File>()
        if (!file.isDirectory()) {
            mutableListOf.add(file)
        }else {
            mutableListOf.addAll(files(file.absolutePath))
        }
        return mutableListOf
    }
    fun dir(str: String): File {
        val file = File(str)
        if (!file.exists()) {
            file.mkdirs()
        } else {
            if (!file.isDirectory) {
                throw InterruptException(ExCode.NO_SUCH_FILE)
            }
        }

        return file
    }
}

object ExcelUtil {
    val MAX_COL = 255

    fun process(inputSrc:String, sheetIdx:Int=0, fc:(file: File, workbook: XSSFWorkbook, sheet:XSSFSheet)->Unit) {
        val files = FUtil.files(inputSrc)

        files.filter { it.name.endsWith(".xlsx") }.forEach {
            val fileInputStream = FileInputStream(it)
            val workbook = XSSFWorkbook(fileInputStream)
            val sheet1 = workbook.getSheetAt(sheetIdx)
            fc(it,workbook,sheet1)

            fileInputStream.close()
        }

    }

    fun processRow(inputSrc:String, sheetIdx:Int=0, startRow:Int=0, fc:(file: File, workbook: XSSFWorkbook, sheet:XSSFSheet, lastRowIdx:Int, lastColIdx:Int, rowIdx:Int, row:XSSFRow?)->Unit) {
        process(inputSrc, sheetIdx){
            file, workbook, sheet1 ->

            var lastColNum = ExcelUtil.maxSheetColIdx(sheet1)
            var lastRowNum = ExcelUtil.maxSheetRowIdx(sheet1,lastColNum)

            if (startRow > lastRowNum) {
                throw InterruptException(ExCode.NUM_ROW_OVERFLOW,startRow+1,lastRowNum+1)
            }

            (startRow..lastRowNum).forEach {
                val row = sheet1.getRow(it)
                fc(file, workbook, sheet1,lastRowNum,lastColNum,it,row)
            }
        }
    }

    fun processRowCell(inputSrc:String, sheetIdx:Int=0, startRow:Int=0, fc:(file: File, workbook: XSSFWorkbook, sheet:XSSFSheet, lastRowIdx:Int, lastColIdx:Int, rowIdx:Int, row:XSSFRow?, cellIdx:Int, cell:XSSFCell?, cellValue:String?)->Unit) {
        processRow(inputSrc, sheetIdx,startRow){
            file, workbook, sheet, lastRowIdx, lastColIdx, rowIdx, row ->
            if (row!=null) {
                (0..lastColIdx).forEach {
                    val cell = row.getCell(it)
                    val cellValue = getCellValue(cell)

                    fc(file, workbook, sheet, lastRowIdx, lastColIdx, rowIdx, row,it,cell,cellValue)
                }
            }
        }
    }

    fun maxSheetRowIdx(sheet: XSSFSheet?, colMAX: Int = MAX_COL): Int {
        if (sheet == null) {
            return -1
        }

        var lastRowNum = sheet.lastRowNum

        return (lastRowNum downTo 0 step 1).firstOrNull { !isEmptyRow(sheet.getRow(it), colMAX) }
                ?: 0
    }

    fun maxSheetColIdx(sheet: XSSFSheet?, colMAX: Int = MAX_COL): Int {
        if (sheet == null) {
            return -1
        }

        var lastRowNum = sheet.lastRowNum

        var maxColIdx = 0
        for (i in 0..lastRowNum) {
            val row = sheet.getRow(i)

            for (j in 0..colMAX) {
                val cell = row?.getCell(j)

                if (isCellNotEmpty(cell) && j > maxColIdx) {
                    maxColIdx = j
                }
            }
        }

        return maxColIdx
    }

    fun isEmptyRow(row: XSSFRow?, colMAX: Int = MAX_COL): Boolean {
        if (row == null) {
            return true
        }
        return (0..colMAX)
                .map { row.getCell(it) }
                .none { isCellNotEmpty(it) }
    }

    fun isCellNotEmpty(cell: XSSFCell?): Boolean {
        val cellValue = getCellValue(cell)
        return cellValue?.isNotEmpty() ?: false
    }

    fun getCellValue(cell: XSSFCell?): String? {
        if (cell == null) {
            return null
        }
        val ret: String?
        when (cell.cellType) {
            XSSFCell.CELL_TYPE_BLANK -> ret = ""
            XSSFCell.CELL_TYPE_BOOLEAN -> ret = cell.booleanCellValue.toString()
            XSSFCell.CELL_TYPE_ERROR -> ret = null
            XSSFCell.CELL_TYPE_FORMULA -> {
                val wb = cell.sheet.workbook
                val crateHelper = wb.creationHelper
                val evaluator = crateHelper.createFormulaEvaluator()
                ret = getCellValue(evaluator.evaluateInCell(cell) as XSSFCell)
            }
            XSSFCell.CELL_TYPE_NUMERIC -> if (DateUtil.isCellDateFormatted(cell)) {
                val theDate = cell.dateCellValue
                val sdf = SimpleDateFormat("HH:mm:ss")
                ret = sdf.format(theDate)
            } else {
                ret = NumberToTextConverter.toText(cell.numericCellValue)
            }
            XSSFCell.CELL_TYPE_STRING -> ret = cell.richStringCellValue.string
            else -> ret = null
        }
        return ret?.trim()
    }


    //一个行范围，列序号sCol中不同的值
    fun colValueSet(sheet1: XSSFSheet, rowRange: IntRange, sCol: Int): MutableSet<String> {
        val splitSet = mutableSetOf<String>()
        (rowRange)
                .map { sheet1.getRow(it) }
                .forEach { row ->
                    ExcelUtil.getCellValue(row?.getCell(sCol))?.let {
                        splitSet.add(it)
                    }
                }
        return splitSet
    }

}
fun main(args: Array<String>) {
//    println(lastRowNum)
//    println(lastColNum)
//    println(sRow)

//    println(InterruptException(ExCode.NO_SUCH_FILE,"啊啊").getMsg())
//    println(listOf(1, 2, 3, (arrayOf(4, 5, 6))))
//    println(listOf(1, 2, 3, *(arrayOf(4, 5, 6))))
    ExcelUtil.process("C:\\Users\\DELL-13\\Desktop\\课程购买.xlsx"){
        it, workbook, sheet ->  println(sheet)
    }

    ExcelUtil.processRowCell("C:\\Users\\DELL-13\\Desktop\\课程购买.xlsx",0,1){
        file, workbook, sheet, lastRowIdx, lastColIdx, rowIdx, row, cellIdx, cell, cellValue ->
        println("$rowIdx x $cellIdx ,$cellValue")
    }
}


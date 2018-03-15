package com.jiangli.finance.excel.business

import com.jiangli.finance.excel.*
import java.time.LocalDateTime

/**
 *
 *
 * @author Jiangli
 * @date 2018/3/12 16:04
 */

fun main(args: Array<String>) {
    val inputSrc = "C:\\Users\\DELL-13\\Desktop\\课程购买.xlsx"
    val outputPath = "C:\\Users\\DELL-13\\Desktop\\outpath1"
    var sCol = 3
    var sRow = 2

    val sheetIdx = 0
    val templateSuffix = "-${ LocalDateTime.now().monthValue}月"

    ExcelUtil.process(inputSrc,sheetIdx){
        src,workbook,sheet1 ->
        val namePrefix = FUtil.prefix(src.name)
        val nameSuffix = FUtil.suffix(src.name)

        var lastColNum = ExcelUtil.maxSheetColIdx(sheet1)
        var lastRowNum = ExcelUtil.maxSheetRowIdx(sheet1,lastColNum)
        sCol-=1
        sRow-=1

        if (sCol > lastColNum) {
            throw InterruptException(ExCode.NUM_COL_OVERFLOW,sCol+1,lastColNum+1)
        }
        if (sRow > lastRowNum) {
            throw InterruptException(ExCode.NUM_ROW_OVERFLOW,sRow+1,lastRowNum+1)
        }

        //meta: []
        val splitSet = ExcelUtil.colValueSet(sheet1, sRow..lastRowNum, sCol)

        //generate: []
        println(splitSet)

        //generate: []
        println(splitSet)

        splitSet.forEach {
            val newFileName = "$it$templateSuffix.$nameSuffix"

            //copy
            val newFile = ExcelCopier.process(ExcelFileCopier(src,sheetIdx,sRow..lastRowNum,0..lastColNum,outputPath,newFileName))

            //filter
            ExcelFileFilterProcessor.process(ExcelFileFilter(newFile,sheetIdx,sRow..lastRowNum,0..lastColNum,sCol,it))
        }
    }

}


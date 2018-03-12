package com.jiangli.finance.excel

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 *
 * @author Jiangli
 * @date 2018/3/12 15:52
 */
data class ExcelFileCopier (val outputPath:String, val newFileName:String) : ExcelFileMeta() {
    constructor( srcFile:File,sheetIdx: Int, rowRange: IntRange, colRange: IntRange, outputPath:String, name:String) : this(outputPath, name) {
        initial(srcFile, sheetIdx, rowRange, colRange)
    }

    override fun toString(): String {
        return "ExcelFileCopier(col=$outputPath, name='$newFileName') ${super.toString()}"
    }
}


object ExcelCopier {
    fun process(copier: ExcelFileCopier): File {
        println(copier)

        val separator = File.separatorChar
        var dateStr = SimpleDateFormat("yyyy_MM_dd HH_mm_ss").format(Date())
        val bakDir = "${copier.outputPath}${separator}bak_$dateStr"


        val newFile = File(copier.outputPath, copier.newFileName)

        //exists?
        if (newFile.exists()) {
            val bakDirFile = FUtil.dir(bakDir)
            FUtil.copy(newFile,File(bakDirFile,copier.newFileName))
        }

        FUtil.copy(copier.file,newFile)

        return newFile
    }

}
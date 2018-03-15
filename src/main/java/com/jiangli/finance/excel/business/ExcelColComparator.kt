package com.jiangli.finance.excel.business

import com.jiangli.finance.excel.ExcelUtil

/**
 *
 *
 * @author Jiangli
 * @date 2018/3/12 16:04
 */

data class Cmp2DiffRs(val titleLeft:String,val listLeft:Set<String>, val titleRight:String,val listRight:Set<String>, val listCommon:Set<String>)

fun cmpSetList(titles: List<String>,list:List<Set<String>>):List<Cmp2DiffRs> {
    val ret = arrayListOf<Cmp2DiffRs>()
    if (list.size>2) {
        (0..list.lastIndex).forEach {
            i->
            (i + 1..list.lastIndex).forEach{
                j->ret.addAll(cmpSetList(arrayListOf(titles[i],titles[j]),arrayListOf(list[i],list[j])))
            }

        }
    } else {
        val set1 = list[0]
        val title1 = titles[0]
        val set2 = list[1]
        val title2 = titles[1]

        val exclu1 = mutableSetOf<String>()
        exclu1.addAll(set1)
        exclu1.removeAll(set2)

        val exclu2 = mutableSetOf<String>()
        exclu2.addAll(set2)
        exclu2.removeAll(set1)


        val com = mutableSetOf<String>()
        com.addAll(set1)
        com.retainAll(set2)


        val one = Cmp2DiffRs(title1,exclu1,title2,exclu2,com)
        ret.add(one)
    }
    return ret
}

fun main(args: Array<String>) {
    val inputSrc = "C:\\Users\\DELL-13\\Desktop\\课程购买 - 副本.xlsx"
    var sRow = 2
    val sheetIdx = 0


    var cmpCol = arrayListOf(3,5)
    var cmpColVal = arrayListOf<MutableSet<String>>()
    var cmpColTitle = arrayListOf<String>()
    cmpCol.forEach {
        cmpColVal.add(mutableSetOf())
        cmpColTitle.add("第${it}列")
    }

    ExcelUtil.processRowCell(inputSrc,sheetIdx,sRow-1){
        file, workbook, sheet, lastRowIdx, lastColIdx, rowIdx, row, cellIdx, cell, cellValue ->
        println("$rowIdx x $cellIdx ,$cellValue")

        cmpCol.forEachIndexed { idx, v ->
            if (v-1 == cellIdx) {
                if (cellValue != null) {
                    cmpColVal[idx].add(cellValue)
                }
            }
        }
    }

//    println(cmpColVal)

    val cmpSetList = cmpSetList(cmpColTitle,cmpColVal)
    println(cmpSetList)

}
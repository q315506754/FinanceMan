package com.jiangli.finance.ui.fx.controller

/**
 *
 *
 * @author Jiangli
 * @date 2018/3/16 9:12
 */

data class CellValueBean(val idx:Int,val title:String,val unit:String) {
    override fun toString(): String {
        return "第${idx+1}${unit} "+title
    }
}

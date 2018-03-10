package com.jiangli.finance.ui

import javax.swing.JFrame

/**
 *
 *
 * @author Jiangli
 * @date 2018/3/10 21:09
 */

class UIWindow : JFrame {
    constructor(title: String?):super(title)  {
        SwingUtil.setCommonFrameStyle(this)

        SwingUtil.setFrameMiddle(this)
    }

}

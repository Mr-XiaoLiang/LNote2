package com.lollipop.lnote.dialog

/**
 * @author lollipop
 * @date 2020/3/7 13:18
 * 自适应的接口，用于适应窗口缩进
 */
interface AdaptiveInset {

    /**
     * 缩进的尺寸
     * 这个尺寸是相对整个窗口而言，
     * 如果不关心某个窗口，那么就不应该使用相应的数值
     */
    fun onInsetChange(left: Int, top: Int, right: Int, bottom: Int)

}
package com.lollipop.lnote.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * @author lollipop
 * @date 2020/3/9 00:18
 * 拼盘的Layout
 */
class PlatterLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int):
    ViewGroup(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context): this(context, null)

    var orientation = Orientation.HORIZONTAL

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        TODO("Not yet implemented")
    }

    enum class Orientation {
        HORIZONTAL,
        VERTICAL
    }

}
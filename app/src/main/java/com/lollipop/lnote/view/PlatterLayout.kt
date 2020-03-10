package com.lollipop.lnote.view

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Size
import android.view.ViewGroup
import android.widget.ImageView

/**
 * @author lollipop
 * @date 2020/3/9 00:18
 * 拼盘的Layout
 * 由于列表是纵向的，因此只要做横向的排版，
 * 然后多个排列下来，就可以满足绝大多数的需求
 *
 * 排版原则在于，所有的Child在同一排或同一列，
 * 但是会保持每个Child的比例。
 * 例如横向排列，将首先计算每个Child的长宽比例，然后根据比例，
 * 等比缩放后得到高度一致情况下的宽度比例，通过宽度比例与Layout的宽度，
 * 得到每个Child的宽度，再以此得到每个Child的高度，并且确定Layout的高度。
 * 最后完成排版。
 */
class PlatterLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int):
    ViewGroup(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context): this(context, null)

    var orientation = Orientation.HORIZONTAL

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        when (orientation) {
            Orientation.HORIZONTAL -> {
                measureHorizontal(widthMeasureSpec, heightMeasureSpec)
            }
            Orientation.VERTICAL -> {
                measureVertical(widthMeasureSpec, heightMeasureSpec)
            }
        }
    }

    private fun measureHorizontal(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(0, 0)
            return
        }
    }

    private fun getChildMeasureSize(index: Int): Size {
        val child = getChildAt(index)
        if (child is ImageView) {
            val drawable = child.drawable
            if (drawable is BitmapDrawable) {
                val childWidth = drawable.bitmap.width
                val childHeight = drawable.bitmap.height
                if (childWidth > 0 && childHeight > 0) {
                    return Size(childWidth, childHeight)
                }
            }
        }
        child.measure(-1, -1)
        return Size(child.measuredWidth, child.measuredHeight)
    }

    private fun measureVertical(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(0, 0)
            return
        }

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        TODO("Not yet implemented")
    }

    enum class Orientation {
        HORIZONTAL,
        VERTICAL
    }

}
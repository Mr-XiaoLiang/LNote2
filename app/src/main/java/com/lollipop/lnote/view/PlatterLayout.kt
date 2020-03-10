package com.lollipop.lnote.view

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Size
import android.view.View
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
        if (childCount < 1) {
            setMeasuredDimension(0, 0)
            return
        }
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
        // 宽度不为空，那么认为宽度固定，优先使用宽度为基准
        val childSizeArray = getChildMeasureSize()
        if (widthMode != MeasureSpec.UNSPECIFIED) {
            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
            val heightWeight = getWeightByHeight(childSizeArray)
            val totalWidth = getTotalWidth(childSizeArray, heightWeight)
            var maxHeight = 0
            for (index in childSizeArray.indices) {
                val size = childSizeArray[index]
                val childWidth = (size.width * heightWeight[index] / totalWidth * widthSize).toInt()
                val childHeight = getHeightByWidth(size, childWidth)
                if (childHeight > maxHeight) {
                    maxHeight = childHeight
                }
                getChildAt(index).measure(
                    MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY))
            }
            setMeasuredDimension(widthSize, maxHeight)
        } else {

        }
    }

    private fun measureVertical(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(0, 0)
            return
        }

    }

    private fun getTotalWidth(sizeArray: Array<Size>, heightWeight: FloatArray): Int {
        var widthSize = 0
        for (index in sizeArray.indices) {
            widthSize += (sizeArray[index].width * heightWeight[index]).toInt()
        }
        return widthSize
    }

    private fun getTotalHeight(sizeArray: Array<Size>, widthWeight: FloatArray): Int {
        var heightSize = 0
        for (index in sizeArray.indices) {
            heightSize += (sizeArray[index].height * widthWeight[index]).toInt()
        }
        return heightSize
    }

    private fun getWidthByHeight(size: Size, height: Int): Int {
        return (height * 1F / size.height * size.width).toInt()
    }

    private fun getHeightByWidth(size: Size, width: Int): Int {
        return (width * 1F / size.width * size.height).toInt()
    }

    private fun getWeightByHeight(sizeArray: Array<Size>): FloatArray {
        val result = FloatArray(sizeArray.size)
        if (sizeArray.isEmpty()) {
            return result
        }

        var example = 0
        for (index in sizeArray.indices) {
            val size = sizeArray[index]
            if (size.width > 0 && size.height > 0) {
                if (example == 0) {
                    example = size.height
                    result[index] = 1F
                } else {
                    result[index] = size.height.toFloat() / example
                }
            } else {
                result[index] = 0F
            }
        }
        return result
    }

    private fun getWeightByWidth(sizeArray: Array<Size>): FloatArray {
        val result = FloatArray(sizeArray.size)
        if (sizeArray.isEmpty()) {
            return result
        }

        var example = 0
        for (index in sizeArray.indices) {
            val size = sizeArray[index]
            if (size.width > 0 && size.height > 0) {
                if (example == 0) {
                    example = size.width
                    result[index] = 1F
                } else {
                    result[index] = size.width.toFloat() / example
                }
            } else {
                result[index] = 0F
            }
        }
        return result
    }

    private fun getChildMeasureSize(): Array<Size> {
        return Array(childCount) { getChildMeasureSize(it) }
    }

    private fun getChildMeasureSize(index: Int): Size {
        val child = getChildAt(index)
        if (child.visibility == View.GONE) {
            return Size(0, 0)
        }
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

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        TODO("Not yet implemented")
    }

    enum class Orientation {
        HORIZONTAL,
        VERTICAL
    }

}
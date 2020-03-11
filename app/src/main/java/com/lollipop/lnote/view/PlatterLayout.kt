package com.lollipop.lnote.view

import android.content.Context
import android.graphics.Rect
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

    private val boundList = ArrayList<Rect>()

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

    private fun getBound(index: Int): Rect {
        if (index < 0) {
            return Rect()
        }
        while (boundList.size < index) {
            boundList.add(Rect())
        }
        return boundList[index]
    }

    private fun measureHorizontal(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(0, 0)
            return
        }
        val childSizeArray = getChildMeasureSize()
        // 宽度不为空，那么认为宽度固定，优先使用宽度为基准
        if (widthMode != MeasureSpec.UNSPECIFIED) {
            // 得到可用宽度
            val widthSize = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
            // 得到高度之间的比例关系
            val heightWeight = getWeightByHeight(childSizeArray)
            // 获取同高度下的宽度
            val totalWidth = getTotalWidth(childSizeArray, heightWeight)
            // 最大高度值
            var maxHeight = 0
            // 可用高度值
            val heightSize = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom
            // child的左侧位置
            var left = paddingLeft
            // child的顶部位置
            val top = paddingTop
            // 开始测量和记录每个child的位置
            for (index in childSizeArray.indices) {
                // 得到child的自身尺寸
                val size = childSizeArray[index]
                // 按照缩放比例计算child的实际宽度
                var childWidth = (size.width * heightWeight[index] / totalWidth * widthSize).toInt()
                // 根据实际宽度，计算等比的高度
                var childHeight = getHeightByWidth(size, childWidth)
                // 如果高度值有限制，那么限制测量结果
                if (heightMode != MeasureSpec.UNSPECIFIED && childHeight > heightSize) {
                    val weight = heightSize * 1F / childHeight
                    childHeight = childHeight.resize(weight)
                    childWidth = childWidth.resize(weight)
                }
                // 如果有新的高度值，那么记录下来
                if (childHeight > maxHeight) {
                    maxHeight = childHeight
                }
                // 将测量结果通知child
                getChildAt(index).measure(
                    MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY))
                // 记录位置信息
                getBound(index).set(left, top, left + childWidth, top + childHeight)
                // 累加X轴偏移量
                left += childWidth
            }
            // 完成测量, 提交数据时，带上前面减去的padding
            setMeasuredDimension(widthSize + paddingLeft + paddingRight,
                maxHeight + paddingTop + paddingBottom)
        } else {
            // 认为是固定高度不固定宽度的场景，此时直接按顺序排下去
            val heightSize = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom
            // X轴的坐标
            var left = paddingLeft
            val top = paddingTop
            // 为每个child计算尺寸
            for (index in childSizeArray.indices) {
                // 得到child的自身尺寸
                val size = childSizeArray[index]
                // 固定的高度，无限制的宽度，因此直接拿宽度即可
                val childWidth = getWidthByHeight(size, heightSize)
                // 将测量结果通知child
                getChildAt(index).measure(
                    MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY))
                // 记录位置信息
                getBound(index).set(left, top, left + childWidth, top + heightSize)
                // 累加X轴偏移量
                left += childWidth
            }
            // 完成测量, 提交数据时，带上前面减去的padding，由于left完成了最后的累加，因此只需要补上右侧的padding即可
            setMeasuredDimension(left + paddingRight,
                heightSize + paddingTop + paddingBottom)
        }
    }

    private fun measureVertical(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(0, 0)
            return
        }
        val childSizeArray = getChildMeasureSize()
        // 高度如果不为空，那么优先使用高度
        if (heightMode != MeasureSpec.UNSPECIFIED) {
            // 获取可用高度
            val heightSize = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom
            // 获取宽度之间的比例
            val widthWight = getWeightByWidth(childSizeArray)
            // 获取理论总高度
            val totalHeight = getTotalHeight(childSizeArray, widthWight)
            // 最大的宽度值
            var maxWidth = 0
            // 获取宽度尺寸
            val widthSize = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
            // child的顶部位置
            var top = paddingTop
            // child的左侧位置
            val left = paddingLeft
            // 开始测量和记录每个child的位置
            for (index in childSizeArray.indices) {
                // 得到child的自身尺寸
                val size = childSizeArray[index]
                // 安装比例计算真实高度
                var childHeight = (size.height * widthWight[index] / totalHeight * heightSize).toInt()
                // 得到同比例的宽度
                var childWidth = getWidthByHeight(size, childHeight)
                // 如果宽度值有限制，那么调整结果
                if (widthMode != MeasureSpec.UNSPECIFIED && childWidth > widthSize) {
                    val weight = widthSize * 1F / childWidth
                    childHeight = childHeight.resize(weight)
                    childWidth = childWidth.resize(weight)
                }
                // 如果有新的宽度值，那么记录下来
                if (childWidth > maxWidth) {
                    maxWidth = childWidth
                }
                // 将测量结果通知child
                getChildAt(index).measure(
                    MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY))
                // 记录位置信息
                getBound(index).set(left, top, left + childWidth, top + childHeight)
                // 累加X轴偏移量
                top += childHeight
            }
            // 完成测量, 提交数据时，带上前面减去的padding
            setMeasuredDimension(maxWidth + paddingLeft + paddingRight,
                heightSize + paddingTop + paddingBottom)
        } else {
            // 认为是固定宽度不固定高度的场景，此时按照顺序直接排下去
            val widthSize = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
            // Y轴坐标
            var top = paddingTop
            val left = paddingLeft
            for (index in childSizeArray.indices) {
                // 得到child的自身尺寸
                val size = childSizeArray[index]
                // 固定的宽度，无限制的高度，直接拿高度
                val childHeight = getHeightByWidth(size, widthSize)
                // 将测量结果通知child
                getChildAt(index).measure(
                    MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY))
                // 记录位置信息
                getBound(index).set(left, top, left + childHeight, top + widthSize)
                // 累加X轴偏移量
                top += childHeight
            }
            // 完成测量, 提交数据时，带上前面减去的padding，由于top完成了最后的累加，因此只需要补上顶部的padding即可
            setMeasuredDimension(widthSize + paddingLeft + paddingRight,
                top + paddingBottom)
        }
    }

    private fun Int.resize(weight: Float): Int {
        return (this * weight).toInt()
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
        val count = childCount
        if (count == 0) {
            return
        }
        for (index in 0 until count) {
            val bounds = getBound(index)
            getChildAt(index).layout(bounds.left, bounds.top, bounds.right, bounds.bottom)
        }
    }

    enum class Orientation {
        HORIZONTAL,
        VERTICAL
    }

}
package com.lollipop.skin

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur

/**
 * @author lollipop
 * @date 2020/2/26 23:34
 * 高斯模糊的工具类
 */
object BlurUtil {

    fun blurBitmap(context: Context, src: Bitmap, out: Bitmap, radius: Float = 25F) {
        // 创建RenderScript内核对象
        val rs = RenderScript.create(context)
        // 创建一个模糊效果的RenderScript的工具对象
        val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间
        // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去
        val allIn = Allocation.createFromBitmap(rs, src)
        val allOut = Allocation.createFromBitmap(rs, out)
        // 设置blurScript对象的输入内存
        blurScript.setRadius(radius.range(1F, 25F))
        // 将数据流写入，并且读出处理好的数据流
        blurScript.setInput(allIn)
        blurScript.forEach(allOut)
        // 拷贝到输出位置
        allOut.copyTo(out)
        // 最后，销毁它
        rs.destroy()
    }

    private fun Float.range(min: Float, max: Float): Float {
        if (this < min) {
            return min
        }
        if (this > max) {
            return max
        }
        return this
    }

}
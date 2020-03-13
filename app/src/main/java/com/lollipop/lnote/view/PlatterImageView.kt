package com.lollipop.lnote.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * @author lollipop
 * @date 2020/3/13 21:42
 * 拼盘中庸的图片View，它的目的在于更换图片时发起一次排版
 */
class PlatterImageView(context: Context, attrs: AttributeSet?, defStyleAttr: Int):
    AppCompatImageView(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context): this(context, null)

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        requestLayout()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        requestLayout()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        requestLayout()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        requestLayout()
    }

    override fun setImageIcon(icon: Icon?) {
        super.setImageIcon(icon)
        requestLayout()
    }

}
package com.lollipop.lnote.activity

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.lnote.R
import com.lollipop.lnote.skin.NoteSkin
import com.lollipop.lnote.util.log
import com.lollipop.lnote.util.stackTrace
import com.lollipop.skin.SkinProvider
import kotlinx.android.synthetic.main.activity_base.*


/**
 * @author lollipop
 * @date 2020-02-24 00:07
 * 基础的Activity
 */
abstract class BaseActivity: AppCompatActivity(), SkinProvider<NoteSkin> {

    companion object {
        private const val DEF_LAYOUT = R.layout.activity_base
    }

    /**
     * 悬浮层的View的ID
     */
    protected abstract val floatingViewId: Int

    /**
     * 内容层的View的ID
     */
    protected abstract val contentViewId: Int

    /**
     * 内容框架的View的ID
     */
    protected var layoutId = DEF_LAYOUT

    private val isDefLayout: Boolean
        get() {
            return layoutId == DEF_LAYOUT
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isDefLayout) {
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        super.onCreate(savedInstanceState)
        setContentView(if (layoutId == 0) { DEF_LAYOUT } else { layoutId })
        initScaffold()
    }

    /**
     * 初始化脚手架
     */
    private fun initScaffold() {
        if (isDefLayout) {
            if (contentViewId != 0) {
                layoutInflater.inflate(contentViewId, contentGroup, true)
            }
            if (floatingViewId != 0) {
                layoutInflater.inflate(floatingViewId, floatingGroup, true)
            }
            setSupportActionBar(toolbar)
            initInsetCallback(rootGroup)
        }
    }

    protected fun initInsetCallback(group: View) {
        group.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        group.fitsSystemWindows = true
        group.setOnApplyWindowInsetsListener { _, insets ->
            onInsetsChange(insets.systemWindowInsetLeft, insets.systemWindowInsetTop,
                insets.systemWindowInsetRight, insets.systemWindowInsetBottom)
            insets.consumeSystemWindowInsets()
        }
    }

    protected fun onInsetsChange(left: Int, top: Int, right: Int, bottom: Int) {
        if (isDefLayout) {
            log("onInsetsChange($left, $top, $right, $bottom)")
//            toolbar.layoutParams = (toolbar.layoutParams as
//                    CollapsingToolbarLayout.LayoutParams).apply {
//                setMargins(left, top, right, 0)
//            }
//            toolbar.layoutParams?.let {
//                it as CollapsingToolbarLayout.LayoutParams
//                log("toolbar.margin=(${it.leftMargin}, ${it.topMargin}, ${it.rightMargin}, ${it.bottomMargin})")
//            }
            Exception("调用栈").printStackTrace()
            toolbar.setPadding(left, top, right, 0)
            contentGroup.setPadding(left, 0, right, bottom)
            floatingGroup.setPadding(left, top, right, bottom)
        }
    }

}
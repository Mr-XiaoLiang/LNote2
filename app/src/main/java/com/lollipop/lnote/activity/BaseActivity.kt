package com.lollipop.lnote.activity

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.lnote.R
import com.lollipop.lnote.skin.NoteSkin
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

    override fun onCreate(savedInstanceState: Bundle?) {
        if (layoutId == DEF_LAYOUT) {
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
        if (layoutId == DEF_LAYOUT) {
            if (contentViewId != 0) {
                layoutInflater.inflate(contentViewId, contentGroup, true)
            }
            if (floatingViewId != 0) {
                layoutInflater.inflate(floatingViewId, floatingGroup, true)
            }
            initInsetCallback(rootGroup)
        }
    }

    protected fun initInsetCallback(group: View) {
        group.fitsSystemWindows = true
        group.setOnApplyWindowInsetsListener { v, insets ->
            insets.consumeStableInsets()
        }
    }

}
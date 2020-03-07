package com.lollipop.lnote.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import com.lollipop.lnote.R
import com.lollipop.lnote.util.lifecycleBinding
import com.lollipop.lnote.util.onEnd
import com.lollipop.lnote.util.onStart

/**
 * @author lollipop
 * @date 2020/3/7 13:16
 * 顶部的Dialog面板
 */
abstract class TopPanelDialog(
    private val selfRegistration: SelfRegistration,
    private val group: ViewGroup): AdaptiveInset {

    protected val context: Context = group.context
    private val duration = 250L
    private val interpolator: AccelerateInterpolator by lazy {
        AccelerateInterpolator(0.8F)
    }
    protected abstract val layoutId: Int
    protected abstract val onceDialog: Boolean
    private var panelView: View? = null
    protected var contentView: View? = null
        private set
    private var backgroundView: View? = null

    private val showTask = Runnable {
        contentView?.let { panel ->
            panel.translationY = panel.height * -1F
            val animator = panel.animate()
            animator.cancel()
            animator.duration = duration
            animator.interpolator = interpolator
            animator.translationY(0F)
            animator.start()
        }
        backgroundView?.let { panel ->
            panel.alpha = 0F
            val animator = panel.animate()
            animator.cancel()
            animator.duration = duration
            animator.interpolator = interpolator
            animator.alpha(1F)
            animator.lifecycleBinding {
                onStart {
                    removeThis(it)
                    if (panelView?.visibility != View.VISIBLE) {
                        panelView?.visibility = View.VISIBLE
                    }
                }
            }
            animator.start()
        }
    }

    private val hideTask = Runnable {
        contentView?.let { panel ->
            val animator = panel.animate()
            animator.cancel()
            animator.duration = duration
            animator.interpolator = interpolator
            animator.translationY(panel.height * -1F)
            animator.start()
        }
        backgroundView?.let { panel ->
            val animator = panel.animate()
            animator.cancel()
            animator.duration = duration
            animator.interpolator = interpolator
            animator.alpha(0F)
            animator.lifecycleBinding {
                onEnd {
                    removeThis(it)
                    if (onceDialog) {
                        group.removeView(panelView)
                        panelView = null
                        contentView = null
                        backgroundView = null
                    } else {
                        panelView?.visibility = View.INVISIBLE
                    }
                }
            }
            animator.start()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        if (panelView == null) {
            // 创建面板容器
            val panel = FrameLayout(context)
            panelView = panel
            panel.setOnTouchListener { _, _ ->
                true
            }
            panel.visibility = View.INVISIBLE
            // 创建背景的View
            val background = View(context)
            backgroundView = background
            background.setBackgroundResource(R.color.topPanelBackground)
            // 构建内容View
            val content = LayoutInflater.from(context).inflate(layoutId, group, false)
            contentView = content
            // 添加背景
            panel.addView(background, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            // 将内容的View加入容器
            panel.addView(content)
            // 注册面板
            TopPanelSelfRegistration(panel, selfRegistration, this)
            // 将容器添加到View树中
            group.addView(panel, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            // 通知View初始化
            onViewCreate(content)
        }
        onReady()
    }

    protected fun doShow() {
        init()
        onShown()
        panelView?.apply {
            removeCallbacks(hideTask)
            removeCallbacks(showTask)
            post(showTask)
        }
    }

    protected fun doHide() {
        onHide()
        panelView?.apply {
            removeCallbacks(hideTask)
            removeCallbacks(showTask)
            post(hideTask)
        }
    }

    protected open fun onReady() {  }

    protected open fun onViewCreate(contentView: View) {  }

    protected inline fun <reified T: View> find(id: Int): T? {
        return contentView?.findViewById(id)
    }

    protected inline fun <reified T: View> tryView(id: Int, run: (T) -> Unit) {
        find<T>(id)?.let(run)
    }

    protected open fun onShown() {  }

    protected open fun onHide() {  }

    private class TopPanelSelfRegistration(
        private val view: View,
        private val selfRegistration: SelfRegistration,
        private val inset: AdaptiveInset):
        View.OnAttachStateChangeListener {

        init {
            view.addOnAttachStateChangeListener(this)
        }

        override fun onViewDetachedFromWindow(v: View?) {
            if (v == view) {
                selfRegistration.unregister(inset)
            }
        }

        override fun onViewAttachedToWindow(v: View?) {
            if (v == view) {
                selfRegistration.register(inset)
            }
        }

    }

    interface SelfRegistration {
        fun register(inset: AdaptiveInset)
        fun unregister(inset: AdaptiveInset)
    }

}
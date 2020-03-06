package com.lollipop.lnote.util

import android.content.res.ColorStateList
import android.graphics.PointF
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.behavior.SwipeDismissBehavior
import com.google.android.material.button.MaterialButton
import com.lollipop.lnote.R
import kotlin.math.abs

/**
 * @author lollipop
 * @date 2020/3/4 01:00
 * 消息的辅助类
 */
class NotificationHelper(group: ViewGroup): View.OnClickListener, View.OnAttachStateChangeListener {

    private val context = group.context
    private val panelView: View
    private val notificationIcon: ImageView
    private val notificationContent: TextView
    private val actionBtn: MaterialButton

    private var onClickListener: (() -> Unit)? = null
    private var onDismissListener: ((DismissType) -> Unit)? = null

    private val timeOut = 25 * 100L
    private val messageHandler: Handler by lazy {
        Handler()
    }
    private var pendingInfo: Info? = null
    private var isShown = false

    private val touchDownPoint = PointF()
    private var touchPointId = 0

    private val notificationIconTint: ColorStateList by lazy {
        ColorStateList.valueOf(context.compatColor(R.color.topNotificationIcon))
    }
    private val alertIconTint: ColorStateList by lazy {
        ColorStateList.valueOf(context.compatColor(R.color.topAlertIcon))
    }

    init {
        panelView = LayoutInflater.from(context).inflate(
            R.layout.fragment_notification, group, false)
        group.addView(panelView)
        notificationIcon = panelView.findViewById(R.id.notificationIcon)
        notificationContent = panelView.findViewById(R.id.notificationContent)
        actionBtn = panelView.findViewById(R.id.actionBtn)
        initView()
    }

    private val showTask = Runnable {
        panelView.translationX = 0F
        panelView.translationY = panelView.height * -1F
        val animator = panelView.animate()
        animator.cancel()
        animator.translationY(0F)
        animator.lifecycleBinding {
            onStart {
                removeThis(it)
                if (panelView.visibility != View.VISIBLE) {
                    panelView.visibility = View.VISIBLE
                }
            }
        }
        animator.start()
    }

    private val hideTask = Runnable {
        val animator = panelView.animate()
        animator.cancel()
        animator.translationY(panelView.height * -1F)
        animator.lifecycleBinding {
            onEnd {
                removeThis(it)
                if (!showNext()) {
                    panelView.visibility = View.INVISIBLE
                }
            }
        }
        animator.start()
    }

    private val timeoutTask = Runnable {
        doDismiss(DismissType.TimeOut)
    }

    private fun initView() {
        panelView.visibility = View.INVISIBLE
        actionBtn.setOnClickListener(this)
        panelView.addOnAttachStateChangeListener(this)
        panelView.setOnTouchListener { _, event ->
            // 拦截所有触摸事件，避免触摸穿透的同时，增加手指滑动移除
//            onPanelTouch(event)
            true
        }
        setUpBehavior()
    }

    private fun onPanelTouch(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pauseTimeout()
                touchPointId = event.getPointerId(0)
                touchDownPoint.set(event.getXByPoint(), event.getYByPoint())
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val index = event.findPointerIndex(touchPointId)
                if (index < 0) {
                    touchPointId = event.getPointerId(0)
                    touchDownPoint.set(event.getXByPoint(), event.getYByPoint())
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event.getXByPoint()
                val y = event.getYByPoint()
                if (y >= 0 || y <= panelView.height) {
                    panelView.translationX += x - touchDownPoint.x
                }
                touchDownPoint.x = x
                log("event.x=${event.getXByPoint()}, touchDownPoint.x=${touchDownPoint.x}")
            }
            MotionEvent.ACTION_UP -> {
                checkSwipe()
            }
            MotionEvent.ACTION_CANCEL -> {
                swipeOut(0)
            }
        }
    }

    private fun setUpBehavior() {
        val layoutParams = panelView.layoutParams
        if (layoutParams !is CoordinatorLayout.LayoutParams) {
            return
        }
        val behavior = Behavior { isDrag ->
            if (isDrag) {
                pauseTimeout()
            } else {
                restoreTimeout()
            }
        }

        behavior.listener = object : SwipeDismissBehavior.OnDismissListener {
            override fun onDismiss(view: View) {
                doDismiss(DismissType.Remove)
            }

            override fun onDragStateChanged(state: Int) {
                when (state) {
                    SwipeDismissBehavior.STATE_DRAGGING, SwipeDismissBehavior.STATE_SETTLING -> {
                        // If the view is being dragged or settling, pause the timeout
                        pauseTimeout()
                    }
                    SwipeDismissBehavior.STATE_IDLE -> {
                        // If the view has been released and is idle, restore the timeout
                        restoreTimeout()
                    }
                    else -> {
                    }
                }
            }
        }
        layoutParams.behavior = behavior
    }

    private fun checkSwipe() {
        val offset = panelView.translationX.toInt()
        swipeOut(if (abs(offset) < panelView.width / 2) { 0 } else { offset % 2 })
    }

    private fun swipeOut(direction: Int) {
        panelView.animate().let { animator ->
            animator.cancel()
            animator.translationX(direction * panelView.width * 1F)
                .lifecycleBinding {
                onEnd {
                    removeThis(it)
                    if (direction == 0) {
                        restoreTimeout()
                    } else {
                        doDismiss(DismissType.Remove)
                    }
                }
            }.start()
        }
    }

    private fun MotionEvent.getXByPoint(): Float {
        val index = findPointerIndex(touchPointId)
        if (index < 0) {
            return 0F
        }
        return getX(index)
    }

    private fun MotionEvent.getYByPoint(): Float {
        val index = findPointerIndex(touchPointId)
        if (index < 0) {
            return 0F
        }
        return getY(index)
    }

    fun onInsetChange(left: Int, top: Int, right: Int) {
        panelView.setPadding(left, top, right, 0)
    }

    fun notify(value: CharSequence, icon: Int = 0, action: CharSequence = "",
               onClick: (() -> Unit)? = null,
               onDismiss: ((DismissType) -> Unit)? = null) {
        showWith(false, value, icon, action, onClick, onDismiss)
    }

    fun alert(value: CharSequence, icon: Int = 0, action: CharSequence = "",
              onClick: (() -> Unit)? = null,
              onDismiss: ((DismissType) -> Unit)? = null) {
        showWith(true, value, icon, action, onClick, onDismiss)
    }

    private fun showWith(isAlert: Boolean,
                         value: CharSequence, icon: Int, action: CharSequence,
                         onClick: (() -> Unit)?,
                         onDismiss: ((DismissType) -> Unit)?) {
        if (isShown) {
            pendingInfo = Info(value, icon, action, onClick, onDismiss, isAlert)
            doDismiss(DismissType.Replace)
            return
        }
        if (isAlert) {
            updateByAlert()
        } else {
            updateByNotification()
        }
        updateValue(value, icon, action, onClick, onDismiss)
        doShow()
    }

    private fun showNext(): Boolean {
        val info = pendingInfo
        pendingInfo = null
        info?:return false
        showWith(info.isAlert, info.value, info.icon, info.action, info.onClick, info.onDismiss)
        return true
    }

    private fun pauseTimeout() {
        messageHandler.removeCallbacks(timeoutTask)
    }

    private fun restoreTimeout() {
        messageHandler.postDelayed(timeoutTask, timeOut)
    }

    private fun updateValue(value: CharSequence, icon: Int = 0, action: CharSequence = "",
                            onClick: (() -> Unit)? = null,
                            onDismiss: ((DismissType) -> Unit)? = null) {
        notificationContent.text = value
        if (icon != 0) {
            notificationIcon.visibility = View.VISIBLE
            notificationIcon.setImageResource(icon)
        } else {
            notificationIcon.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(action)) {
            actionBtn.visibility = View.VISIBLE
            actionBtn.text = action
            onClickListener = onClick
        } else {
            actionBtn.visibility = View.GONE
            onClickListener = null
        }
        onDismissListener = onDismiss
    }

    private fun updateByNotification() {
        panelView.setBackgroundColor(context.compatColor(R.color.topNotificationBackground))
        notificationContent.setTextColor(context.compatColor(R.color.topNotificationContent))
        notificationIcon.imageTintList = notificationIconTint
        actionBtn.setTextColor(context.compatColor(R.color.topNotificationAction))
    }

    private fun updateByAlert() {
        panelView.setBackgroundColor(context.compatColor(R.color.topAlertBackground))
        notificationContent.setTextColor(context.compatColor(R.color.topAlertContent))
        notificationIcon.imageTintList = alertIconTint
        actionBtn.setTextColor(context.compatColor(R.color.topAlertAction))
    }

    private fun onDismiss(dismissType: DismissType) {
        isShown = false
        if (dismissType == DismissType.Action) {
            onClickListener?.invoke()
        }
        onClickListener = null
        onDismissListener?.invoke(dismissType)
        onDismissListener = null
    }

    private fun doDismiss(dismissType: DismissType) {
        pauseTimeout()
        onDismiss(dismissType)
        panelView.removeCallbacks(hideTask)
        panelView.removeCallbacks(showTask)
        if (dismissType == DismissType.Remove) {
            panelView.left = 0
            panelView.alpha = 1F
            panelView.visibility = View.INVISIBLE
            return
        }
        if (dismissType == DismissType.Detached) {
            return
        }
        panelView.post(hideTask)
    }

    private fun doShow() {
        if (isShown) {
            doDismiss(DismissType.Replace)
        }
        restoreTimeout()
        isShown = true
        panelView.removeCallbacks(hideTask)
        panelView.removeCallbacks(showTask)
        panelView.post(showTask)
    }

    override fun onViewDetachedFromWindow(v: View?) {
        doDismiss(DismissType.Detached)
    }

    override fun onViewAttachedToWindow(v: View?) {  }

    override fun onClick(v: View?) {
        doDismiss(DismissType.Action)
    }

    enum class DismissType {
        /**
         * 超时
         */
        TimeOut,
        /**
         * 被点击
         */
        Action,
        /**
         * 被移除
         */
        Remove,
        /**
         * View被销毁
         */
        Detached,
        /**
         * 被顶替
         */
        Replace
    }

    private data class Info(val value: CharSequence, val icon: Int, val action: CharSequence,
        val onClick: (() -> Unit)?, val onDismiss: ((DismissType) -> Unit)?, val isAlert: Boolean)

    private class Behavior(callback: (isDrag: Boolean) -> Unit) : SwipeDismissBehavior<View>() {
        private val delegate = BehaviorDelegate(this, callback)

        override fun canSwipeDismissView(child: View): Boolean {
            return true
        }

        override fun onInterceptTouchEvent(parent: CoordinatorLayout,
                                           child: View, event: MotionEvent): Boolean {
            delegate.onInterceptTouchEvent(parent, child, event)
            return super.onInterceptTouchEvent(parent, child, event)
        }
    }

    private class BehaviorDelegate(behavior: SwipeDismissBehavior<*>,
                                   private val managerCallback: (isDrag: Boolean) -> Unit) {

        fun onInterceptTouchEvent(parent: CoordinatorLayout, child: View, event: MotionEvent) {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN ->
                    if (parent.isPointInChildBounds(child, event.x.toInt(), event.y.toInt())) {
                        managerCallback.invoke(true)
                    }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    managerCallback.invoke(false)
                }
                else -> {
                }
            }
        }

        init {
            behavior.setStartAlphaSwipeDistance(0.1f)
            behavior.setEndAlphaSwipeDistance(0.6f)
            behavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_ANY)
        }
    }

}